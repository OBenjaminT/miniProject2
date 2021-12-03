package ch.epfl.cs107.play.game.icwars;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.AreaGame;
import ch.epfl.cs107.play.game.areagame.actor.Orientation;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsActor;
import ch.epfl.cs107.play.game.icwars.actor.RealPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Soldier;
import ch.epfl.cs107.play.game.icwars.actor.Tank;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.game.icwars.area.Level0;
import ch.epfl.cs107.play.game.icwars.area.Level1;
import ch.epfl.cs107.play.game.tutosSolution.actor.GhostPlayer;
import ch.epfl.cs107.play.game.tutosSolution.actor.SimpleGhost;
import ch.epfl.cs107.play.game.tutosSolution.area.Tuto2Area;
import ch.epfl.cs107.play.game.tutosSolution.area.tuto2.Ferme;
import ch.epfl.cs107.play.game.tutosSolution.area.tuto2.Village;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Window;

public class ICWars extends AreaGame {

    private int areaIndex;
    private RealPlayer player;
    private Tank tank;
    private Soldier soldier;

    private final String[] areas = {"icwars/Level0", "icwars/Level1"};


    private void createAreas(){
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

        ICWarsArea area = (ICWarsArea)setCurrentArea(areaKey, true);
        DiscreteCoordinates coords = area.getRealPlayerSpawnPosition();
        tank = new Tank(area, area.getTankSpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        soldier = new Soldier(area, area.getSoldierSpawnPosition(), ICWarsActor.Faction.ALLY, 5, 10);
        player = new RealPlayer(area, coords, ICWarsActor.Faction.ALLY, tank, soldier);
        player.enterArea(area, coords);
        player.centerCamera();

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public String getTitle() {
        return "ICWars";
    }

    private void initArea(String areaKey) {
        setCurrentArea(areaKey, true);
    }

    @Override
    public void end() {
    }
}
