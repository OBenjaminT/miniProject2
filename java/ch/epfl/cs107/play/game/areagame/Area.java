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


/**
 * Area is a "Part" of the AreaGame. An Area is made of a Behavior, and a List of Actors
 */
public abstract class Area implements Playable, PauseMenu.Pausable {

    // Context objects
    private Window window;
    private FileSystem fileSystem;
    // Camera Parameter
    private Actor viewCandidate;
    private Vector viewCenter;
    /// List of Actors inside the area
    private List<Actor> actors;
    /// List of Actors we want to register/unregistered from the area for next update iteration
    private List<Actor> registeredActors;
    private List<Actor> unregisteredActors;
    /// Sublist of actor (interactors) inside the area
    private List<Interactor> interactors;
    private Map<Interactable, List<DiscreteCoordinates>> interactablesToEnter;
    private Map<Interactable, List<DiscreteCoordinates>> interactablesToLeave;
    /// The behavior Map
    private AreaBehavior areaBehavior;
    /// pause mechanics and menu to display. May be null - start indicate if area already begins, paused indicate if we display the pause menu
    private boolean started, paused;
    private AreaPauseMenu menu;


    /**
     * @return (float): camera scale factor, assume it is the same in x and y direction
     */
    public abstract float getCameraScaleFactor();

    /**
     * Setter for the Behavior of this Area
     * Please call this method in the `begin` method of every subclass
     *
     * @param ab (AreaBehavior), not null
     */
    protected final void setBehavior(AreaBehavior ab) {
        this.areaBehavior = ab;
    }

    /**
     * Setter for the view Candidate
     *
     * @param a (Actor), not null
     */
    public final void setViewCandidate(Actor a) {
        this.viewCandidate = a;
    }


    /**
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
     * Indicate if the given actor exists into the actor list
     *
     * @param a (Actor): the given actor, may be null
     * @return (boolean): true if the given actor exists into actor list
     */
    public boolean exists(Actor a) {
        return actors.contains(a);
    }


    /**
     * Getter for the area width
     *
     * @return (int) : the width in number of cols
     */
    public int getWidth() {
        return areaBehavior.getWidth();
    }

    /**
     * Getter for the area height
     *
     * @return (int) : the height in number of rows
     */
    public int getHeight() {
        return areaBehavior.getHeight();
    }

    /**
     * @return the Window Keyboard for inputs
     */
    public final Keyboard getKeyboard() {
        return window.getKeyboard();
    }

    /**
     * @return the Window Mouse for inputs
     */
    public final Mouse getMouse() {
        return window.getMouse();
    }

    /**
     * @return the mouse position relatively to the area and the cells
     */
    public Vector getRelativeMousePosition() {
        return getMouse().getPosition()
            .max(new Vector(0, 0))
            .min(new Vector(getWidth(), getHeight()));
    }

    /**
     * @return the mouse coordinates relatively to the area and the cells
     */
    public DiscreteCoordinates getRelativeMouseCoordinates() {
        Vector mousePosition = getRelativeMousePosition();
        return new DiscreteCoordinates((int) Math.floor(mousePosition.x), (int) Math.floor(mousePosition.y));
    }

    /**
     * @return (boolean): true if the method begin already called once. You can use resume() instead
     */
    public final boolean isStarted() {
        return started;
    }

    /**
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
     * Resume method: Can be overridden
     *
     * @param window     (Window): display context, not null
     * @param fileSystem (FileSystem): given file system, not null
     * @return (boolean) : if the resume succeed, true by default
     */
    public boolean resume(Window window, FileSystem fileSystem) {
        return true;
    }

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
     * Suspend method: Can be overridden, called before resume other
     */
    public void suspend() {
        // Do nothing by default
    }


    @Override
    public void close() {
        System.out.println("Close requested");
    }


    /// Area Implements `PauseMenu.Pausable`

    /**
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

    @Override
    public final void requestPause() {
        // TODO if the request end up: It is this Area decision, implement a strategy
        this.paused = true;
    }

    /**
     * Can be called by anny possessor of this Area
     * Caller indicates it requests a resume of the pause state to the game
     * Notice: this method chooses if the request ends up or not
     */
    @Override
    public final void requestResume() {
        // TODO if the request end up: It is this Area decision, implement a strategy
        this.paused = false;
    }

    @Override
    public final boolean isPaused() {
        return paused;
    }


    /**
     * @return all the units in the area
     */
    private ArrayList<Units> getUnits (){
        ArrayList<Units> units = new ArrayList<>();
        for(Actor actor : actors){
            if(actor instanceof Units){
                units.add((Units)actor);
            }
        }
        return units;
    }

    /**
     * @return a list of integers representing the indexes of attackable units with coordinates
     * that are in a range
     * @param faction attable ennemies have a differerent faction from the oe given as a parametter
     * @param range the range where attackables units can be found
     */
    public ArrayList<Integer> getIndexOfAttackableEnnemies (ICWarsActor.Faction faction, ICWarsRange range){
        ArrayList<Units> units = getUnits();
        ArrayList<Integer> IndexsOfAttackableEnnemies = new ArrayList<>();
        for(int i=0; i<units.size(); i++){
            Units unit=units.get(i);
            boolean unitIsclose = range.nodeExists(new DiscreteCoordinates((int)unit.getPosition().x, (int)unit.getPosition().y));
            if(unit.faction!=faction && unitIsclose){
                IndexsOfAttackableEnnemies.add(i);
            }
        }
        return IndexsOfAttackableEnnemies;
    }

    /**
     * @param indexOfUnitToAttack the index of the unit in the unit list that should be the center fo the camera
     */
    public void centerCameraOnTargetedEnnemy(int indexOfUnitToAttack){
        this.setViewCandidate(this.getUnits().get(indexOfUnitToAttack));
    }

    /**
     * @param indexOfUnitToAttack  the index of the unit in the unit list that should receive the dammages
     * @param dammage used to calculate the amount of received damage
     * @param numberOfStars depends on the type of cell the attacked unit is on and it is used to calculate the actual dammage the unit receives
     */
    public void attack(int indexOfUnitToAttack, int dammage, int numberOfStars){
        ArrayList<Units> units = getUnits();
        Units unitToAttack = units.get(indexOfUnitToAttack);
        unitToAttack.receivesDammage(dammage -numberOfStars);
    }
}
