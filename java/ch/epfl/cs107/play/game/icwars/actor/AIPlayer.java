package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class AIPlayer extends ICWarsPlayer{
    /**
     * TODO
     *  @param area
     *
     * @param position
     * @param faction
     * @param units
     */
    public AIPlayer(Area area, DiscreteCoordinates position, Faction faction, Unit... units) {
        super(area, position, faction, units);
    }
}
