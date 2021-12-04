package ch.epfl.cs107.play.game.icwars.area.level;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

public class Level1 extends ICWarsArea {
    @Override
    public String getTitle() {
        return "icwars/Level1";
    }

    protected void createArea() {
        // Base
        registerActor(new Background(this));
    }

    @Override
    public DiscreteCoordinates getRealPlayerSpawnPosition() {
        return new DiscreteCoordinates(2, 5);
    }

    @Override
    public DiscreteCoordinates getTankSpawnPosition() {
        return new DiscreteCoordinates(2, 5);
    }

    @Override
    public DiscreteCoordinates getSoldierSpawnPosition() {
        return new DiscreteCoordinates(3, 5);
    }
}
