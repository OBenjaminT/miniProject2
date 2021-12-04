package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

import java.util.ArrayList;
import java.util.Arrays;


abstract class ICWarsPlayer extends ICWarsActor {
    protected ArrayList<Units> units = new ArrayList<>();
    protected Sprite sprite;

    public ICWarsPlayer(Area area, DiscreteCoordinates position, Faction faction, Units... units) {
        super(area, position, faction);
        this.units.addAll(Arrays.asList(units));
        RegisterUnitsAsActors();
    }

    /**
     * register all the units of the player in the player's ownerArea
     */
    private void RegisterUnitsAsActors() {
        for (Units unit : units) {
            unit.enterArea(this.getOwnerArea(), new DiscreteCoordinates((int) unit.getPosition().x, (int) unit.getPosition().y));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        this.sprite.draw(canvas);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        //remoing all the units that have hp below zero from the units list of the player and unregister this unit form the ownerArea
        for (int i = 0; i < this.units.size(); ++i) {
            Units unit = units.get(i);
            if (!unit.isAlive()) {
                units.remove(unit);
                unit.leaveArea();
            }
        }
    }

/*    @Override
    public void leaveArea() {
        for(Units unit: units) {
            unit.leaveArea();
        }
        super.leaveArea();
    }*/

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

}
