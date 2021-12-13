package ch.epfl.cs107.play.game.areagame;

import ch.epfl.cs107.play.game.DragHelper;
import ch.epfl.cs107.play.game.PauseMenu;
import ch.epfl.cs107.play.game.Playable;
import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.actor.Draggable;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Interactor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.game.icwars.area.ICWarsRange;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Mouse;
import ch.epfl.cs107.play.window.Window;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * TODO
 * <p>
 * Area is a "Part" of the AreaGame. An Area is made of a Behavior, and a List of Actors
 */
public abstract class Area implements Playable, PauseMenu.Pausable {

    // Context objects
    /**
     * TODO
     */
    private Window window;

    /**
     * TODO
     */
    private FileSystem fileSystem;

    // Camera Parameter

    /**
     * TODO
     */
    private Actor viewCandidate;

    /**
     * TODO
     */
    private Vector viewCenter;

    /// List of Actors inside the area

    /**
     * TODO
     */
    private List<Actor> actors;

    /// List of Actors we want to register/unregistered from the area for next update iteration

    /**
     * TODO
     */
    private List<Actor> registeredActors;

    /**
     * TODO
     */
    private List<Actor> unregisteredActors;

    /// Sublist of actor (interactors) inside the area

    /**
     * TODO
     */
    private List<Interactor> interactors;

    /**
     * TODO
     */
    private Map<Interactable, List<DiscreteCoordinates>> interactablesToEnter;

    /**
     * TODO
     */
    private Map<Interactable, List<DiscreteCoordinates>> interactablesToLeave;

    /// The behavior Map

    /**
     * TODO
     */
    private AreaBehavior areaBehavior;

    /// pause mechanics and menu to display. May be null - start indicate if area already begins, paused indicate if we
    // display the pause menu

    /**
     * TODO
     */
    private boolean started;

    /**
     * TODO
     */
    private boolean paused;

    /**
     * TODO
     */
    private AreaPauseMenu menu;


    /**
     * TODO
     *
     * @return (float): camera scale factor, assume it is the same in x and y direction
     */
    public abstract float getCameraScaleFactor();

    /**
     * TODO
     * <p>
     * Setter for the Behavior of this Area
     * Please call this method in the `begin` method of every subclass
     *
     * @param ab (AreaBehavior), not null
     */
    protected final void setBehavior(AreaBehavior ab) {
        this.areaBehavior = ab;
    }

    /**
     * TODO
     * <p>
     * Setter for the view Candidate
     *
     * @param a (Actor), not null
     */
    public final void setViewCandidate(Actor a) {
        this.viewCandidate = a;
    }


    /**
     * TODO
     * <p>
     * Add an actor to the actors list
     * and to the behavior area cell if the actor is an Interactable
     * and to the interactor list if the actor is an Interactor
     *
     * @param a        (Actor)(Interactor?)(Interactable?): the actor to add, not null
     * @param safeMode (Boolean): if True, the method ends
     */
    private void addActor(Actor a, boolean safeMode) {

        boolean errorHappen = false;

        if (a instanceof Interactor) interactors.add((Interactor) a);
        if (a instanceof Interactable)
            errorHappen = !enterAreaCells((Interactable) a, ((Interactable) a).getCurrentCells());
        errorHappen = errorHappen || !actors.add(a);

        if (errorHappen && !safeMode) {
            System.out.println("Actor " + a + " cannot be completely added, so remove it from where it was");
            // Call it in safe mode to avoid recursive calls
            removeActor(a, true);
        }
    }

    /**
     * TODO
     * <p>
     * Remove an actor form the actor list
     * and from the behavior area cell if the actor is an Interactable
     * and from the interactor list if the actor is an Interactor
     *
     * @param a        (Actor): the actor to remove, not null
     * @param safeMode (Boolean): if True, the method ends
     */
    private void removeActor(Actor a, boolean safeMode) {
        boolean errorHappen = false;

        if (a instanceof Interactor)
            errorHappen = !interactors.remove((Interactor) a);
        if (a instanceof Interactable)
            errorHappen = errorHappen || !leaveAreaCells(((Interactable) a), ((Interactable) a).getCurrentCells());
        errorHappen = errorHappen || !actors.remove(a);

        if (errorHappen && !safeMode) {
            System.out.println("Actor " + a + " cannot be completely removed, so add it from where it was");
            // Call it in safe mode to avoid recursive calls
            addActor(a, true);
        }
    }

    /**
     * TODO
     * <p>
     * Register an actor : will be added at next update
     *
     * @param a (Actor): the actor to register, not null
     * @return (boolean): true if the actor is correctly registered
     */
    public final boolean registerActor(Actor a) {
        // TODO if actor can be registered: It is this Area decision, implement a strategy
        return registeredActors.add(a);
    }

    /**
     * TODO
     * <p>
     * Unregister an actor : will be removed at next update
     *
     * @param a (Actor): the actor to unregister, not null
     * @return (boolean): true if the actor is correctly unregistered
     */
    public final boolean unregisterActor(Actor a) {
        // TODO if actor can be unregistered: It is this Area decision, implement a strategy
        return unregisteredActors.add(a);
    }

    /**
     * TODO
     * <p>
     * Indicate if the given actor exists into the actor list
     *
     * @param a (Actor): the given actor, may be null
     * @return (boolean): true if the given actor exists into actor list
     */
    public boolean exists(Actor a) {
        return actors.contains(a);
    }


    /**
     * TODO
     * <p>
     * Getter for the area width
     *
     * @return (int) : the width in number of cols
     */
    public int getWidth() {
        return areaBehavior.getWidth();
    }

    /**
     * TODO
     * <p>
     * Getter for the area height
     *
     * @return (int) : the height in number of rows
     */
    public int getHeight() {
        return areaBehavior.getHeight();
    }

    /**
     * TODO
     *
     * @return the Window Keyboard for inputs
     */
    public final Keyboard getKeyboard() {
        return window.getKeyboard();
    }

    /**
     * TODO
     *
     * @return the Window Mouse for inputs
     */
    public final Mouse getMouse() {
        return window.getMouse();
    }

    /**
     * TODO
     *
     * @return the mouse position relatively to the area and the cells
     */
    public Vector getRelativeMousePosition() {
        return getMouse().getPosition()
            .max(new Vector(0, 0))
            .min(new Vector(getWidth(), getHeight()));
    }

    /**
     * TODO
     *
     * @return the mouse coordinates relatively to the area and the cells
     */
    public DiscreteCoordinates getRelativeMouseCoordinates() {
        Vector mousePosition = getRelativeMousePosition();
        return new DiscreteCoordinates((int) Math.floor(mousePosition.x), (int) Math.floor(mousePosition.y));
    }

    /**
     * TODO
     *
     * @return (boolean): true if the method begin already called once. You can use resume() instead
     */
    public final boolean isStarted() {
        return started;
    }

    /**
     * TODO
     * <p>
     * If possible make the given interactable entity leave the given area cells
     *
     * @param entity      (Interactable), not null
     * @param coordinates (List of DiscreteCoordinates), may be empty but not null
     * @return (boolean): True if possible to leave
     */
    public final boolean leaveAreaCells(Interactable entity, List<DiscreteCoordinates> coordinates) {
        // TODO if Interactable can leave the cells: It is this Area decision, implement a strategy
        // Until now, the entity is put in a map waiting the update end to avoid concurrent exception during interaction
        if (areaBehavior.canLeave(entity, coordinates)) {
            interactablesToLeave.put(entity, coordinates);
            return true;
        } else return false;
    }

    /**
     * TODO
     * <p>
     * If possible make the given interactable entity enter the given area cells
     *
     * @param entity      (Interactable), not null
     * @param coordinates (List of DiscreteCoordinates), may be empty but not null
     * @return (boolean): True if possible to enter
     */
    public final boolean enterAreaCells(Interactable entity, List<DiscreteCoordinates> coordinates) {
        // TODO if Interactable can enter the cells: It is this Area decision, implement a strategy
        // Until now, the entity is put in a map waiting the update end to avoid concurrent exception during interaction
        if (areaBehavior.canEnter(entity, coordinates)) {
            interactablesToEnter.put(entity, coordinates);
            return true;
        } else return false;
    }

    /**
     * TODO
     * <p>
     * Inform if the entity can enter the area cells
     *
     * @param entity      (Interactable), not null
     * @param coordinates (List of DiscreteCoordinates), may be empty but not null
     * @return (boolean): True if possible to enter
     */
    public final boolean canEnterAreaCells(Interactable entity, List<DiscreteCoordinates> coordinates) {
        return areaBehavior.canEnter(entity, coordinates);
    }


    /// Area implements Playable

    /**
     * TODO
     *
     * @param window     (Window): display context. Not null
     * @param fileSystem (FileSystem): given file system. Not null
     * @return
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        this.window = window;
        this.fileSystem = fileSystem;
        actors = new LinkedList<>();
        interactors = new LinkedList<>();
        registeredActors = new LinkedList<>();
        unregisteredActors = new LinkedList<>();
        interactablesToEnter = new HashMap<>();
        interactablesToLeave = new HashMap<>();
        viewCenter = Vector.ZERO;
        paused = false;
        started = true;
        return true;
    }

    /**
     * TODO
     * <p>
     * Resume method: Can be overridden
     *
     * @param window     (Window): display context, not null
     * @param fileSystem (FileSystem): given file system, not null
     * @return (boolean) : if the resume succeed, true by default
     */
    public boolean resume(Window window, FileSystem fileSystem) {
        return true;
    }

    /**
     * TODO
     *
     * @param deltaTime elapsed time since last update, in seconds, non-negative
     */
    @Override
    public void update(float deltaTime) {
        purgeRegistration();

        // Decide if we update the contextual menu or this content
        if (paused && menu != null)
            menu.update(deltaTime);
        else {
            // Render actors
            actors.forEach(actor -> actor.update(deltaTime));

            Draggable currentDraggedElement = DragHelper.getCurrentDraggedElement();
            if (currentDraggedElement != null && currentDraggedElement.wantsDropInteraction())
                areaBehavior.dropInteractionOf(currentDraggedElement, getRelativeMouseCoordinates());

            // Realize interaction between interactors and their cells contents
            interactors.forEach(interactor -> {
                if (interactor.wantsCellInteraction())
                    areaBehavior.cellInteractionOf(interactor);
                if (interactor.wantsViewInteraction())
                    areaBehavior.viewInteractionOf(interactor);
            });

            // Update camera location
            updateCamera();

            // Draw actors
            actors.forEach(actor -> {
                actor.bip(window);
                actor.draw(window);
            });
        }
    }

    /**
     * TODO
     */
    final void purgeRegistration() {
        // PART 1
        // - Register actors
        registeredActors.forEach(actor -> addActor(actor, false));
        registeredActors.clear();

        // - unregister actors
        unregisteredActors.forEach(actor -> removeActor(actor, false));
        unregisteredActors.clear();

        // PART 2
        // - leave old cells
        interactablesToLeave.forEach((key, value) -> {
            areaBehavior.leave(key, value);
            key.onLeaving(value);
        });
        interactablesToLeave.clear();

        // - enter new cells
        interactablesToEnter.forEach((key, value) -> {
            areaBehavior.enter(key, value);
            key.onEntering(value);
        });
        interactablesToEnter.clear();
    }


    /**
     * TODO
     */
    private void updateCamera() {

        // Update expected viewport center
        if (viewCandidate != null) {
            viewCenter = viewCandidate.getPosition();
        } else { // Set default view to center
            viewCenter = new Vector(getWidth() / (float) 2, getHeight() / (float) 2);
        }
        // Compute new viewport
        Transform viewTransform = Transform.I.scaled(getCameraScaleFactor()).translated(viewCenter);
        window.setRelativeTransform(viewTransform);
    }

    /**
     * TODO
     * <p>
     * Suspend method: Can be overridden, called before resume other
     */
    public void suspend() {
        // Do nothing by default
    }


    /**
     * TODO
     */
    @Override
    public void close() {
        System.out.println("Close requested");
    }


    /// Area Implements `PauseMenu.Pausable`

    /**
     * TODO
     * <p>
     * Can be called by any possessor of this Area.
     * Caller indicate it requests a pause with given menu displayed.
     * Notice: this method chooses if the request ends up or not
     *
     * @param menu (AreaPauseMenu): The context menu to display. It (or any of its components) will
     *             be responsible for the ResumeRequest, not null
     */
    public final void requestAreaPause(AreaPauseMenu menu) {
        // TODO if the request end up: It is this Area decision, implement a strategy
        if (menu != null) {
            this.menu = menu;
            // Important to begin the menu each time : isResumeRequested must be set to false
            this.menu.begin(window, fileSystem);
            this.menu.setOwner(this);
        }
        requestPause();
    }

    /**
     * TODO
     */
    @Override
    public final void requestPause() {
        // TODO if the request end up: It is this Area decision, implement a strategy
        this.paused = true;
    }

    /**
     * TODO
     * <p>
     * Can be called by anny possessor of this Area
     * Caller indicates it requests a resume of the pause state to the game
     * Notice: this method chooses if the request ends up or not
     */
    @Override
    public final void requestResume() {
        // TODO if the request end up: It is this Area decision, implement a strategy
        this.paused = false;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public final boolean isPaused() {
        return paused;
    }


    /**
     * TODO
     *
     * @return all the units in the area
     */
    private ArrayList<Units> getUnits() {
        return actors.stream()
            .filter(actor -> actor instanceof Units)
            .map(actor -> (Units) actor)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * TODO
     *
     * @param faction attackable enemies have a different faction from the oe given as a parameter
     * @param range   the range where attackable units can be found
     * @return a list of integers representing the indexes of attackable units with coordinates
     * that are in a range
     */
    public ArrayList<Integer> getIndexOfAttackableEnemies(ICWarsActor.Faction faction, ICWarsRange range) {
        ArrayList<Units> units = getUnits();
        return IntStream.range(0, units.size())
            .filter(i -> units.get(i).faction != faction)
            .filter(i -> range.nodeExists(new DiscreteCoordinates((int) units.get(i).getPosition().x, (int) units.get(i).getPosition().y)))
            .boxed()
            .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * TODO
     *
     * @param indexOfUnitToAttack the index of the unit in the unit list that should be the center fo the camera
     */
    public void centerCameraOnTargetedEnemy(int indexOfUnitToAttack) {
        this.setViewCandidate(this.getUnits().get(indexOfUnitToAttack));
    }

    /**
     * TODO
     *
     * @param indexOfUnitToAttack the index of the unit in the unit list that should receive the dammages
     * @param damage              used to calculate the amount of received damage
     * @param numberOfStars       depends on the type of cell the attacked unit is on and it is used to calculate the actual dammage the unit receives
     */
    public void attack(int indexOfUnitToAttack, int damage, int numberOfStars) {
        getUnits().get(indexOfUnitToAttack)
            .receivesDamage(damage - numberOfStars);
    }
}
