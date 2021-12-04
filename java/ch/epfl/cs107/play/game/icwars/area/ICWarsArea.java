package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public abstract class ICWarsArea extends Area {

    /**
     * Create the area by adding it all actors
     * called by begin method
     * Note it set the Behavior as needed !
     */
    protected abstract void createArea();

    /**
     * return player spawn position in this specific area
     */
    abstract public DiscreteCoordinates getRealPlayerSpawnPosition();

    /**
     * return tank spawn position in this specific area
     */
    abstract public DiscreteCoordinates getTankSpawnPosition();

    /**
     * return soldier spawn position in this specific area
     */
    abstract public DiscreteCoordinates getSoldierSpawnPosition();


    @Override
    public final float getCameraScaleFactor() {
        //private ArrayList<Units> units = new ArrayList<>();
        return 10.f;
    }

    //public abstract DiscreteCoordinates getPlayerSpawnPosition();

    /// Demo2Area implements Playable

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
}
