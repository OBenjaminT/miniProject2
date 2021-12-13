package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

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
     *
     * @param cameraScaleFactor
     * @param player
     */
    public ICWarsPlayerGUI(float cameraScaleFactor,
                           ICWarsPlayer player) {
        this.player = player;
        this.cameraScaleFactor = cameraScaleFactor;
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
        if (PlayerSelectedUnit == null) return;
        if (PlayerSelectedUnit.hasNotAlreadyMoved())
            PlayerSelectedUnit
                .drawRangeAndPathTo(
                    new DiscreteCoordinates((int) player.getPosition().x, (int) player.getPosition().y),
                    canvas
                );

    }
}
