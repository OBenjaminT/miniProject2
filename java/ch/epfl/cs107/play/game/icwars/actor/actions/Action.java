package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.Units;

abstract class Action implements Actable {
    protected final Units unit;
    protected final Area area;
    protected final String name;
    protected final int key;

    public Action(Units unit, Area area, String name, int key) {
        this.unit = unit;
        this.area = area;
        this.name = name;
        this.key = key;
    }
}
