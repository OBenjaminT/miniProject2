package ch.epfl.cs107.play.game.icwars.actor.units;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.actor.actions.Attack;
import ch.epfl.cs107.play.game.icwars.actor.actions.Wait;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.ArrayList;

/**
 * TODO
 */
public class Tank extends Unit {


    /**
     * TODO
     */
    final Wait wait;

    /**
     * TODO
     */
    final Attack attack;

    /**
     * TODO
     *  @param area     the area in which the unit is
     *
     * @param position position of the unit in the area
     * @param faction  faction to which the units belong (eiter ALLY or ENEMY
     */
    public Tank(Area area, DiscreteCoordinates position, Faction faction) {
        this(area, position, faction, 5, 10);
    }

    /**
     * TODO
     *
     * @param area     the area in which the unit is
     * @param position position of the unit in the area
     * @param faction  faction to which the units belong (eiter ALLY or ENEMY
     * @param repair   the amount that the unit can increase its HP
     * @param hp       the number of HP a unit has
     *                 the sprite of the tank is also initiated
     */
    public Tank(Area area, DiscreteCoordinates position, Faction faction, int repair, int hp) {
        // TODO comments

        super(area, position, faction, repair, 4, hp, 10, 7);
        this.sprite = new Sprite(
            this.getSpriteName(),
            1.5f,
            1.5f,
            this,
            null,
            new Vector(-0.25f, -0.25f)
        );
        this.actions = new ArrayList<>();
        this.wait = new Wait(this, this.getOwnerArea());
        this.actions.add(wait);
        this.attack = new Attack(this, this.getOwnerArea());
        this.actions.add(attack);
    }

    /**
     * TODO
     *
     * @param areaInteractionVisitor (AreaInteractionVisitor) : the visitor
     */
    @Override
    public void acceptInteraction(AreaInteractionVisitor areaInteractionVisitor) {
        //TODO define this
    }

    /**
     * TODO
     *
     * @return the name of the tank depending on its faction (it will be useful to draw the sprite in the constructor)
     */

    protected String getSpriteName() {
        if (this.faction.equals(Faction.ALLY)) return "icwars/friendlyTank";
        else return "icwars/enemyTank";
    }
}
