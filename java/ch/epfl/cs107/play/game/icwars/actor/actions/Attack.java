package ch.epfl.cs107.play.game.icwars.actor.actions;

import ch.epfl.cs107.play.game.actor.ImageGraphics;
import ch.epfl.cs107.play.game.areagame.Area;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.actor.ICWarsPlayer;
import ch.epfl.cs107.play.game.icwars.actor.Units;
import ch.epfl.cs107.play.math.RegionOfInterest;
import ch.epfl.cs107.play.window.Canvas;
import ch.epfl.cs107.play.window.Keyboard;

import java.util.ArrayList;

public class Attack extends Action {

    int indexOfUnitToAttack = -1;
    private ImageGraphics cursor = new ImageGraphics ( ResourcePath. getSprite (" icwars / UIpackSheet "),
            1f, 1f ,
            new RegionOfInterest(4*18 , 26*18 ,16 ,16) );

    public Attack(Units unit, Area area, String name, int key) {
        super(unit, area, name, key);
    }

    @Override
    public void draw(Canvas canvas) {
        if(indexOfUnitToAttack!=-1){
            unit.centerCameraOnTargetedEnnemy(indexOfUnitToAttack);
            cursor . setAnchor ( canvas . getPosition (). add (1 ,0) );
            cursor.draw(canvas);
        }
    }

    /**
     * @param dt same as for the super class
     * @param player same as for the super class
     * @param keyboard same as for the super class
     * within a distance equals the radius attribute of the attacking unit,
     * an ennemy unit is chosen with the keyboard to be attacked and receives a certain
     * dammage depending on the attacking unit's dammage and the number of defensiveStars of
     * the cell where the attacked unit is
     */
    @Override
    public void doAction(float dt, ICWarsPlayer player, Keyboard keyboard) {
        ArrayList<Integer> IndexOfAttackableEnnemies = unit.getIndexOfAttackableEnnemies();
        if(keyboard.get(Keyboard.LEFT).isReleased()){
            indexOfUnitToAttack = (indexOfUnitToAttack-1)/IndexOfAttackableEnnemies.size();
        }
        else if(keyboard.get(Keyboard.RIGHT).isReleased()){
            indexOfUnitToAttack = (indexOfUnitToAttack+1)/IndexOfAttackableEnnemies.size();
        }
        else if(keyboard.get(Keyboard.ENTER).isReleased()){
            unit.attack(indexOfUnitToAttack);
            indexOfUnitToAttack = -1;;//so that the draw method knows that no ennemies are selected
        }
    }
}
