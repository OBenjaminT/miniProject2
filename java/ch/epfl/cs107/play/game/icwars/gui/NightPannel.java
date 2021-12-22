package ch.epfl.cs107.play.game.icwars.gui;

import ch.epfl.cs107.play.game.actor.Actor;
import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.actor.ShapeGraphics;
import ch.epfl.cs107.play.game.icwars.area.ICWarsArea;
import ch.epfl.cs107.play.math.Polygon;
import ch.epfl.cs107.play.math.Shape;
import ch.epfl.cs107.play.math.Transform;
import ch.epfl.cs107.play.math.Vector;
import ch.epfl.cs107.play.window.Canvas;

import java.awt.*;

public class NightPannel implements Graphics, Actor {
    ICWarsArea area;
    private ShapeGraphics background;

    @Override
    public void draw(Canvas canvas) {
        if(this.area.isNight()){
            Shape rect = new Polygon(0, 0, 0, area.getCameraScaleFactor(), area.getCameraScaleFactor(), area.getCameraScaleFactor(), area.getCameraScaleFactor(), 0);
            background = new ShapeGraphics(rect, Color.DARK_GRAY, Color.BLACK, 0f, 0.7f, 3000f);
            background.draw(canvas);
        }
    }

    public NightPannel(ICWarsArea area){
        this.area=area;
    }

    @Override
    public Transform getTransform() {
        return null;
    }

    @Override
    public Vector getVelocity() {
        return null;
    }
}
