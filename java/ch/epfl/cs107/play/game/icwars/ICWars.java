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
import ch.epfl.cs107.play.window.Keyboard;
import ch.epfl.cs107.play.window.Window;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ICWars extends AreaGame {

    private final String[] areas = {"icwars/Level0", "icwars/Level1"};
    States gameState;
    private int areaIndex;
    private Tank tank;
    private Soldier soldier;
    private Keyboard keyboard;


    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            // Levels
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
            var coords = area.getAllyCenter();
            tank = new Tank(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
            soldier = new Soldier(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);

            // Players
            Arrays.stream(new ICWarsPlayer[]{
                new RealPlayer(area, coords, ICWarsActor.Faction.ALLY, tank, soldier),
            }).forEach(player -> {
                player.enterArea(area, coords);
                players.add(player);
            });
        }
    }

    @Override
    public void update(float deltaTime) {
        // convention: the first player in `activePlayers` is the one whose turn it is
        this.gameState = switch (gameState) {
            case INIT -> {
                activePlayers.addAll(
                    players.stream()
                        .filter(p -> !p.isDefeated()) // add only non-defeated players
                        .collect(Collectors.toList()));
                yield States.CHOOSE_PLAYER;
            }
            case CHOOSE_PLAYER -> activePlayers.isEmpty()
                ? States.END_TURN
                : States.START_PLAYER_TURN;
            case START_PLAYER_TURN -> {
                activePlayer = activePlayers.get(0);
                activePlayer.startTurn();
                activePlayer.centerCamera();
                yield States.PLAYER_TURN;
            }
            case PLAYER_TURN -> {
                activePlayers.get(0).update(deltaTime);
                yield activePlayers.get(0).isIdle()
                    ? States.PLAYER_TURN
                    : gameState;
            } // loops forever?
            case END_PLAYER_TURN -> {
                var player = activePlayers.get(0);
                if (player.isDefeated())
                    player.leaveArea(); // remove him from the playing area
                else {
                    // player.endTurn(); // TODO reset all the players units movement
                    activePlayers.remove(player);
                }
                yield States.CHOOSE_PLAYER; // it said only change like this in the first branch but nothing for the second
            }
            case END_TURN -> (2 > players.stream().filter(p -> !p.isDefeated()).count())
                ? States.END
                : States.INIT;
            case END -> {
                yield gameState;
            }
        };
        // Next level with `N`
        if (keyboard.get(Keyboard.N).isReleased())
            changeIfNPressed();
        // Reset to start with `R`
        if (keyboard.get(Keyboard.R).isReleased())
            this.begin(this.getWindow(), this.getFileSystem());
        // Select first unit with `U`
        if (keyboard.get(Keyboard.U).isReleased())
            players.get(0).selectUnit(0); // 0, 1 ...
        // TODO: Close with `Q`
        if (keyboard.get(Keyboard.Q).isReleased())
            this.getWindow().isCloseRequested();
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
