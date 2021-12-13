package ch.epfl.cs107.play.game.areagame;

import ch.epfl.cs107.play.game.Game;
import ch.epfl.cs107.play.game.PauseMenu;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.window.Window;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


/**
 * TODO
 * <p>
 * AreaGames are a concept of Game which is displayed in a (MxN) Grid which is called an Area
 * An AreaGame has multiple Areas
 */
abstract public class AreaGame implements Game, PauseMenu.Pausable {

    /**
     * TODO
     */
    protected List<ICWarsPlayer> players;

    /**
     * TODO
     */
    protected List<ICWarsPlayer> PlayersWaitingForNextTurn = new ArrayList<>();

    /**
     * TODO
     */
    protected List<ICWarsPlayer> PlayersWaitingForCurrentTurn = new ArrayList<>();

    /**
     * TODO
     */
    protected ICWarsPlayer activePlayer;

    /**
     * TODO
     * <p>
     * The current area the game is in
     */
    protected Area currentArea;

    /// Context objects

    /**
     * TODO
     */
    private Window window;

    /**
     * TODO
     */
    private FileSystem fileSystem;

    /// A map containing all the Area of the Game

    /**
     * TODO
     */
    private LinkedHashMap<String, Area> areas;

    /// pause mechanics and menu to display. May be null

    /**
     * TODO
     */
    private boolean paused;

    /**
     * TODO
     */
    private boolean requestPause;

    /**
     * TODO
     */
    private PauseMenu menu;

    /**
     * TODO
     */
    protected final void resetPlayers() {
        players = new ArrayList<>();
        PlayersWaitingForNextTurn = new ArrayList<>();
        PlayersWaitingForCurrentTurn = new ArrayList<>();
    }

    /**
     * TODO
     * <p>
     * Add an Area to the AreaGame list
     *
     * @param a (Area): The area to add, not null
     */
    protected final void addArea(Area a) {
        areas.put(a.getTitle(), a);
    }

    /**
     * TODO
     */
    protected final void resetAreas() {
        areas = new LinkedHashMap<>();
    }

    /**
     * TODO
     *
     * @return
     */
    protected boolean nextArea() {
        int index = areas.values().stream().toList().indexOf(currentArea);
        players.forEach(ICWarsPlayer::leaveArea);
        if (index < areas.size() - 1) {
            setCurrentArea(areas.get(areas.keySet().stream().toList().get(index + 1)), true);
            return true;
        } else {
            System.out.println("Game over");
            return false;
        }
    }

    /**
     * TODO
     *
     * @param area
     * @param forceBegin
     * @return
     */
    protected final Area setCurrentArea(Area area, boolean forceBegin) {
        return setCurrentArea(area.getTitle(), forceBegin);
    }

    /**
     * TODO
     * <p>
     * Setter for the current area: Select an Area in the list from its key
     * - the area is then begin or resume depending on if the area is already started or not and if it is forced
     *
     * @param key        (String): Key of the Area to select, not null
     * @param forceBegin (boolean): force the key area to call begin even if it was already started
     * @return (Area): after setting it, return the new current area
     */
    protected final Area setCurrentArea(String key, boolean forceBegin) {
        try (Area newArea = areas.get(key)) {
            if (newArea == null) {
                System.out.println("New Area not found, keep previous one");
            } else {
                // Stop previous area if it exists
                if (currentArea != null) {
                    currentArea.suspend();
                    currentArea.purgeRegistration(); // Is useful?
                }

                currentArea = newArea;

                // Start/Resume the new one
                if (forceBegin || !currentArea.isStarted())
                    currentArea.begin(window, fileSystem);
                else currentArea.resume(window, fileSystem);
            }
        }

        return currentArea;
    }

    /**
     * TODO
     * <p>
     * Set the pause menu
     *
     * @param menu (PauseMenu) : the new pause menu, not null
     * @return (PauseMenu): the new pause menu, not null
     */
    protected final PauseMenu setPauseMenu(PauseMenu menu) {
        this.menu = menu;
        this.menu.begin(window, fileSystem);
        this.menu.setOwner(this);
        return menu;
    }

    /**
     * TODO
     *
     * @return (Window) : the Graphic and Audio context
     */
    protected final Window getWindow() {
        return window;
    }

    /**
     * TODO
     *
     * @return (FIleSystem): the linked file system
     */
    protected final FileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * TODO
     * <p>
     * Getter for the current area
     *
     * @return (Area)
     */
    protected final Area getCurrentArea() {
        return this.currentArea;
    }

    /// AreaGame implements Playable

    /**
     * TODO
     *
     * @param window     (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {

        // Keep context
        this.window = window;
        this.fileSystem = fileSystem;

        areas = new LinkedHashMap<>();
        players = new ArrayList<>();
        paused = false;
        return true;
    }

    /**
     * TODO
     *
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        if (paused && menu != null)
            menu.update(deltaTime);
        else currentArea.update(deltaTime);
        paused = requestPause;
    }

    /**
     * TODO
     */
    @Override
    public void requestPause() {
        requestPause = true;
    }

    /**
     * TODO
     */
    @Override
    public void requestResume() {
        requestPause = false;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public boolean isPaused() {
        return paused;
    }
}
