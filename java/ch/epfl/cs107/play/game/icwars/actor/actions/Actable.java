package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.actor.Graphics;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.window.Keyboard;

public interface Actable extends Graphics {
    void doAction(float dt, ICWarsPlayer player, Keyboard keyboard);
}
