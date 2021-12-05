package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.gui.ICWarsPlayerGUI;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;


public class ICWarsPlayer extends ICWarsActor {
    protected ArrayList<Units> units = new ArrayList<>();
    protected Sprite sprite;
    protected Units SelectedUnit;
    ICWarsPlayerGUI playerGUI = new ICWarsPlayerGUI(this.getOwnerArea().getCameraScaleFactor(), this);

    public ICWarsPlayer(Area area, DiscreteCoordinates position, Faction faction, Units... units) {
        super(area, position, faction);
        this.units.addAll(Arrays.asList(units));
        RegisterUnitsAsActors();
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
        playerGUI.draw(canvas);
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

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        //TODO implement this
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
}
