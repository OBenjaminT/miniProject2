package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

public class ICWarsPlayerGUI implements Graphics {
    ICWarsPlayer player;
    Units PlayerSelectedUnit;
    float cameraSclareFactor;



    public ICWarsPlayerGUI(float cameraScaleFactor ,
                           ICWarsPlayer player){
        this.player=player;
        this.cameraSclareFactor= cameraScaleFactor;
    }

    /**
     * @param playerSelectedUnit is given as a value to the ICWarsPlayerGUI' playerSelectedUnit
     */
    public void setPlayerSelectedUnit(Units playerSelectedUnit) {
        this.PlayerSelectedUnit = playerSelectedUnit;
    }

    @Override
    public void draw(Canvas canvas) {
        PlayerSelectedUnit.drawRangeAndPathTo(new DiscreteCoordinates((int) player.getPosition().x, (int) player.getPosition().y), canvas);
    }
}
