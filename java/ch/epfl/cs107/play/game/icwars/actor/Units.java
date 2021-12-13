package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Path;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.actions.Action;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.stream.IntStream;

abstract public class Units extends ICWarsActor {

    // data
    protected int current_HP;
    protected int maxHP;
    protected int repair;
    protected int radius;
    protected boolean isAlreadyMoved;
    protected ArrayList<Action> actions; // List of actions the unit can take
    int numberOfStarsofCurrentCell;
    // ui
    protected String name;
    protected Sprite sprite;
    // path
    ICWarsRange range;

    /**
     * @param area       the area in which the unit is
     * @param position   position of the unit in the area
     * @param faction    faction to which the units belong (eiter ALLY or ENEMY
     * @param repair     the amount that the unit can increase its HP
     * @param radius     the maximum distance for moving
     * @param current_HP the number of HP a unit has
     * @param maxHP      the maximum HP the unit has
     */
    public Units(Area area,
                 DiscreteCoordinates position,
                 Faction faction,
                 int repair,
                 int radius,
                 int current_HP,
                 int maxHP) {
        super(area, position, faction);
        this.maxHP = maxHP;
        this.radius = radius;
        this.setHp(current_HP);
        this.repair = repair;
        this.range = new ICWarsRange();
        this.isAlreadyMoved = false; // at its creation a unit hasn't already been moved
        completeUnitsRange();
    }

    protected abstract int getDamage();

    /**
     * if the HP are given negative, they are set to 0
     * if they are given above maxHP, they are set to maxHP
     * else they are set to the given @param hp
     *
     * @param HP the hp the unit has
     */
    public void setHp(int HP) {
        this.current_HP = HP < 0 ? 0 : Math.min(HP, maxHP);
    }

    public void setIsAlreadyMoved(boolean isAlreadyMoved) {
        this.isAlreadyMoved = isAlreadyMoved;
    }

    public boolean hasNotAlreadyMoved() {
        return !isAlreadyMoved;
    }

    /**
     * @return true if the unit's hp are positive
     */
    public boolean isAlive() {
        return this.current_HP > 0;
    }

    public boolean isDead() {
        return !isAlive();
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * @return unit name
     */
    protected String getName() {
        return this.name;
    }

    /**
     * fills the unit's range attribute with nodes that are both
     * within a radius range of the unit's coordinates and in the grid
     */
    private void completeUnitsRange() {
        this.range = new ICWarsRange();
        int widthIndex = this.getOwnerArea().getWidth() - 1;
        int heightIndex = this.getOwnerArea().getHeight() - 1;
        IntStream.rangeClosed(-radius, radius)
            .map(x -> x + this.getCurrentMainCellCoordinates().x)
            .filter(x -> x <= heightIndex)
            .filter(x -> x >= 0)
            .forEach(x -> IntStream.rangeClosed(-radius, radius)
                .map(y -> y + this.getCurrentMainCellCoordinates().y)
                .filter(y -> y <= widthIndex)
                .filter(y -> y >= 0)
                .forEach(y -> range.addNode(
                    new DiscreteCoordinates(x, y), // NodeCoordinates
                    x > 0, // hasLeftNeighbour
                    y > 0, // hasTopNeighbour
                    x < widthIndex, // hasRightNeighbour
                    y < heightIndex // hasUnderNeighbour
                )));
    }

    /**
     * a unit doesn't take spaceCellSpace
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * a unit is not ViewInteractable
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * a unit is CellInteractable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * Draw the unit's range and a path from the unit position to
     * destination
     *
     * @param destination path destination
     * @param canvas      canvas
     */
    public void drawRangeAndPathTo(DiscreteCoordinates destination,
                                   Canvas canvas) {
        range.draw(canvas);
        var path =
            range.shortestPath(getCurrentMainCellCoordinates(), destination);
        // Draw path only if it exists (destination inside the range)
        if (path != null)
            new Path(getCurrentMainCellCoordinates().toVector(), path).draw(canvas);
    }

    /**
     * @return the indexes of attackble units in the area's list of units
     */
    public ArrayList<Integer> getIndexOfAttackableEnnemies() {
        return this.getOwnerArea().getIndexOfAttackableEnnemies(this.faction, this.range);
    }

    /**
     * @param indexOfUnitToAttack the index of the unit in the areas' units list that should be attacked
     */
    public void attack(int indexOfUnitToAttack){
        int dammage = this.getDamage();
        this.getOwnerArea().attack(indexOfUnitToAttack, dammage, numberOfStarsofCurrentCell);
    }

    /**
     * @return the list of available actions for the unit (used by the player when he wants to use the unit)
     */
    protected ArrayList<Action> getAvailableActions(){
        return this.actions;
    }

    /**
     * @param newPosition new unit's position
     * @return true if super.changePosition does so and if a node with newPosition coordinates
     * exists in the units range. If the move is possible, the unit's radius is adapted to the newPosition
     * else return false
     */
    @Override
    public boolean changePosition(DiscreteCoordinates newPosition) {
        if (this.range.nodeExists(newPosition) && super.changePosition(newPosition)) {
            completeUnitsRange();
            return true;
        } else return false;
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ICWarsInteractionVisitor) v).interactWith(this);
    }

    private static class ICWarsUnitInteractionHandler implements ICWarsInteractionVisitor {
        Units unit;

        public ICWarsUnitInteractionHandler(Units unit) {
            this.unit = unit;
        }

        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell icWarsCell) {
            unit.numberOfStarsofCurrentCell = icWarsCell.getNumberOfStars();
        }
    }

    /**
     * @param indexOfUnitToAttack will be transmitted to the area and used in the area method
     * centerCameraOnTargetedEnnemy(int indexOfUnitToAttack)
     */
    public void centerCameraOnTargetedEnnemy(int indexOfUnitToAttack){
        this.getOwnerArea().centerCameraOnTargetedEnnemy(indexOfUnitToAttack);
    }

    /**
     * @param receivedDammage the amount of dammage that will be deduced from the current hp of the unit
     */
    public void receivesDammage(int receivedDammage){
        this.setHp(current_HP-receivedDammage);
    }
}
