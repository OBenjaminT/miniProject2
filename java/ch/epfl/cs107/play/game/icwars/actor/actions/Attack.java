package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.AIPlayer;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Unit;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

/**
 * TODO
 */
public class Attack extends Action {

    /**
     * TODO
     */
    private final ImageGraphics cursor = new ImageGraphics(
            ResourcePath . getSprite ("icwars/UIpackSheet"),
        1f,
        1f,
        new RegionOfInterest(4 * 18, 26 * 18, 16, 16)
    );

    /**
     * TODO
     */
    int indexOfUnitToAttack;
    /**
     * when the action has been realised, for the next action I want to start poiting at the ennemy unit with index 0
     */
    boolean IndexOfUnitToAttackCanBeSetToZero;

    /**
     * TODO
     *
     * @param unit
     * @param area
     */
    public Attack(Unit unit, Area area) {

        super(unit, area, "(A)ttack", Keyboard.A);
        this.indexOfUnitToAttack=0;
        IndexOfUnitToAttackCanBeSetToZero=true;
    }

    /**
     * TODO
     *
     * @param canvas target, not null
     */
    @Override
    public void draw(Canvas canvas) {
        if (!unit.getIndexOfAttackableEnemies().isEmpty()) {
            unit.centerCameraOnTargetedEnemy(unit.getIndexOfAttackableEnemies().get(indexOfUnitToAttack));
            cursor.setAnchor(canvas.getPosition().add(1, 0));
            cursor.draw(canvas);
        }
    }

    /**
     * @param bol sets IndexOfUnitToAttackCanBeSetToZero to bol
     */
    public void IndexOfUnitToAttackCanBeSetToZero(boolean bol){
        IndexOfUnitToAttackCanBeSetToZero = bol;
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
        if (IndexOfAttackableEnemies.isEmpty() || keyboard.get(Keyboard.TAB).isReleased()) {
            player.canSelectActionAgain();
        } else {
            if(IndexOfUnitToAttackCanBeSetToZero) indexOfUnitToAttack=0;
            if (keyboard.get(Keyboard.LEFT).isReleased()) {
                indexOfUnitToAttack = (indexOfUnitToAttack == 0)
                    ? IndexOfAttackableEnemies.size() - 1
                    : indexOfUnitToAttack - 1;
                System.out.println("left");
            } else if (keyboard.get(Keyboard.RIGHT).isReleased()) {
                indexOfUnitToAttack = (indexOfUnitToAttack + 1) % IndexOfAttackableEnemies.size();
                System.out.println("right");
            } else if (keyboard.get(Keyboard.ENTER).isReleased()) {
                unit.attack(IndexOfAttackableEnemies.get(indexOfUnitToAttack));
               /* indexOfUnitToAttack = -1; //so that the draw method knows that no enemies are selected*/
                player.setPlayerCurrentState(ICWarsPlayer.States.NORMAL);
                System.out.println("ENTER");
            }
        }
    }

    public void doAutoAction(float dt, AIPlayer player, Keyboard keyboard){
        //if ennemies are in the range for an attack, attack the one with lowest health
        if(unit.getIndexOfAttackableEnemies()!=null){
            //repeat the same scheme as with attack: the area will perform the attack on the unit with the lowest health
            //TODO create a method in unit called attackLowestHealth that will call attackLowestHealth of area that will perform the attack
        }

    }
}
