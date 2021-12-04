package ch.epfl.cs107.play;

import ch.epfl.cs107.play.game.Game;
import ch.epfl.cs107.play.game.areagame.io.ResourcePath;
import ch.epfl.cs107.play.game.icwars.ICWars;
import ch.epfl.cs107.play.io.DefaultFileSystem;
import ch.epfl.cs107.play.io.FileSystem;
import ch.epfl.cs107.play.io.ResourceFileSystem;
import ch.epfl.cs107.play.window.Window;
import ch.epfl.cs107.play.window.swing.SwingWindow;

/**
 * Main entry point.
 */
public class Play {

    public static final int WINDOW_HEIGHT = 550;
    public static final int WINDOW_WIDTH = 550;
    /**
     * One second in nano second
     */
    private static final float ONE_SEC = 1E9f;

    /**
     * Main entry point.
     *
     * @param args (Array of String): ignored
     */
    public static void main(String[] args) throws Exception {

        // Define cascading file system
        final FileSystem fileSystem = new ResourceFileSystem(DefaultFileSystem.INSTANCE);

        // Create a demo game and initialize corresponding texts
        //XMLTexts.initialize(fileSystem, "strings/icmon_fr.xml"); // example of dialog strings

        // Use Swing display

        //	Recorder recorder = new Recorder(window);
        //	RecordReplayer replayer = new RecordReplayer(window);

        // I changed Window and Game to implement Autoclosable,
        // so we just do a try-with-resources, and it closes it automatically
        try (Game game = new ICWars(); final Window window = new SwingWindow(game.getTitle(), fileSystem, WINDOW_WIDTH, WINDOW_HEIGHT)) {
            window.registerFonts(ResourcePath.FONTS);

            if (game.begin(window, fileSystem)) {
                //recorder.start();
                //replayer.start("zelda.xml");

                // Use system clock to keep track of time progression
                long currentTime = System.nanoTime();
                long lastTime;
                final float frameDuration = ONE_SEC / game.getFrameRate();

                // Run until the user try to close the window

                do {
                    // Compute time interval
                    lastTime = currentTime;
                    currentTime = System.nanoTime();
                    float deltaTime = (currentTime - lastTime);

                    try {
                        int timeDiff = Math.max(0, (int) (frameDuration - deltaTime));
                        Thread.sleep((int) (timeDiff / 1E6), (int) (timeDiff % 1E6));
                    } catch (InterruptedException e) {
                        System.out.println("Thread sleep interrupted");
                    }

                    currentTime = System.nanoTime();
                    deltaTime = (currentTime - lastTime) / ONE_SEC;

                    // Let the game do its stuff
                    game.update(deltaTime);

                    // Render and update input
                    window.update();
                    //recorder.update();
                    //replayer.update();
                } while (!window.isCloseRequested());

            }
            //recorder.stop("zelda.xml");
        }
    }
}
