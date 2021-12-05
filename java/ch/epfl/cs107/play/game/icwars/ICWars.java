package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
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

public class ICWars extends AreaGame {

    private final String[] areas = {"icwars/Level0", "icwars/Level1"};
    private int areaIndex;
    private RealPlayer player;
    private Tank tank;
    private Soldier soldier;

    private void createAreas() {
        addArea(new Level0());
        addArea(new Level1());
    }

    @Override
    public boolean begin(Window window, FileSystem fileSystem) {
        if (super.begin(window, fileSystem)) {
            createAreas();
            int areaIndex = 0;
            initArea(areas[areaIndex]);
            return true;
        }
        return false;
    }

    private void initArea(String areaKey) {
        ICWarsArea area = (ICWarsArea) setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getAllyCenter();
        tank = new Tank(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        soldier = new Soldier(area, area.getFreeAllySpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        player = new RealPlayer(area, coords, ICWarsActor.Faction.ALLY, tank, soldier);
        player.enterArea(area, coords);
        player.centerCamera();
    }

    @Override
    public void update(float deltaTime) {
        changeIfNPressed();
        resetIfRPressed();
        super.update(deltaTime);
    }

    /**
     * if the button "N" is pressed,
     * if the current area isn't the last area :
     *  the real player leaves the area,
     *  the area is changed to the next in the area list
     *  the player enters the new area
     * else:
     *  print "game over"
     */
    private void changeIfNPressed() {
        if (getWindow().getKeyboard().get(Keyboard.N).isReleased())
            if (areaIndex != areas.length - 1) {
                ++areaIndex;
                player.leaveArea();
                try (var currentArea = (ICWarsArea) setCurrentArea(areas[areaIndex], true)) {
                    player.enterArea(currentArea, currentArea.getAllyCenter());
                }
                player.centerCamera();
            } else System.out.println("Game over");
    }

    /**
     * if the button "N" is pressed,
     * the game restarts in the same conditions as it initially started
     */
    private void resetIfRPressed() {
        if (getWindow().getKeyboard().get(Keyboard.R).isReleased())
            this.begin(this.getWindow(), this.getFileSystem());
    }

    @Override
    public String getTitle() {
        return "ICWars";
    }

    @Override
    public void close() {
    }
}
