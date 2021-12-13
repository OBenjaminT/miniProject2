package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Path;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.actions.Actable;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * TODO
 */
abstract public class Unit extends ICWarsActor {

    // data
    /**
     * The {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit}'s current health points. When
     * this is less than {@code 0} the unit is dead.
     */
    protected int current_HP;

    /**
     * The maximum health points of the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int) Unit}.
     */
    protected int maxHP;

    /**
     * TODO
     */
    protected int repair;

    /**
     * TODO
     */
    protected int radius;

    /**
     * If this is {@code true} then the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit} has
     * already been moved this turn.
     */
    protected boolean hasAlreadyMoved;

    /**
     * TODO
     */
    protected ArrayList<Actable> actions; // List of actions the unit can take

    /**
     * The name of the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit}.
     */
    protected String name;

    // ui
    /**
     * The {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit}'s visual representation in the
     * game.
     */
    protected Sprite sprite;

    /**
     * TODO
     */
    ICWarsRange range;

    /**
     * TODO
     */
    private int numberOfStarsOfCurrentCell;

    /**
     * TODO
     *
     * @param area       the area in which the unit is
     * @param position   position of the unit in the area
     * @param faction    faction to which the units belong (eiter ALLY or ENEMY
     * @param repair     the amount that the unit can increase its HP
     * @param radius     the maximum distance for moving
     * @param current_HP the number of HP a unit has
     * @param maxHP      the maximum HP the unit has
     */
    public Unit(Area area,
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
        this.hasAlreadyMoved = false; // at its creation a unit hasn't already been moved
        completeUnitsRange();
    }

    /**
     * TODO
     *
     * @return
     */
    protected abstract int getDamage();

    /**
     * TODO
     * <p>
     * if the HP are given negative, they are set to 0
     * if they are given above maxHP, they are set to maxHP
     * else they are set to the given @param hp
     *
     * @param HP the hp the unit has
     */
    public void setHp(int HP) {
        this.current_HP = HP < 0 ? 0 : Math.min(HP, maxHP);
    }

    /**
     * TODO
     *
     * @param isAlreadyMoved
     */
    public void setIsAlreadyMoved(boolean isAlreadyMoved) {
        this.hasAlreadyMoved = isAlreadyMoved;
    }

    /**
     * Tells you if the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit} has not moved this
     * turn.
     *
     * @return {@code true} if the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit} hasn't
     * moved on this turn yet, {@code false} if it has.
     */
    public boolean hasNotAlreadyMoved() {
        return !hasAlreadyMoved;
    }

    /**
     * Tells you if the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit} is alive.
     *
     * @return {@code true} if the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit}'s HP is
     * positive.
     */
    public boolean isAlive() {
        return this.current_HP > 0;
    }

    /**
     * Tells you if the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit} is dead.
     * <p>
     * Is the boolean opposite of {@link #isAlive()}.
     *
     * @return {@code true} if the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit}'s HP
     * isn't positive.
     */
    public boolean isDead() {
        return !isAlive();
    }

    /**
     * TODO
     *
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * Tells you the name of the {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit}.
     *
     * @return The {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int)  Unit}'s name.
     */
    protected String getName() {
        return this.name;
    }

    /**
     * TODO
     * <p>
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
     * TODO
     * <p>
     * a unit doesn't take spaceCellSpace
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * TODO
     * <p>
     * a unit is not ViewInteractable
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * TODO
     * <p>
     * a unit is CellInteractable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }

    /**
     * TODO
     * <p>
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
     * TODO
     *
     * @return the indexes of attackable units in the area's list of units
     */
    public ArrayList<Integer> getIndexOfAttackableEnemies() {
        return this.getOwnerArea().getIndexOfAttackableEnemies(this.faction, this.range);
    }

    /**
     * TODO
     *
     * @param indexOfUnitToAttack the index of the unit in the areas' units list that should be attacked
     */
    public void attack(int indexOfUnitToAttack) {
        this.getOwnerArea().attack(
            indexOfUnitToAttack,
            this.getDamage(),
            getNumberOfStarsOfCurrentCell()
        );
    }

    /**
     * TODO
     *
     * @return the list of available actions for the unit (used by the player when he wants to use the unit)
     */
    protected ArrayList<Actable> getAvailableActions() {
        return this.actions;
    }

    /**
     * TODO
     *
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

    /**
     * TODO
     *
     * @param v (AreaInteractionVisitor) : the visitor
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        ((ICWarsInteractionVisitor) v).interactWith(this);
    }

    /**
     * TODO
     *
     * @return
     */
    public int getNumberOfStarsOfCurrentCell() {
        return numberOfStarsOfCurrentCell;
    }

    /**
     * TODO
     *
     * @param numberOfStarsOfCurrentCell
     */
    public void setNumberOfStarsOfCurrentCell(int numberOfStarsOfCurrentCell) {
        this.numberOfStarsOfCurrentCell = numberOfStarsOfCurrentCell;
    }

    /**
     * TODO
     *
     * @param receivedDamage the amount of damage that will be deduced from the current hp of the unit
     */
    public void receivesDamage(int receivedDamage) {
        this.setHp(current_HP - receivedDamage);
    }

    /**
     * TODO
     *
     * @param indexOfUnitToAttack will be transmitted to the area and used in the area method
     *                            centerCameraOnTargetedEnemy(int indexOfUnitToAttack)
     */
    public void centerCameraOnTargetedEnemy(int indexOfUnitToAttack) {
        this.getOwnerArea().centerCameraOnTargetedEnemy(indexOfUnitToAttack);
    }

    /**
     * TODO
     */
    private static class ICWarsUnitInteractionHandler implements ICWarsInteractionVisitor {
        /**
         * TODO
         */
        Unit unit;

        /**
         * TODO
         *
         * @param unit
         */
        public ICWarsUnitInteractionHandler(Unit unit) {
            this.unit = unit;
        }

        /**
         * TODO
         *
         * @param icWarsCell
         */
        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell icWarsCell) {
            unit.setNumberOfStarsOfCurrentCell(icWarsCell.getNumberOfStars());
        }
    }
}
