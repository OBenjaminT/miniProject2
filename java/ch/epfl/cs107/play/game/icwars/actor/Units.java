package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Path;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.stream.IntStream;

abstract public class Units extends ICWarsActor {

    // data
    protected int current_HP;
    protected int maxHP;
    protected int repair;
    protected int radius;
    protected boolean isAlreadyMoved;
    // ui
    protected String name;
    protected Sprite sprite;
    // path
    ICWarsRange range;

    /**
     * @param area       the area in yhich the unit is
     * @param position   position of the unit in the area
     * @param faction    faction to which the units belong (eiter ALLY or ENNEMY
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
        this.isAlreadyMoved = false; //at its creation a unit hasn't already been moved
        completeUnitsRange();
    }

    protected abstract int getDammage();

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

    public boolean isAlreadyMoved() {
        return isAlreadyMoved;
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
     * a unit is not Viewinteractable
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
}
