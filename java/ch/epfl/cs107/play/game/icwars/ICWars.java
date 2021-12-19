package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.*;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.level.Level0;
import ch.epfl.cs107.play.game.icwars.area.level.Level1;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * TODO
 */
public class ICWars extends AreaGame {

    /**
     * The {@link States States} that the {@link ICWars} game is currently in.
     */
    private States gameState;

    /**
     * The {@link Keyboard} used to interact with the {@link ICWars} game.
     */
    private Keyboard keyboard;


    /**
     * Start the {@link ICWars} game. Can fail (return {@code false}).
     * <p>
     * It does these things, in this order:
     * <li>Call {@link AreaGame#begin(Window, FileSystem)}.</li>
     * <li>Get the {@link #keyboard}.</li>
     * <li>Call {@link #resetAreas()} which empties the list of areas.</li>
     * <li>Initialise and then call {@link #addArea(Area)} on all of the levels in {@link ICWars}.</li>
     * <li>Call {@link #nextArea()} to set {@link #currentArea} to the first level, then initialise it.</li>
     *
     * @param window     The {@link Window} that the game is displayed in.
     * @param fileSystem The {@link FileSystem} that the game gets its visual resources from.
     * @return {@code true} if the game successfully started.
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            keyboard = window.getKeyboard();

            this.resetAreas();
            Arrays.stream(new ICWarsArea[]{
                new Level0(),
                new Level1(),
            }).forEach(this::addArea);
            if (this.nextArea()) initArea();

            return true;
        } else return false;
    }

    /**
     * Overloads {@link #initArea(ICWarsArea)}.
     * <p>
     * Call {@link #initArea(ICWarsArea) initArea} on the {@link #currentArea currentArea}.
     */
    private void initArea() {
        initArea((ICWarsArea) currentArea);
    }

    /**
     * <p> Initialise an {@link ICWarsArea} by: </p>
     *
     * <li>removing all {@link #players} (call {@link #resetPlayers()});</li>
     * <li>initialising the {@link ICWarsPlayer}s and their units;</li>
     * <li>adding each player to the {@link #players players list} and making then
     * {@link ICWarsPlayer#enterArea(Area, DiscreteCoordinates) enter the area};</li>
     * <li>Setting {@link #gameState} to {@link States#INIT INIT}</li>
     *
     * @param area The {@link ICWarsArea} object to initialise.
     */
    private void initArea(ICWarsArea area) {
        this.resetPlayers();
        Arrays.stream(new ICWarsPlayer[]{
            new RealPlayer(
                area,
                area.getFactionCenter(ICWarsActor.Faction.ALLY),
                ICWarsActor.Faction.ALLY,
                area.factionUnits(ICWarsActor.Faction.ALLY).toArray(new Unit[0])
            ),
            new AIPlayer(
                area,
                area.getFactionCenter(ICWarsActor.Faction.ENEMY),
                ICWarsActor.Faction.ENEMY,
                area.factionUnits(ICWarsActor.Faction.ENEMY).toArray(new Unit[0])
            ),
        }).forEach(player -> {
            players.add(player);
            player.enterArea(
                area,
                area.getFactionCenter(player.faction)
            ); // change to get center
        });
        gameState = States.INIT;
    }

    /**
     * Process all the input keys.
     */
    private void processKeyboardInput() {
        // Next level with `N`
        if (keyboard.get(Keyboard.N).isReleased())
            if (this.nextArea()) initArea();
        // Reset to start with `R`
        if (keyboard.get(Keyboard.R).isReleased())
            this.begin(this.getWindow(), this.getFileSystem());
        // Select first unit with `U`
        if (keyboard.get(Keyboard.U).isReleased())
            players.get(0).selectUnit(0); // 0, 1 ...
        // TODO: Close with `Q`
        if (keyboard.get(Keyboard.Q).isReleased())
            this.getWindow().isCloseRequested();
    }

    /**
     * First calls {@link #processKeyboardInput()}, then, depending on the current {@link #gameState}, update the game
     * accordingly. Then call {@link AreaGame#update(float)}.
     *
     * @param deltaTime The time since the last call to this function.
     */
    @Override
    public void update(float deltaTime) {
        processKeyboardInput();
        this.gameState = switch (gameState) {
            case INIT -> {
                PlayersWaitingForCurrentTurn.addAll(
                    players.stream()
                        .filter(p -> !p.isDefeated()) // add only non-defeated players
                        .collect(Collectors.toList()));
                yield States.CHOOSE_PLAYER;
            }
            case CHOOSE_PLAYER -> {
                if (!PlayersWaitingForCurrentTurn.isEmpty()) {
                    // chose the next player that has to play in the current turn
                    // remove it from the list of players that wait for a turn
                    this.activePlayer = PlayersWaitingForCurrentTurn.remove(0);
                    yield States.START_PLAYER_TURN;
                } else yield States.END_TURN;
            }
            case START_PLAYER_TURN -> {
                activePlayer.startTurn();
                activePlayer.centerCamera();
                yield States.PLAYER_TURN;
            }
            case PLAYER_TURN -> {
                //activePlayer.update(deltaTime);needs to be removed!!!! (else the palyer is updated 2 times)
                yield activePlayer.isIdle()
                    ? States.END_PLAYER_TURN
                    : gameState;
            }
            case END_PLAYER_TURN -> {
                if (!activePlayer.isDefeated()) {
                    activePlayer.endTurn(); // TODO reset all the players units movement
                    PlayersWaitingForNextTurn.add(activePlayer);
                } else activePlayer.leaveArea();

                yield States.CHOOSE_PLAYER; // it said only change like this in the first branch but nothing for the second
            }
            case END_TURN -> {
                players.removeIf(ICWarsPlayer::isDefeated);

                PlayersWaitingForNextTurn.stream()
                    .filter(ICWarsPlayer::isDefeated)
                    .forEach(player -> players.remove(player));

                if (PlayersWaitingForNextTurn.size() != 1) {
                    PlayersWaitingForCurrentTurn.addAll(PlayersWaitingForNextTurn);
                    yield States.CHOOSE_PLAYER;
                } else yield States.END;
            }
            case END -> {
                if (this.nextArea()) initArea();
                yield gameState;
            }
        };
        super.update(deltaTime);
    }

    /**
     * Returns the title of the {@link ICWars} game.
     *
     * @return Currently {@code "ICWars"}
     */
    @Override
    public String getTitle() {
        return "ICWars";
    }

    /**
     * No-op. Used to implement {@link AutoCloseable}.
     */
    @Override
    public void close() {
    }

    /**
     * States that an {@link ICWars} game can be in.
     */
    private enum States {
        /**
         * TODO
         */
        INIT,

        /**
         * TODO
         */
        CHOOSE_PLAYER,

        /**
         * TODO
         */
        START_PLAYER_TURN,

        /**
         * TODO
         */
        PLAYER_TURN,

        /**
         * TODO
         */
        END_PLAYER_TURN,

        /**
         * TODO
         */
        END_TURN,

        /**
         * TODO
         */
        END,
    }
}
