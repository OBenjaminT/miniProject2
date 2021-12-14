package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

import java.util.List;

/**
 * TODO
 */
public abstract class ICWarsArea extends Area {
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
     * <p>
     * Get the "ally center", currently used as the player spawn point
     */
    public abstract DiscreteCoordinates getFactionCenter(ICWarsActor.Faction faction);

    public abstract List<Unit> factionUnits(ICWarsActor.Faction faction);

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
}
