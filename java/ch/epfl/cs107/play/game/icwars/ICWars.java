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

public class ICWars extends AreaGame {

    private final String[] areas = {"icwars/Level0", "icwars/Level1"};
    States gameState;
    private int areaIndex;
    private Keyboard keyboard;


    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Levels
            this.resetAreas();
            Arrays.stream(new ICWarsArea[]{
                new Level0(),
                new Level1(),
            }).forEach(this::addArea);

            keyboard = window.getKeyboard();
            initArea(areas[0]);
            gameState = States.INIT;
            return true;
        } else return false;
    }

    private void initArea(String areaKey) {
        try (var area = (ICWarsArea) setCurrentArea(areaKey, true)) {
            this.resetPlayers();
            var coordinates = area.getAllyCenter();

            Arrays.stream(new ICWarsPlayer[]{
                createAITeam(area, coordinates),
                createPlayerTeam(area, coordinates),
            }).forEach(player -> {
                player.enterArea(area, coordinates); // change to get center
                players.add(player);
            });
        }
    }

    private ICWarsPlayer createPlayerTeam(ICWarsArea area, DiscreteCoordinates coordinates) {
        Tank enemyTank = new Tank(area, area.getFreeEnemySpawnPosition(), ICWarsActor.Faction.ENEMY, 5, 10);
        Soldier enemySoldier = new Soldier(area, area.getFreeEnemySpawnPosition(), ICWarsActor.Faction.ENEMY, 5, 10);
        return new RealPlayer(area, coordinates, ICWarsActor.Faction.ENEMY, enemyTank, enemySoldier);
    }

    private ICWarsPlayer createAITeam(ICWarsArea area, DiscreteCoordinates coordinates) {
        Tank allyTank = new Tank(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        Soldier allySoldier = new Soldier(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        return new RealPlayer(area, coordinates, ICWarsActor.Faction.ALLY, allyTank, allySoldier);
    }

    private void resetGameState() {
        this.areaIndex = 0;
        this.begin(this.getWindow(), this.getFileSystem());
    }

    @Override
    public void update(float deltaTime) {
        // Next level with `N`
        if (keyboard.get(Keyboard.N).isReleased())
            changeIfNPressed();
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
                initArea(areas[1]);
                yield gameState;
            }
        };
        super.update(deltaTime);
    }

    /**
     * if the button "N" is pressed,
     * if the current area isn't the last area :
     * the real player leaves the area,
     * the area is changed to the next in the area list
     * the player enters the new area
     * else:
     * print "game over"
     */
    private void changeIfNPressed() {
        if (areaIndex != areas.length - 1) {
            ++areaIndex;
            players.forEach(ICWarsPlayer::leaveArea);
            try (var currentArea = (ICWarsArea) setCurrentArea(areas[areaIndex], true)) {
                players.get(0).enterArea(currentArea, currentArea.getAllyCenter());
            }
            players.get(0).centerCamera();
        } else System.out.println("Game over");
    }

    @Override
    public String getTitle() {
        return "ICWars";
    }

    @Override
    public void close() {
    }

    /**
     * states that an `ICWars` can be in
     */
    public enum States {
        INIT,
        CHOOSE_PLAYER,
        START_PLAYER_TURN,
        PLAYER_TURN,
        END_PLAYER_TURN,
        END_TURN,
        END,
    }
}
