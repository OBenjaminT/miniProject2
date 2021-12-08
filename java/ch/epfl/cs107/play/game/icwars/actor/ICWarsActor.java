package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.areagame.actor.MovableAreaEntity;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.Collections;
import java.util.List;

abstract public class ICWarsActor extends MovableAreaEntity  {
    public Faction faction;

    /**
     * Default MovableAreaEntity constructor
     *
     * @param area     (Area): Owner area. Not null
     * @param faction  (Orientation): Initial orientation of the entity. Not null
     * @param position (Coordinate): Initial position of the entity. Not null
     */
    public ICWarsActor(Area area, DiscreteCoordinates position, Faction faction) {
        super(area, Orientation.UP, position);
        this.faction = faction;
    }

    public void enterArea(Area area, DiscreteCoordinates position) {
        area.registerActor(this);
        setOwnerArea(area);
        setCurrentPosition(position.toVector());
    }

    public void leaveArea() {
        getOwnerArea().unregisterActor(this);
    }

    public List<DiscreteCoordinates> getCurrentCells() {
        return Collections.singletonList(getCurrentMainCellCoordinates());
    }

    // faction aly is associated to boolean true, enemy to false
    public enum Faction {
        ALLY(true),
        ENEMY(false),
        ;

        final boolean isAlly;

        Faction(boolean isAlly) {
            this.isAlly = isAlly;
        }
    }
}
