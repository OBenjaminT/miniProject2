package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

abstract public class Units extends ICWarsActor {

    // data
    protected int current_HP;
    protected int maxHP;
    protected int repair;
    protected int radius;
    // ui
    protected String name;
    protected Sprite sprite;

    /**
     * @param area       the area in yhich the unit is
     * @param position   position of the unit in the area
     * @param faction    faction to which the units belong (eiter ALLY or ENNEMY
     * @param repair     the amount that the unit can increase its HP
     * @param radius     the maximum distance for moving
     * @param current_HP the number of HP a unit has
     * @param maxHP      the maximum HP the unit has
     */
    public Units(Area area, DiscreteCoordinates position, Faction faction, int repair, int radius, int current_HP, int maxHP) {
        super(area, position, faction);
        this.maxHP = maxHP;
        this.radius = radius;
        this.setHp(current_HP);
        this.repair = repair;
    }

    protected abstract int getDammage();

    /**
     * if the HP are given negative, they are set to 0
     * if they are given above maxHP, they are set to maxHP
     * else they are set to the given @param hp
     *
     * @param HP the hp the unit has
     */
    public void setHp(int HP) {
        this.current_HP = HP < 0 ? 0 : Math.min(HP, maxHP);
    }

    /**
     * @return true if the unit's hp are positive
     */
    public boolean isAlive() {
        return this.current_HP > 0;
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * @return unit name
     */
    protected String getName() {
        return this.name;
    }


    /**
     * a unit doesn't take spaceCellSpace
     */
    @Override
    public boolean takeCellSpace() {
        return false;
    }

    /**
     * a unit is not Viewinteractable
     */
    @Override
    public boolean isViewInteractable() {
        return false;
    }

    /**
     * a unit is CellInteractable
     */
    @Override
    public boolean isCellInteractable() {
        return true;
    }
}
