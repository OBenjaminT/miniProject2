package ch.epfl.cs107.play.game.icwars.area;

import ch.epfl.cs107.play.game.areagame.AreaBehavior;
import ch.epfl.cs107.play.game.areagame.actor.Interactable;
import ch.epfl.cs107.play.game.areagame.handler.AreaInteractionVisitor;
import ch.epfl.cs107.play.window.Window;

import java.util.Arrays;

public class ICWarsBehavior extends AreaBehavior {
    /**
     * Default Tuto2Behavior Constructor
     *
     * @param window (Window), not null
     * @param name   (String): Name of the Behavior, not null
     */
    public ICWarsBehavior(Window window, String name) {
        super(window, name);
        int height = getHeight();
        int width = getWidth();
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                setCell(
                    x,
                    y,
                    new ICWarsCell(x, y, ICWarsCellType.toType(getRGB(height - 1 - y, x)))
                );
    }

    public enum ICWarsCellType {
        // https://stackoverflow.com/questions/25761438/understanding-bufferedimage-getrgb-output-values
        NONE(0x0, 0), // Should never be used except in the toType method
        ROAD(0xff_00_00_00, 0), // the second value is the number of defense stars
        PLAIN(0xff_28_a7_45, 1),
        WOOD(0xff_ff_00_00, 3),
        RIVER(0xff_00_00_ff, 0),
        MOUNTAIN(0xff_ff_ff_00, 4),
        CITY(0xff_ff_ff_ff, 2);

        final int type;
        final int numberOfStars;

        ICWarsCellType(int type, int numberOfStars) {
            this.type = type;
            this.numberOfStars = numberOfStars;
        }

        public static ICWarsBehavior.ICWarsCellType toType(int type) {
            return Arrays.stream(ICWarsCellType.values()) // for each cell type
                .filter(ict -> ict.type == type) // if it's the type we're looking for
                .findFirst() // get the first one, and return it
                .orElse(NONE); // if there isn't one, return `NONE`
        }
    }

    /**
     * Cell adapted to the Tuto2 game
     */
    public static class ICWarsCell extends AreaBehavior.Cell {

        /**
         * Default Tuto2Cell Constructor
         *
         * @param x    (int): x coordinate of the cell
         * @param y    (int): y coordinate of the cell
         * @param type (UnknownCellType), not null
         */
        public ICWarsCell(int x, int y, ICWarsBehavior.ICWarsCellType type) {
            super(x, y);
            /// Type of the cell following the enum
        }

        @Override
        protected boolean canLeave(Interactable entity) {
            return true;
        }

        @Override
        protected boolean canEnter(Interactable entity) {
            // if the entity takes Cell Space else it can enter
            // if another entity on the cell also takes the space entity can't enter
            return !entity.takeCellSpace()
                || entities.stream().noneMatch(Interactable::takeCellSpace);
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
