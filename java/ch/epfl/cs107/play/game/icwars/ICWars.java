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

    //units for ally player
    private Tank allyTank;
    private Soldier allySoldier;

    //units for enemyplayer
    private Soldier ennemySoldier;
    private Tank ennemyTank;


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
            allyTank = new Tank(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
            allySoldier = new Soldier(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);

            ennemyTank = new Tank(area, area.getFreeEnnemySpawnPosition(), ICWarsActor.Faction.ENEMY, 5, 10);
            ennemySoldier = new Soldier(area, area.getFreeEnnemySpawnPosition(), ICWarsActor.Faction.ENEMY, 5, 10);

            // Players
            RealPlayer AllyPlayer = new RealPlayer(area, coords, ICWarsActor.Faction.ALLY, allyTank, allySoldier);
            AllyPlayer.enterArea(area, area.getAllyCenter());
            players.add(AllyPlayer);

            RealPlayer EnnemyPlayer = new RealPlayer(area, coords, ICWarsActor.Faction.ENEMY, ennemyTank,ennemySoldier );
            EnnemyPlayer.enterArea(area, area.getEnnemyCenter());
            players.add(EnnemyPlayer);

            Arrays.stream(new ICWarsPlayer[]{
                    new RealPlayer(area, coords, ICWarsActor.Faction.ALLY, allyTank, allySoldier),
            }).forEach(player -> {
                player.enterArea(area, coords);
                players.add(player);
            });
        }
    }

    public ICWarsPlayer getActivePlayer() {
        return PlayersWaitingForCurrentTurn.get(0);
    }

    @Override
    public void update(float deltaTime) {
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
                if( PlayersWaitingForCurrentTurn.isEmpty()){
                    yield States.END_TURN;
                }
                else{
                    //chose the next playr that has to play in the current turn
                    //remove it from the lsit of players taht wait for a turn
                    this.activePlayer=PlayersWaitingForCurrentTurn.get(0);
                    PlayersWaitingForCurrentTurn.remove(this.activePlayer);
                    yield States.START_PLAYER_TURN;
                }
//                activePlayers.isEmpty()
//                        ? States.END_TURN
//                        : States.START_PLAYER_TURN;
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
            } // loops forever?
            case END_PLAYER_TURN -> {
                var player = activePlayer;
                if (!player.isDefeated()) {
                    player.endTurn(); // TODO reset all the players units movement
                    PlayersWaitingForNextTurn.add(player);
                    PlayersWaitingForCurrentTurn.remove(player);
                } else {
                    PlayersWaitingForCurrentTurn.remove(player);
                    player.leaveArea();
                }; // remove him from the playing area

                yield States.CHOOSE_PLAYER; // it said only change like this in the first branch but nothing for the second
            }
            case END_TURN -> /*(2 > players.stream().filter(p -> !p.isDefeated()).count())
                ? States.END
                : States.INIT;*/
                    {
                        for(ICWarsPlayer player: players){
                            if(player.isDefeated()){
                                players.remove(player);
                            }
                        }

                        for(ICWarsPlayer player: PlayersWaitingForNextTurn){
                            if(player.isDefeated()){
                                players.remove(player);
                            }
                        }

                        if(PlayersWaitingForNextTurn.size()==1){
                            yield States.END;
                        }
                        else{
                            for(ICWarsPlayer player: PlayersWaitingForNextTurn){
                                PlayersWaitingForCurrentTurn.add(player);
                            }
                            yield States.CHOOSE_PLAYER;
                        }
                    }
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
