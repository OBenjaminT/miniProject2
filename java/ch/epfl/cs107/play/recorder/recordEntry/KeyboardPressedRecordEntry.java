package ch.epfl.cs107.play.recorder.recordEntry;

import ch.epfl.cs107.play.window.Window;

import java.awt.*;

public class KeyboardPressedRecordEntry extends RecordEntry {

    private static final long serialVersionUID = 1;
    private final int keycode;

    public KeyboardPressedRecordEntry(long time, int keycode) {
        super(time);
        this.keycode = keycode;
    }

    @Override
    public void replay(Robot robot, Window window) {
        robot.keyPress(keycode);
    }
}
