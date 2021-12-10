package ch.epfl.cs107.play.game.icwars.actor.units;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

public class Tank extends Units {
    static int TankMaxHP = 10;
    static int TankRadius = 4;
    static int TankDamage = 7;

    /**
     * @param area     the area in which the unit is
     * @param position position of the unit in the area
     * @param faction  faction to which the units belong (eiter ALLY or ENEMY
     * @param repair   the amount that the unit can increase its HP
     * @param hp       the number of HP a unit has
     *                 the sprite of the tank is also initiated
     */
    public Tank(Area area, DiscreteCoordinates position, Faction faction, int repair, int hp) {
        super(area, position, faction, repair, TankRadius, hp, TankMaxHP);
        this.sprite = new Sprite(this.getName(),
            1.5f,
            1.5f,
            this,
            null,
            new Vector(-0.25f, -0.25f));
    }

    @Override
    public void acceptInteraction(AreaInteractionVisitor v) {
        //TODO define this
    }

    @Override
    protected int getDamage() {
        return TankDamage;
    }

    /**
     * @return the name of the tank depending on its faction (it will be useful to draw the sprite in the constructor)
     */
    @Override
    protected String getName() {
        if (this.faction == Faction.ALLY) return "icwars/friendlyTank";
        else return "icwars/enemyTank";

    }
}
