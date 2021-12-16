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

/**
 * A Unit is equivalent to a piece on a chess board. It's an entity that can be moved and interacts with other Units.
 */
abstract public class Unit extends ICWarsActor {

    /**
     * The {@link Unit}'s current health points. When this is less than or equal to {@code 0} the {@link Unit} is dead.
     */
    protected int current_HP;

    /**
     * The maximum health points of the {@link Unit}.
     */
    protected int maxHP;

    /**
     * The amount that the {@link Unit}'s {@link #current_HP} recovers at the end of a turn.
     */
    protected int repair;

    /**
     * The cumulative vertical and horizontal distance that the {@link Unit} can move in one turn.
     */
    protected int radius;

    /**
     * The damage this {@link Unit} deals when it {@link #attack(int) attacks}.
     */
    protected int damage;

    /**
     * If this is {@code true} then the {@link Unit} has already been moved this turn.
     */
    protected boolean hasAlreadyMoved;

    /**
     * The list of {@link Action actions} the {@link Unit} can take.
     */
    protected ArrayList<Action> actions;

    /**
     * The name of the {@link Unit}.
     */
    protected String name;

    /**
     * The {@link Unit}'s visual representation in the game.
     */
    protected Sprite sprite;

    /**
     * The collection of tiles that are in the {@link Unit}'s range and that it can move to. See {@link ICWarsRange}.
     */
    ICWarsRange range;

    /**
     * TODO
     */
    private int numberOfStarsOfCurrentCell;

    /**
     * Initialises a Unit class with full health.
     * <p>
     * Calls {@link #Unit(Area, DiscreteCoordinates, Faction, int, int, int, int, int) Unit constructor} with
     * {@link #current_HP} equal to {@link #maxHP}.
     *
     * @param area     The {@link Area} in which the {@link Unit} acts. Passed straight to the
     *                 {@link ICWarsActor#ICWarsActor(Area, DiscreteCoordinates, Faction) ICWarsActor constructor}.
     * @param position The position that the {@link Unit} starts at in the {@link Area}.  Passed straight to the
     *                 {@link ICWarsActor#ICWarsActor(Area, DiscreteCoordinates, Faction) ICWarsActor constructor}.
     * @param faction  The {@link Faction Faction} to which the
     *                 {@link Unit} belongs to. Passed straight to the
     *                 {@link ICWarsActor#ICWarsActor(Area, DiscreteCoordinates, Faction)ICWarsActor constructor}.
     * @param repair   The amount that the {@link Unit}'s {@link #current_HP} recovers at the end of a turn. Passed to
     *                 {@link #repair}.
     * @param radius   The {@link Unit}'s movement. Passed to {@link #radius}.
     * @param maxHP    The maximum health points that the {@link Unit} has. Passed to {@link #maxHP}.
     * @param damage   The damage that this {@link Unit} deals. Passed to {@link #damage}
     */
    public Unit(Area area,
                DiscreteCoordinates position,
                Faction faction,
                int repair,
                int radius,
                int maxHP,
                int damage) {
        this(area, position, faction, repair, radius, maxHP, maxHP, damage);
    }

    /**
     * Initialises a Unit class.
     *
     * @param area       The {@link Area} in which the {@link Unit} acts. Passed straight to the
     *                   {@link ch.epfl.cs107.play.game.icwars.actor.ICWarsActor#ICWarsActor(Area, DiscreteCoordinates, Faction) ICWarsActor constructor}.
     * @param position   The position that the {@link Unit} starts at in the {@link Area}.  Passed straight to the
     *                   {@link ch.epfl.cs107.play.game.icwars.actor.ICWarsActor#ICWarsActor(Area, DiscreteCoordinates, Faction) ICWarsActor constructor}.
     * @param faction    The {@link ch.epfl.cs107.play.game.icwars.actor.ICWarsActor.Faction Faction} to which the
     *                   {@link Unit} belongs to. Passed straight to the
     *                   {@link ch.epfl.cs107.play.game.icwars.actor.ICWarsActor#ICWarsActor(Area, DiscreteCoordinates, Faction)ICWarsActor constructor}.
     * @param repair     The amount that the {@link Unit}'s {@link #current_HP} recovers at the end of a turn. Passed to
     *                   {@link #repair}.
     * @param radius     The {@link Unit}'s movement. Passed to {@link #radius}.
     * @param current_HP The health points the {@link Unit} starts with. Passed to {@link #current_HP}.
     * @param maxHP      The maximum health points that the {@link Unit} has. Passed to {@link #maxHP}.
     * @param damage     The damage that this {@link Unit} deals. Passed to {@link #damage}
     */
    public Unit(Area area,
                DiscreteCoordinates position,
                Faction faction,
                int repair,
                int radius,
                int current_HP,
                int maxHP,
                int damage) {
        // TODO comments

        super(area, position, faction);
        this.repair = repair;
        this.radius = radius;
        this.maxHP = maxHP; // maxHP *must* be set before current_HP
        this.setHp(current_HP);
        this.damage = damage;
        this.range = new ICWarsRange();
        completeUnitsRange();
        this.hasAlreadyMoved = false;
    }

    /**
     * @return This {@link Unit}'s {@link #damage}.
     */
    protected int getDamage() {
        return damage;
    }

    /**
     * Set's the {@link Unit}'s {@link #current_HP} whilst making sure that it's always greater than {@code 0} and not
     * more than {@link #maxHP}.
     *
     * @param HP The new HP value of this {@link Unit} (not necessarily the resulting HP value).
     */
    public void setHp(int HP) {
        this.current_HP = HP < 0 ? 0 : Math.min(HP, maxHP);
    }

    /**
     * @param isAlreadyMoved Sets {@link #hasAlreadyMoved} to the input.
     */
    public void setIsAlreadyMoved(boolean isAlreadyMoved) {
        this.hasAlreadyMoved = isAlreadyMoved;
    }

    /**
     * Tells you if the {@link Unit} has not moved this turn.
     *
     * @return {@code true} if the {@link Unit} hasn't moved on this turn yet, {@code false} if it has.
     */
    public boolean hasNotAlreadyMoved() {
        return !hasAlreadyMoved;
    }

    /**
     * Tells you if the {@link Unit} is alive.
     *
     * @return {@code true} if the {@link Unit}'s HP is positive.
     */
    public boolean isAlive() {
        return this.current_HP > 0;
    }

    /**
     * Tells you if the {@link Unit} is dead.
     * <p>
     * Is the boolean opposite of {@link #isAlive()}.
     *
     * @return {@code true} if the {@link Unit}'s {@link #current_HP} isn't positive.
     */
    public boolean isDead() {
        return !isAlive();
    }

    /**
     * Draws the {@link Unit} by calling {@link Sprite#draw(Canvas)} on its {@link #sprite} with the input
     * {@link Canvas}.
     * <p>
     * Implements {@link ch.epfl.cs107.play.game.actor.Graphics Graphics}.
     *
     * @param canvas The {@link Canvas} that the {@link #sprite} is drawn on.
     */
    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * @return The {@link Unit}'s {@link #name}.
     */
    protected String getName() {
        return this.name;
    }

    /**
     * Fills the {@link Unit}'s {@link #range} with the nodes that are both within the surrounding square in a distance of
     * {@link #radius} and in the {@link Area}.
     */
    private void completeUnitsRange() {
        // TODO comments

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
     * Does this {@link Unit} take up space on the cell it's on.
     * <p>
     * Implements {@link ch.epfl.cs107.play.game.areagame.actor.Interactable Interactable}.
     *
     * @return {@code false} because this {@link Unit} doesn't take up space on the cell it's on.
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * Is this {@link Unit} {@code viewInteractable}.
     * <p>
     * Implements {@link ch.epfl.cs107.play.game.areagame.actor.Interactable Interactable}.
     *
     * @return {@code false} because this {@link Unit} isn't {@code viewInteractable}.
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * a unit is CellInteractable
     * <p>
     * Implements {@link ch.epfl.cs107.play.game.areagame.actor.Interactable Interactable}.
     *
     * @return
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
    public void drawRangeAndPathTo(DiscreteCoordinates destination, Canvas canvas) {
        // TODO comments

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
     * @return The indexes of attackable units in the area's list of units.
     */
    public ArrayList<Integer> getIndexOfAttackableEnemies() {
        return this.getOwnerArea().getIndexOfAttackableEnemies(this.faction, this.range);
    }

    /**
     * Calls {@link Area#attack(int, int, int)} on the {@link Unit} pointed to by the {@code indexOfUnitToAttack}.
     *
     * @param indexOfUnitToAttack The index of the {@link Unit} in the {@link Area}'s units list that should be
     *                            attacked.
     */
    public void attack(int indexOfUnitToAttack) {
        // TODO comments

        this.getOwnerArea().attack(
            indexOfUnitToAttack,
            this.getDamage(),
            getNumberOfStarsOfCurrentCell()
        );
    }

    /**
     * @return The list of available {@link Action actions} that the {@link Unit} can take.
     */
    protected ArrayList<Action> getAvailableActions() {
        return this.actions;
    }

    /**
     * If the node is in the {@link Area} and in the {@link Unit}'s {@link #range} it passes the new position on to
     * {@link ICWarsActor#changePosition(DiscreteCoordinates) super.changePosition}.
     * <p>
     * Then if fills the new range with {@link #completeUnitsRange()}.
     * <p>
     * Overrides
     * {@link ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity#changePosition(DiscreteCoordinates) MovableAreaEntity.changePosition(DiscreteCoordinates)}.
     *
     * @param newPosition The new position for the {@link Unit}.
     * @return Returns {@code true} if {@link ICWarsActor#changePosition(DiscreteCoordinates) super.changePosition} does
     * so, and if the newPosition coordinates exist in the {@link Unit}'s range.
     */
    @Override
    public boolean changePosition(DiscreteCoordinates newPosition) {
        // TODO comments

        if (this.range.nodeExists(newPosition) && super.changePosition(newPosition)) {
            completeUnitsRange();
            return true;
        } else return false;
    }

    /**
     * TODO
     * <p>
     * Implements {@link ch.epfl.cs107.play.game.areagame.actor.Interactable Interactable}.
     *
     * @param areaInteractionVisitor The interaction
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor areaInteractionVisitor) {
        ((ICWarsInteractionVisitor) areaInteractionVisitor).interactWith(this);
    }

    /**
     * TODO
     *
     * @return {@link #numberOfStarsOfCurrentCell}
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
     * The way to make this {@link Unit} take damage (or otherwise reduce it's {@link #current_HP}).
     *
     * @param receivedDamage Subtracts this number from the {@link #current_HP}.
     */
    public void takeDamage(int receivedDamage) {
        this.setHp(current_HP - Math.min(numberOfStarsOfCurrentCell - receivedDamage, 0));
    }

    /**
     * Center the camera's focus on this {@link Unit}'s currently targeted enemy.
     *
     * @param indexOfUnitToAttack Passed to this {@link Unit unit's} {@link ICWarsPlayer owner's} {@link Area}'s
     *                            {@link Area#centerCameraOnTargetedEnemy(int) centerCameraOnTargetedEnemy} method.
     */
    public void centerCameraOnTargetedEnemy(int indexOfUnitToAttack) {
        this.getOwnerArea().centerCameraOnTargetedEnemy(indexOfUnitToAttack);
    }

    /**
     * TODO
     */
    private static class ICWarsUnitInteractionHandler implements ICWarsInteractionVisitor {
        /**
         * The {@link Unit} that this {@link ICWarsUnitInteractionHandler interaction handler} handles interactions for.
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
         * <p>
         * Implements {@link ICWarsInteractionVisitor}.
         *
         * @param icWarsCell
         */
        @Override
        public void interactWith(ICWarsBehavior.ICWarsCell icWarsCell) {
            unit.setNumberOfStarsOfCurrentCell(icWarsCell.getNumberOfStars());
        }
    }
}
