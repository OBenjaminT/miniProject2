package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.math.DiscreteCoordinates;
import ch.epfl.cs107.play.window.Window;

public class ICWarsBehavior extends AreaBehavior{
    public enum ICWarsCellType{
        //https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        NONE(0,0), // Should never be used except
        // in the toType method
        ROAD(-16777216, 0), // the second value is the number
        // of defense stars
        PLAIN(-14112955, 1),
        WOOD(-65536, 3),
        RIVER(-16776961, 0),
        MOUNTAIN(-256, 4),
        CITY(-1,2);

        final int type;
        final int numberOfStars;

        ICWarsCellType(int type, int numberOfStars){
            this.type = type;
            this.numberOfStars = numberOfStars;
        }

        public static ICWarsBehavior.ICWarsCellType toType(int type){
            for(ICWarsBehavior.ICWarsCellType ict : ICWarsBehavior.ICWarsCellType.values()){
                if(ict.type == type)
                    return ict;
            }
            // When you add a new color, you can print the int value here before assign it to a type
            System.out.println(type);
            return NONE;
        }
    }

    /**
     * Default Tuto2Behavior Constructor
     * @param window (Window), not null
     * @param name (String): Name of the Behavior, not null
     */
    public ICWarsBehavior(Window window, String name){
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for(int y = 0; y < height; y++) {
            for (int x = 0; x < width ; x++) {
                ICWarsBehavior.ICWarsCellType type = ICWarsBehavior.ICWarsCellType.toType(getRGB(height-1-y, x));
                setCell(x,y, new ICWarsBehavior.ICWarsCell(x,y,type));
            }
        }
    }

    /**
     * Cell adapted to the Tuto2 game
     */
    public class ICWarsCell extends AreaBehavior.Cell {
        /// Type of the cell following the enum
        private final ICWarsBehavior.ICWarsCellType type;

        /**
         * Default Tuto2Cell Constructor
         * @param x (int): x coordinate of the cell
         * @param y (int): y coordinate of the cell
         * @param type (EnigmeCellType), not null
         */
        public  ICWarsCell(int x, int y, ICWarsBehavior.ICWarsCellType type){
            super(x, y);
            this.type = type;
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            //if the entity takes Cell Space
            if (entity.takeCellSpace()) {
                for (Interactable otherEntity : entities) {//if another entity on the cell also takes the space
                    if (otherEntity.takeCellSpace()) {
                        return false; //entity can't enter
                    }
                }
            }
            return true; //else it can enter
        }


        @Override
        public boolean isCellInteractable() {
            return true;
        }

        @Override
        public boolean isViewInteractable() {
            return false;
        }

        @Override
        public void acceptInteraction(AreaInteractionVisitor v) {
        }

    }
}
