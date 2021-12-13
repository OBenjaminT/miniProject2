package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;

public class Attack extends Action {

    public Attack(Units unit, Area area, String name, int key) {
        super(unit, area, name, key);
    }

    @Override
    public void draw(Canvas canvas) {

    }

    /**
     * within a distance equals the radius attribute of the attacking unit, an enemy unit is chosen with the keyboard to
     * be attacked and receives a certain damage depending on the attacking unit's damage and the number of
     * defensiveStars of the cell where the attacked unit is
     *
     * @param dt       same as for the super class
     * @param player   same as for the super class
     * @param keyboard same as for the super class
     */
    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        ArrayList<Integer> IndexOfAttackableEnemies = unit.getIndexOfAttackableEnemies();
        int indexOfUnitToAttack = 0;
        if (keyboard.get(Keyboard.LEFT).isReleased()) {
            indexOfUnitToAttack = (indexOfUnitToAttack - 1) / IndexOfAttackableEnemies.size();
        } else if (keyboard.get(Keyboard.RIGHT).isReleased()) {
            indexOfUnitToAttack = (indexOfUnitToAttack + 1) / IndexOfAttackableEnemies.size();
        } else if (keyboard.get(Keyboard.RIGHT).isReleased()) {
            unit.attack(indexOfUnitToAttack);
        }
    }
}
