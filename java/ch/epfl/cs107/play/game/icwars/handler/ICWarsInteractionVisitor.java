package ch.epfl.cs107.play.game.icwars.handler;

import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;

/**
 * TODO
 */
public interface ICWarsInteractionVisitor extends AreaInteractionVisitor {
    /**
     * TODO
     *
     * @param unit
     */
    default void interactWith(Units unit) {
        //empty by default
    }

    /**
     * TODO
     *
     * @param player
     */
    default void interactWith(RealPlayer player) {
        //empty by default
    }

    /**
     * TODO
     *
     * @param icWarsCell
     */
    default void interactWith(ICWarsBehavior.ICWarsCell icWarsCell) {
        //empty by default
    }
}
