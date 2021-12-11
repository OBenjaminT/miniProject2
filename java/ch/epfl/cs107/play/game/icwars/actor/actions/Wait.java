package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

public class Wait extends Action {
    public Wait(Units unit, Area area) {
        super(unit, area, "(W)ait", Keyboard.W);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        this.unit.setIsAlreadyMoved(true);
        player.setPlayerCurrentState(ICWarsPlayer.States.NORMAL);
    }
}
