package ch.epfl.cs107.play.game.icwars.actor;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Canvas;

abstract public class Units extends ICWarsActor{

    protected int hp;

    protected int maxHP;

    protected String name;

    protected Sprite sprite;


    protected abstract int getDammage();

    protected int repair;

    protected int radius;



    /**
     * @param area the area in yhich the unit is
     * @param position position of the unit in the area
     * @param faction faction to which the units belong (eiter ALLY or ENNEMY
     * @param repair the amount that the unit can increase its HP
     * @param radius the maximum distance for moving
     * @param hp the number of HP a unit has
     * @param maxHP the maximum HP the unit has
     */

    public Units(Area area, DiscreteCoordinates position, Faction faction, int repair, int radius, int hp, int maxHP) {
        super(area, position, faction);
        this.radius = radius;
        this.setHp(hp);
        this.repair = repair;
        this.maxHP = maxHP;
    }

    /**
     * @param hp the hp the unt has
     * if the HP are given negative, they are set to 0
     * if they are given above maxHP, they are set to maxHP
     * else they are set to the given @param hp
     */
    public void setHp(int hp) {
        if(hp<0) this.hp = 0;
        else if(hp>maxHP) this.hp=maxHP;
        else this.hp =hp;
    }

    @Override
    public void draw(Canvas canvas) {
        sprite.draw(canvas);
    }

    /**
     * @return true if the unit's hp are positive
     */
    public boolean isAlive (){
        return this.hp>0;
    }

    /**
     * @return unit name
     */
    protected String getName (){
        return this.name;
    }

    @Override
    /**
     * a unit doesn't take spaceCellSpace
     */
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
