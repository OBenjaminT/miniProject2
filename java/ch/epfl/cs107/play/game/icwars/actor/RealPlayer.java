package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.handler.ICWarsInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Button;
import ch.epfl.cs107.play.window.Keyboard;

public class RealPlayer extends ICWarsPlayer {
    /// Animation duration in frame number
    private final static int MOVE_DURATION = 6;
    private final ICWarsPlayerInteractionHandler handler = new ICWarsPlayerInteractionHandler(this);

    public RealPlayer(Area area, DiscreteCoordinates position, Faction faction, Units... units) {
        super(area, position, faction, units);
        this.sprite = new Sprite(this.getName(), 1.5f, 1.5f, this, null, new Vector(-0.25f, -0.25f));
    }

    /**
     * @return the sprite name
     */
    private String getName() {
        if (this.faction == Faction.ALLY) return "icwars/allyCursor";
        else return "icwars//enemyCursor";
    }

    @Override
    public void update(float deltaTime) {
        Keyboard keyboard = getOwnerArea().getKeyboard();
        //make sure the player can't move when it isn't his turn
        if(this.playerCurrentState!=States.IDLE) {
            if (RealPlayerCanMove()) {
                moveIfPressed(Orientation.LEFT, keyboard.get(Keyboard.LEFT));
                moveIfPressed(Orientation.UP, keyboard.get(Keyboard.UP));
                moveIfPressed(Orientation.RIGHT, keyboard.get(Keyboard.RIGHT));
                moveIfPressed(Orientation.DOWN, keyboard.get(Keyboard.DOWN));
            }
        }
        if (keyboard.get(Keyboard.Q).isDown()) getOwnerArea().close();
        super.update(deltaTime);

    }

    /**
     * Orientate and Move this player in the given orientation if the given button is down
     *
     * @param orientation (Orientation): given orientation, not null
     * @param b           (Button): button corresponding to the given orientation, not null
     */
    private void moveIfPressed(Orientation orientation, Button b) {
        if (b.isDown() && !isDisplacementOccurs()) {
            orientate(orientation);
            move(MOVE_DURATION);
            //this.EnterWasRealsed=false;
        }
    }

    @Override
    public void interactWith(Interactable other) {
        other.acceptInteraction(handler);
    }

    /**
     * @return true only if playerCurrentState = NORMAL, SELECT_UNIT or MOVE_UNIT
     */
    private boolean RealPlayerCanMove() {
        return this.playerCurrentState == States.NORMAL
            || this.playerCurrentState == States.SELECT_CELL
            || this.playerCurrentState == States.MOVE_UNIT;
    }

    private static class ICWarsPlayerInteractionHandler implements ICWarsInteractionVisitor {
        RealPlayer player;

        public ICWarsPlayerInteractionHandler(RealPlayer player) {
            this.player = player;
        }

        @Override
        public void interactWith(Units unit) {
            if (player.playerCurrentState == States.SELECT_CELL && unit.faction == player.faction)
                player.selectUnit(unit);
        }
    }
}
