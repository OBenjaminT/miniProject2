package ch.epfl.cs107.play.game.icwars.handler;

import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;

public interface ICWarsInteractionVisitor extends AreaInteractionVisitor {
    default void interactWith(Units unit) {
        //empty by default
    }

    default void interactWith(RealPlayer player) {
        //empty by default
    }

    default void interactWith(ICWarsBehavior.ICWarsCell icWarsCell) {
        //empty by default
    }
}
