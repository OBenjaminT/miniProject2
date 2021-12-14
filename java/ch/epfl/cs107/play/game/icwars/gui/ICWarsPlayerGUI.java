package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.area.ICWarsBehavior;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

/**
 * TODO
 */
public class ICWarsPlayerGUI implements Graphics {

    /**
     * TODO
     */
    ICWarsPlayer player;

    /**
     * TODO
     */
    Unit PlayerSelectedUnit;

    /**
     * TODO
     */
    float cameraScaleFactor;

    /**
     * TODO
     */
    private int NumberOfStarsOfCurrentCell;

    /**
     * TODO
     */
    private ICWarsBehavior.ICWarsCellType TypeOfCurrentCell;

    /**
     * TODO
     */
    private Unit unitOnCell;

    /**
     * TODO
     *
     * @param cameraScaleFactor
     * @param player
     */
    public ICWarsPlayerGUI(float cameraScaleFactor, ICWarsPlayer player) {
        this.player = player;
        this.cameraScaleFactor = cameraScaleFactor;
    }

    /**
     * TODO
     *
     * @param numberOfStarsOfCurrentCell
     */
    public void setNumberOfStarsOfCurrentCell(int numberOfStarsOfCurrentCell) {
        NumberOfStarsOfCurrentCell = numberOfStarsOfCurrentCell;
    }

    /**
     * TODO
     *
     * @param typeOfCurrentCell
     */
    public void setTypeOfCurrentCell(ICWarsBehavior.ICWarsCellType typeOfCurrentCell) {
        TypeOfCurrentCell = typeOfCurrentCell;
    }

    /**
     * TODO
     *
     * @param unitOnCell
     */
    public void setUnitOnCell(Unit unitOnCell) {
        this.unitOnCell = unitOnCell;
    }

    /**
     * TODO
     *
     * @param playerSelectedUnit is given as a value to the ICWarsPlayerGUI' playerSelectedUnit
     */
    public void setPlayerSelectedUnit(Unit playerSelectedUnit) {
        this.PlayerSelectedUnit = playerSelectedUnit;
    }

    /**
     * TODO
     *
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (PlayerSelectedUnit != null && PlayerSelectedUnit.hasNotAlreadyMoved()) {
            PlayerSelectedUnit
                .drawRangeAndPathTo(
                    new DiscreteCoordinates((int) player.getPosition().x, (int) player.getPosition().y),
                    canvas
                );
        }
    }
}