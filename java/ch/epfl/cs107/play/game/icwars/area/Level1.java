package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.areagame.actor.Foreground;

public class Level1 extends ICWarsArea{
    @Override
    public String getTitle() {
        return "icwars/Level1";
    }

    protected void createArea() {
        // Base
        registerActor(new Background(this));
        registerActor(new Foreground(this));
    }

/*    @Override
    public DiscreteCoordinates getPlayerSpawnPosition() {
        return null;
    }*/
}
