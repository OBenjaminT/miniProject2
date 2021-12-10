package ch.epfl.cs107.play.game.icwars.area.level;

import ch.epfl.cs107.play.game.areagame.actor.Background;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.DiscreteCoordinates;

import java.util.ArrayList;

public class Level0 extends ICWarsArea {
    public final SpawnPoints allyFactionSpawnPoints;
    public final SpawnPoints enemyFactionSpawnPoints;

    public Level0() {
        var levelAllySpawnPoints = new ArrayList<DiscreteCoordinates>();
        levelAllySpawnPoints.add(new DiscreteCoordinates(2, 5));
        levelAllySpawnPoints.add(new DiscreteCoordinates(3, 5));
        this.allyFactionSpawnPoints = new SpawnPoints(levelAllySpawnPoints);

        var levelEnnemySpawnPoints = new ArrayList<DiscreteCoordinates>();
        levelEnnemySpawnPoints.add(new DiscreteCoordinates(3, 8));
        levelEnnemySpawnPoints.add(new DiscreteCoordinates(9, 5));
        this.enemyFactionSpawnPoints = new SpawnPoints(levelEnnemySpawnPoints);
    }

    @Override
    public DiscreteCoordinates getAllyCenter() {
        return new DiscreteCoordinates(0, 0);
    }

    @Override
    public DiscreteCoordinates getEnnemyCenter() {
        return new DiscreteCoordinates(7, 4);
    }

    @Override
    public String getTitle() {
        return "icwars/Level0";
    }

    protected void createArea() {
        // Base
        registerActor(new Background(this));
    }


    public DiscreteCoordinates getFreeAllySpawnPosition() {
        return allyFactionSpawnPoints.getFreeSpawnPosition();
    }

    public DiscreteCoordinates getFreeEnnemySpawnPosition() {
        return enemyFactionSpawnPoints.getFreeSpawnPosition();
    }
}
