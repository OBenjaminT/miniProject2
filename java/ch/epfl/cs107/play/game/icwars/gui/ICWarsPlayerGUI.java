package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class ICWarsPlayerGUI implements Graphics {
    ICWarsPlayer player;
    Unit PlayerSelectedUnit;
    float cameraScaleFactor;
    private int NumberOfStarsOfCurrentCell;
    private ICWarsBehavior.ICWarsCellType TypeOfCurrentCell;
    private Unit unitOnCell;

    public void setNumberOfStarsOfCurrentCell(int numberOfStarsOfCurrentCell) {
        NumberOfStarsOfCurrentCell = numberOfStarsOfCurrentCell;
    }

    public void setTypeOfCurrentCell(ICWarsBehavior.ICWarsCellType typeOfCurrentCell) {
        TypeOfCurrentCell = typeOfCurrentCell;
    }

    public void setUnitOnCell(Unit unitOnCell) {
        this.unitOnCell = unitOnCell;
    }


    public ICWarsPlayerGUI(float cameraScaleFactor,
                           ICWarsPlayer player) {
        this.player = player;
        this.cameraScaleFactor = cameraScaleFactor;
    }

    /**
     * @param playerSelectedUnit is given as a value to the ICWarsPlayerGUI' playerSelectedUnit
     */
    public void setPlayerSelectedUnit(Unit playerSelectedUnit) {
        this.PlayerSelectedUnit = playerSelectedUnit;
    }

    @Override
    public void draw(Canvas canvas) {
        if (PlayerSelectedUnit == null) return;
        if (PlayerSelectedUnit.hasNotAlreadyMoved())
            PlayerSelectedUnit
                    .drawRangeAndPathTo(
                            new DiscreteCoordinates((int) player.getPosition().x, (int) player.getPosition().y),
                            canvas
                    );

    }
}