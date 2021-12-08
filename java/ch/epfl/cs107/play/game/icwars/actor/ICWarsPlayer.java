package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ICWarsPlayer extends ICWarsActor implements Interactor {
    protected ArrayList<Units> units = new ArrayList<>();
    protected Sprite sprite;
    protected Units SelectedUnit;
    ICWarsPlayerGUI playerGUI = new ICWarsPlayerGUI(this.getOwnerArea().getCameraScaleFactor(), this);
    States playerCurrentState;

    public ICWarsPlayer(Area area, DiscreteCoordinates position, Faction faction, Units... units) {
        super(area, position, faction);
        this.units.addAll(Arrays.asList(units));
        RegisterUnitsAsActors();
        this.playerCurrentState = States.IDLE;
    }

    /**
     * register all the units of the player in the player's ownerArea
     */
    private void RegisterUnitsAsActors() {
        units.forEach(unit -> unit.enterArea(this.getOwnerArea(), new DiscreteCoordinates((int) unit.getPosition().x, (int) unit.getPosition().y)));
    }

    @Override
    public void draw(Canvas canvas) {
        this.sprite.draw(canvas);
        if (playerCurrentState.equals(States.MOVE_UNIT)) playerGUI.draw(canvas);
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
        Keyboard keyboard = this.getOwnerArea().getKeyboard();
        switch (playerCurrentState) {
            case NORMAL:
                if (keyboard.get(Keyboard.ENTER).isDown()) this.playerCurrentState = States.SELECT_CELL;
                else if (keyboard.get(Keyboard.TAB).isDown()) this.playerCurrentState = States.IDLE;
                break;
            case SELECT_CELL:
                if (this.SelectedUnit != null) this.playerCurrentState = States.MOVE_UNIT;
                break;
            case MOVE_UNIT:
                if (keyboard.get(Keyboard.ENTER).isDown()) {
                    this.SelectedUnit.changePosition(new DiscreteCoordinates((int) SelectedUnit.getPosition().x, (int) SelectedUnit.getPosition().y));
                    SelectedUnit.setIsAlreadyMoved(true);
                    this.playerCurrentState = States.NORMAL;
                }
                break;
            default:
                break;
        }
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
     * @return true if the arraylist of units is empty
     */
    public boolean isDefeated() {
        return this.units.isEmpty();
    }

    /**
     * @param index the index of the selected Uit in the player's units list
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
     * when the turn starts for the player, he enters the NORMAL state,
     * the camera is centered on him,
     * all his units can be moved
     */
    public void startTurn() {
        this.playerCurrentState = States.NORMAL;
        // TODO idk if something should be done to make him receptive to controls (cn the guidelines page 18)
        this.getOwnerArea().setViewCandidate(this);
        units.forEach(unit -> unit.setIsAlreadyMoved(false));
    }

    /**
     * @param coordinates used for super.onleaving(coordinates)
     *                    in addition, playerCurrentState is set to NORMAl so that the player is available for future interactions
     */
    @Override
    public void onLeaving(List<DiscreteCoordinates> coordinates) {
        super.onLeaving(coordinates);
        if (this.playerCurrentState == States.SELECT_CELL)
            this.playerCurrentState = States.NORMAL;
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

    //states that an ICWarsPlayer can be in
    public enum States {
        IDLE,
        NORMAL,
        SELECT_CELL,
        MOVE_UNIT,
        ACTION_SELECTED,
        ACTION,
    }
}
