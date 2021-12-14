package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * TODO
 */
public abstract class ICWarsArea extends Area {
    /**
     * TODO
     */
    public SpawnPoints allyFactionSpawnPoints;

    /**
     * TODO
     * <p>
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();

    /**
     * TODO
     */
    public SpawnPoints enemyFactionSpawnPoints;

    /**
     * TODO
     * <p>
     * Get the "ally center", currently used as the player spawn point
     */
    public abstract DiscreteCoordinates getAllyCenter();

    /**
     * TODO
     * <p>
     * Get the "enemy center", currently used as the player spawn point
     */
    public abstract DiscreteCoordinates getEnemyCenter();

    public ICWarsArea(SpawnPoints allyFactionSpawnPoints, SpawnPoints enemyFactionSpawnPoints) {
        this.allyFactionSpawnPoints = allyFactionSpawnPoints;
        this.enemyFactionSpawnPoints = enemyFactionSpawnPoints;
    }

    public abstract List<Unit> factionUnits(ICWarsActor.Faction faction);

    /**
     * TODO
     * <p>
     * Get a free coordinate that the ally can spawn a unit in
     */
    public Optional<DiscreteCoordinates> getFreeEnemySpawnPosition() {
        return enemyFactionSpawnPoints.getFreeSpawnPosition();
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public final float getCameraScaleFactor() {
        return 10.f;
    }

    /**
     * TODO
     *
     * @param window
     * @param fileSystem
     * @return
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Set the behavior map
            ICWarsBehavior behavior = new ICWarsBehavior(window, getTitle());
            setBehavior(behavior);
            createArea();
            return true;
        }
        return false;
    }

    /**
     * TODO
     *
     * @return
     */
    public Optional<DiscreteCoordinates> getFreeAllySpawnPosition() {
        return allyFactionSpawnPoints.getFreeSpawnPosition();
    }

    //public abstract DiscreteCoordinates getPlayerSpawnPosition();

    /// Demo2Area implements Playable


    /**
     * TODO
     * <p>
     * A class that holds a list of `DiscreteCoordinates` and remembers if anything has been spawned there.
     */
    public static class SpawnPoints {
        /**
         * TODO
         */
        // holds a set of coordinates representing potential spawn points. True if available, False if taken.
        private final Map<DiscreteCoordinates, Boolean> spawnPoints = new HashMap<>();

        /**
         * TODO
         * <p>
         * Register a list of `DiscreteCoordinates` and set their availability to true
         *
         * @param coordinates The list of available spawn coordinates
         */
        public SpawnPoints(List<DiscreteCoordinates> coordinates) {
            coordinates.forEach(c -> spawnPoints.put(c, true));
        }

        /**
         * TODO
         * <p>
         * Returns an available coordinate to spawn a unit in.
         * Does lots of null checking.
         * Finds the first coordinate available.
         * Then sets its availability to false.
         * Then returns it.
         */
        public Optional<DiscreteCoordinates> getFreeSpawnPosition() {
            // get the first coordinate, with a bunch of null checking in case there are none left
            var firstCoordinate = spawnPoints.entrySet().stream() // go through all spawnPoints
                .filter(Map.Entry::getValue) // only keep those that are available
                .findFirst();
            if (firstCoordinate.isPresent()) { // if there is an available coordinate
                var ret = firstCoordinate.get().getKey();
                spawnPoints.put(ret, false); // update taken coordinate to unavailable
                return Optional.of(ret);
            } else return Optional.empty();
        }
    }

}
