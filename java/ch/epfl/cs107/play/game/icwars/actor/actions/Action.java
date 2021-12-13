package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.Units;

/**
 * TODO
 */
abstract class Action implements Actable {

    /**
     * TODO
     */
    protected final Units unit;

    /**
     * TODO
     */
    protected final Area area;

    /**
     * TODO
     */
    protected final String name;

    /**
     * TODO
     */
    protected final int key;

    /**
     * TODO
     *
     * @param unit
     * @param area
     * @param name
     * @param key
     */
    public Action(Units unit, Area area, String name, int key) {
        this.unit = unit;
        this.area = area;
        this.name = name;
        this.key = key;
    }
}
