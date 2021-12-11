package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class Attack extends Action {

    public Attack(Units unit, Area area, String name, int key) {
        super(unit, area, name, key);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {

    }
}
