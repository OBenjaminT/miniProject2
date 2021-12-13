package ch.epfl.cs107.play.game.icwars.actor.units;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

public class Soldier extends Units {

    /**
     * TODO
     */
    static int SoldierMaxHP = 10;

    /**
     * TODO
     */
    static int SoldierRadius = 2;

    /**
     * TODO
     */
    static int SoldierDamage = 7;

    /**
     * TODO
     *
     * @param area     the area in which the unit is
     * @param position position of the unit in the area
     * @param faction  faction to which the units belong (eiter ALLY or ENEMY
     * @param repair   the amount that the unit can increase its HP
     * @param hp       the number of HP a unit has
     *                 the sprite of the soldier is also initiated
     */
    public Soldier(Area area, DiscreteCoordinates position, Faction faction, int repair, int hp) {
        super(area, position, faction, repair, SoldierRadius, hp, SoldierMaxHP);
        this.sprite = new Sprite(this.getName(), 1.5f, 1.5f, this, null, new
            Vector(-0.25f, -0.25f));
    }

    /**
     * TODO
     *
     * @param v (AreaInteractionVisitor) : the visitor
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        //TODO define this
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    protected int getDamage() {
        return SoldierDamage;
    }

    /**
     * TODO
     *
     * @return the name of the soldier depending on its faction (it will be useful to draw the sprite in the constructor)
     */
    @Override
    protected String getName() {
        if (this.faction.equals(Faction.ALLY)) return "icwars/friendlySoldier";
        else return "icwars/enemySoldier";
    }
}
