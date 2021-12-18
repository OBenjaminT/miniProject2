package ch.epfl.cs107.play.game.icwars.actor.units;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.actor.Sprite;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.game.icwars.actor.actions.Attack;
import ch.epfl.cs107.play.game.icwars.actor.actions.Wait;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.math.Vector;

import java.util.ArrayList;

public class Soldier extends Unit {

    /**
     * TODO
     */
    final Wait SoldierWait;

    /**
     * TODO
     */
    final Attack SoldierAttack;

    /**
     * TODO
     *
     * @param area     the area in which the unit is
     * @param position position of the unit in the area
     * @param faction  faction to which the units belong (eiter ALLY or ENEMY
     */
    public Soldier(Area area, DiscreteCoordinates position, Faction faction) {
        this(area, position, faction, 5, 5);
    }

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
        super(area, position, faction, repair, 2, hp, 5, 7, "Soldier");
        this.sprite = new Sprite(
            this.getSpriteName(),
            1.5f,
            1.5f,
            this,
            null,
            new Vector(-0.25f, -0.25f)
        );
        this.actions = new ArrayList<>();
        this.SoldierWait = new Wait(this, this.getOwnerArea());
        this.actions.add(SoldierWait);
        this.SoldierAttack = new Attack(this, this.getOwnerArea());
        this.actions.add(SoldierAttack);
    }

/*    *//**
     * TODO
     *
     * @param areaInteractionVisitor (AreaInteractionVisitor) : the visitor
     *//*
    @Override
    public void acceptInteraction(AreaInteractionVisitor areaInteractionVisitor) {
        //TODO define this
    }*/

    /**
     * TODO
     *
     * @return the name of the soldier depending on its faction (it will be useful to draw the sprite in the constructor)
     */
    protected String getSpriteName() {
        if (this.faction.equals(Faction.ALLY)) return "icwars/friendlySoldier";
        else return "icwars/enemySoldier";
    }

    @Override
    public Attack getAttackAction(){
        return this.SoldierAttack;
    }

}
