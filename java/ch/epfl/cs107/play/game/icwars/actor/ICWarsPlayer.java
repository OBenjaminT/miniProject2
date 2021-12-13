package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ICWarsPlayer extends ICWarsActor implements Interactor {
    public States playerCurrentState;
    protected ArrayList<Units> units = new ArrayList<>();
    protected Sprite sprite;
    protected Units SelectedUnit;
    ICWarsPlayerGUI playerGUI = new ICWarsPlayerGUI(this.getOwnerArea().getCameraScaleFactor(), this);
    boolean EnterWasReleased = false;

    public ICWarsPlayer(Area area, DiscreteCoordinates position, Faction faction, Units... units) {
        super(area, position, faction);
        this.units.addAll(Arrays.asList(units));
        RegisterUnitsAsActors();
        this.playerCurrentState = States.IDLE;
    }

    public void setPlayerCurrentState(States playerCurrentState) {
        this.playerCurrentState = playerCurrentState;
    }

    /**
     * register all the units of the player in the player's ownerArea
     */
    private void RegisterUnitsAsActors() {
        units.forEach(unit -> unit.enterArea(
            this.getOwnerArea(),
            new DiscreteCoordinates((int) unit.getPosition().x, (int) unit.getPosition().y)
        ));
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.playerCurrentState != States.IDLE) {
            this.sprite.draw(canvas);
            if (playerCurrentState.equals(States.MOVE_UNIT))
                playerGUI.draw(canvas);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // removing all the units that have hp below zero from the units list of the player and unregister this unit form the ownerArea
        units.stream()
            .filter(Units::isDead)
            .forEach(unit -> {
                units.remove(unit);
                unit.leaveArea();
            });
        drawOpacityOfUnits();
        var keyboard = this.getOwnerArea().getKeyboard();
        // https://www.baeldung.com/java-switch
        // Ensures all cases are covered, doesn't need break blocks, and assigns value to `yield` result.
        /*this.playerCurrentState = switch (playerCurrentState) {
            case IDLE -> playerCurrentState;
            case NORMAL -> {
                System.out.println(EnterWasReleased);
                if (!keyboard.get(Keyboard.ENTER).isReleased())
                    EnterWasReleased = false;
                *//*                else if (keyboard.get(Keyboard.ENTER).isReleased()) {
                    yield States.SELECT_CELL;
                }*//*
                if (keyboard.get(Keyboard.TAB).isReleased()) {
                    System.out.println("tab");
                    EnterWasReleased = false;
                    yield States.IDLE;
                } else yield !EnterWasReleased
                    ? keyboard.get(Keyboard.ENTER).isReleased() ? States.SELECT_CELL : playerCurrentState
                    : playerCurrentState;
            }
            case SELECT_CELL -> {
                System.out.println("select CELL");
                // TODO select the unit in this cell
                yield this.selectUnit() != null
                    ? States.MOVE_UNIT
                    : playerCurrentState;
            }
            case MOVE_UNIT -> {
                if (keyboard.get(Keyboard.ENTER).isReleased()) {
                    var pos = this.getPosition();
                    this.SelectedUnit.changePosition(new DiscreteCoordinates((int) pos.x, (int) pos.y));
                    SelectedUnit.setIsAlreadyMoved(true);
                    EnterWasReleased = true;
                    yield States.NORMAL;
                } else yield playerCurrentState;
            }
            case ACTION_SELECTED, ACTION -> {
                //this.unselectUnit();
                yield playerCurrentState;
            }
            // TODO
        };*/
    }

    public boolean isIdle() {
        return playerCurrentState.equals(States.IDLE);
    }

    @Override
    public void leaveArea() {
        units.forEach(ICWarsActor::leaveArea);
        super.leaveArea();
    }

    @Override
    public void enterArea(Area area, DiscreteCoordinates position) {
        super.enterArea(area, position);
        units.forEach(unit -> unit.enterArea(area, new DiscreteCoordinates((int) unit.getPosition().x, (int) unit.getPosition().y)));
    }

    public void centerCamera() {
        getOwnerArea().setViewCandidate(this);
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
     * @return true if the arraylist of units is empty
     */
    public boolean isDefeated() {
        return this.units.isEmpty();
    }

    /**
     * @param index the index of the selected Unit in the player's units list
     *              SelectedUnit parameter is associated to the proper Unit
     *              SelectedUnit is also transmitter to the playerGUI with the setter
     */
    public void selectUnit(int index) {
        SelectedUnit = this.units.get(index);
        playerGUI.setPlayerSelectedUnit(this.SelectedUnit);
    }

    /**
     * @param unit the unit the player wants to select
     *             SelectedUnit parameter is associated to the proper Unit
     *             SelectedUnit is also transmitter to the playerGUI with the setter
     */
    public void selectUnit(Units unit) {
        SelectedUnit = unit;
        playerGUI.setPlayerSelectedUnit(this.SelectedUnit);
    }

    /**
     * if a unit `UNIT` has the same position as the player, and he hasn't already been moved, we call SelectedUnit(UNIT)
     * else SelectedUnit is set to null
     */
    public Units selectUnit() {
        units.stream() // for all units
            .filter(u -> u.getPosition().equals(this.getPosition())) // if they are on the same square as the player
            .filter(Units::hasNotAlreadyMoved) // and haven't already moved
            .findFirst() // find the first one
            .ifPresentOrElse( // if there is one that fits the criteria
                this::selectUnit, // select it
                () -> SelectedUnit = null); // else `selectedUnit` is null
        return this.SelectedUnit;
    }

    /**
     * SelectedUnit is set to null
     */
    public void unselectUnit() {
        SelectedUnit = null;
    }

    /**
     * when the turn starts for the player, he enters the NORMAL state,
     * the camera is centered on him,
     * all his units can be moved
     */
    public void startTurn() {
        this.playerCurrentState = States.NORMAL;
        this.getOwnerArea().setViewCandidate(this);
        units.forEach(unit -> unit.setIsAlreadyMoved(false));
    }

    public void endTurn() {
        this.units.forEach(unit -> unit.setIsAlreadyMoved(false));
    }

    /**
     * @param coordinates used for `super.onLeaving(coordinates)`
     *                    in addition, playerCurrentState is set to NORMAl so that the player is available for future interactions
     */
    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        this.playerCurrentState = switch (this.playerCurrentState) {
            case IDLE, ACTION, ACTION_SELECTED, MOVE_UNIT, NORMAL -> this.playerCurrentState;
            case SELECT_CELL -> States.NORMAL;
        };
    }

    @Override
    public List<DiscreteCoordinates> getFieldOfViewCells() {
        return null;
    }

    @Override
    public boolean wantsCellInteraction() {
        return true;
    }

    @Override
    public boolean wantsViewInteraction() {
        return false;
    }

    @Override
    public void interactWith(Interactable other) {

    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        v.interactWith(this);
    }

    /**
     * the sprite of the already moved units has a smaller opacity
     * than the sprite of other  units
     */
    private void drawOpacityOfUnits() {
        this.units.forEach(unit -> unit.sprite.setAlpha(unit.isAlreadyMoved ? 0.1f : 1.0f));
    }

    /**
     * states that an `ICWarsPlayer` can be in
     */
    public enum States {
        IDLE,
        NORMAL,
        SELECT_CELL,
        MOVE_UNIT,
        ACTION_SELECTED,
        ACTION,
    }
}
