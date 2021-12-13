package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

/**
 * TODO
 */
public class Attack extends Action {

    int indexOfUnitToAttack = -1;
    private ImageGraphics cursor = new ImageGraphics(
        ResourcePath.getSprite(" icwars / UIpackSheet "),
        1f,
        1f,
        new RegionOfInterest(4 * 18, 26 * 18, 16, 16)
    );

    /**
     * TODO
     *
     * @param unit
     * @param area
     */
    public Attack(Units unit, Area area) {
        super(unit, area, "(A)ttack", Keyboard.A);
    }

    /**
     * TODO
     *
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (indexOfUnitToAttack != -1) {
            unit.centerCameraOnTargetedEnnemy(indexOfUnitToAttack);
            cursor.setAnchor(canvas.getPosition().add(1, 0));
            cursor.draw(canvas);
        }
    }

    /**
     * TODO
     * <p>
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
        var IndexOfAttackableEnemies = unit.getIndexOfAttackableEnemies();
        if (keyboard.get(Keyboard.LEFT).isReleased()) {
            indexOfUnitToAttack = indexOfUnitToAttack == 0
                ? IndexOfAttackableEnemies.size() - 1
                : indexOfUnitToAttack - 1;
        } else if (keyboard.get(Keyboard.RIGHT).isReleased()) {
            indexOfUnitToAttack = (indexOfUnitToAttack + 1) % IndexOfAttackableEnemies.size();
        } else if (keyboard.get(Keyboard.ENTER).isReleased()) {
            unit.attack(indexOfUnitToAttack);
            indexOfUnitToAttack = -1; //so that the draw method knows that no enemies are selected
        }
    }
}
