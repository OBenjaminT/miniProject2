package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class ICWarsArea extends Area {
    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();

    /**
     * Get the "ally center", currently used as the player spawn point
     */
    public abstract DiscreteCoordinates getAllyCenter();

    /**
     * Get the "ennemy center", currently used as the player spawn point
     */
    public abstract DiscreteCoordinates getEnnemyCenter();

    /**
     * Get the a free coordinate that the ally can spawn a unit in
     */
    public abstract DiscreteCoordinates getFreeAllySpawnPosition();

    @Override
    public final float getCameraScaleFactor() {
        return 10.f;
    }

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

    public abstract DiscreteCoordinates getFreeEnnemySpawnPosition();

    //public abstract DiscreteCoordinates getPlayerSpawnPosition();

    /// Demo2Area implements Playable


    /**
     * A class that holds a list of `DiscreteCoordinates` and remembers if anything has been spawned there.
     */
    public static class SpawnPoints {
        // holds a set of coordinates representing potential spawn points. True if available, False if taken.
        private final Map<DiscreteCoordinates, Boolean> spawnPoints = new HashMap<>();

        /**
         * Register a list of `DiscreteCoordinates` and set their availability to true
         *
         * @param coordinates The list of available spawn coordinates
         */
        public SpawnPoints(List<DiscreteCoordinates> coordinates) {
            coordinates.forEach(c -> spawnPoints.put(c, true));
        }

        /**
         * Returns an available coordinate to spawn a unit in.
         * Does lots of null checking.
         * Finds the first coordinate available.
         * Then sets its availability to false.
         * Then returns it.
         */
        public DiscreteCoordinates getFreeSpawnPosition() {
            // get the first coordinate, with a bunch of null checking in case there are none left
            var firstCoordinate = spawnPoints.entrySet().stream()
                .map(Optional::ofNullable)
                .filter(Optional::isPresent)
                .filter(t -> t.get().getValue())
                .findFirst()
                .orElseGet(Optional::empty)
                .orElse(null);
            if (firstCoordinate == null) return null;
            var ret = firstCoordinate.getKey();
            spawnPoints.put(ret, false); // update taken coordinate to unavailable
            return ret;
        }
    }
}
