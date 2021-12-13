package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.units.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.units.Tank;
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
     * TODO
     */
    private final String[] areas = {"icwars/Level0", "icwars/Level1"};

    /**
     * TODO
     */
    States gameState;

    /**
     * TODO
     */
    private Keyboard keyboard;


    /**
     * TODO
     *
     * @param window
     * @param fileSystem
     * @return
     */
    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        // begin game
        // reset all fields about the game
        // add the levels
        // set teh keyboard
        // initialise the first area
        if (super.begin(window, fileSystem)) {
            // Levels
            this.resetAreas();
            Arrays.stream(new ICWarsArea[]{
                new Level0(),
                new Level1(),
            }).forEach(this::addArea);

            keyboard = window.getKeyboard();
            initArea(areas[0]);
            return true;
        } else return false;
    }

    /**
     * TODO
     * <p>
     * Call {@link #initArea(ICWarsArea) initArea} on the {@link #currentArea currentArea}.
     */
    private void initArea() {
        initArea((ICWarsArea) currentArea);
    }

    /**
     * TODO
     * <p>
     * Calls {@link #setCurrentArea setCurrentArea} with {@code areaKey} and then calls
     * {@link #initArea(ICWarsArea) initArea} on the result.
     *
     * @param areaKey A string identifying the area to initialise e.g. {@code "icwars/Level0"}
     */
    private void initArea(String areaKey) {
        try (var area = (ICWarsArea) setCurrentArea(areaKey, true)) {
            initArea(area);
        }
    }

    /**
     * TODO
     *
     * <p> Initialise an area by: </p>
     *
     * <li>removing all players;</li>
     * <li>initialising the players and their units;</li>
     * <li>adding each player to the players list;</li>
     * <li>making the player enter the area.</li>
     *
     * <p> Then set the game state to {@code States.INIT}. </p>
     *
     * @param area The area object to initialise.
     */
    private void initArea(ICWarsArea area) {
        this.resetPlayers();
        var coordinates = area.getAllyCenter();

        Arrays.stream(new ICWarsPlayer[]{
            createAITeam(area, coordinates),
            createPlayerTeam(area, coordinates),
        }).forEach(player -> {
            players.add(player);
            player.enterArea(area, coordinates); // change to get center
        });
        gameState = States.INIT;
    }

    /**
     * TODO
     *
     * @param area
     * @param coordinates
     * @return
     */
    private ICWarsPlayer createPlayerTeam(ICWarsArea area, DiscreteCoordinates coordinates) {
        Tank enemyTank = new Tank(area, area.getFreeEnemySpawnPosition(), ICWarsActor.Faction.ENEMY, 5, 10);
        Soldier enemySoldier = new Soldier(area, area.getFreeEnemySpawnPosition(), ICWarsActor.Faction.ENEMY, 5, 10);
        return new RealPlayer(area, coordinates, ICWarsActor.Faction.ENEMY, enemyTank, enemySoldier);
    }

    /**
     * TODO
     *
     * @param area
     * @param coordinates
     * @return
     */
    private ICWarsPlayer createAITeam(ICWarsArea area, DiscreteCoordinates coordinates) {
        Tank allyTank = new Tank(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        Soldier allySoldier = new Soldier(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        return new RealPlayer(area, coordinates, ICWarsActor.Faction.ALLY, allyTank, allySoldier);
    }

    /**
     * TODO
     */
    private void resetGameState() {
        this.begin(this.getWindow(), this.getFileSystem());
    }

    /**
     * TODO
     *
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime) {
        // Next level with `N`
        if (keyboard.get(Keyboard.N).isReleased())
            nextLevel();
        // Reset to start with `R`
        if (keyboard.get(Keyboard.R).isReleased())
            resetGameState();
        // Select first unit with `U`
        if (keyboard.get(Keyboard.U).isReleased())
            players.get(0).selectUnit(0); // 0, 1 ...
        // TODO: Close with `Q`
        if (keyboard.get(Keyboard.Q).isReleased())
            this.getWindow().isCloseRequested();
        // convention: the first player in `activePlayers` is the one whose turn it is
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
                activePlayer.update(deltaTime);
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
                this.nextLevel();
                initArea();
                yield gameState;
            }
        };
        super.update(deltaTime);
    }

    /**
     * TODO
     *
     * <p>
     * if the button "N" is pressed,
     * if the current area isn't the last area :
     * the real player leaves the area,
     * the area is changed to the next in the area list
     * the player enters the new area
     * else:
     * print "game over"
     */
    private void nextLevel() {
        if (this.nextArea()) {
            initArea();
            players.get(0).enterArea(currentArea, ((ICWarsArea) currentArea).getAllyCenter());
            players.get(0).centerCamera();
            this.gameState = States.INIT;
        }
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public String getTitle() {
        return "ICWars";
    }

    /**
     * TODO
     */
    @Override
    public void close() {
    }

    /**
     * TODO
     * <p>
     * states that an `ICWars` can be in
     */
    public enum States {
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
