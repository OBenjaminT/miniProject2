package ch.epfl.cs107.play.game.icwars.area.level;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;

public class Level1 extends ICWarsArea {
    public final SpawnPoints allyFactionSpawnPoints;
    public final SpawnPoints enemyFactionSpawnPoints;

    public Level1() {
        this.allyFactionSpawnPoints = new SpawnPoints(new ArrayList<>());
        this.enemyFactionSpawnPoints = new SpawnPoints(new ArrayList<>());
    }

    @Override
    public String getTitle() {
        return "icwars/Level1";
    }

    protected void createArea() {
        // Base
        registerActor(new Background(this));
    }


    @Override
    public DiscreteCoordinates getAllyCenter() {
        return new DiscreteCoordinates(0, 0);
    }

    @Override
    public DiscreteCoordinates getFreeAllySpawnPosition() {
        return allyFactionSpawnPoints.getFreeSpawnPosition();
    }
}
