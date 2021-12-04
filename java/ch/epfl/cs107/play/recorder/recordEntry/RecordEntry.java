package ch.epfl.cs107.play.recorder.recordEntry;

import ch.epfl.cs107.play.window.Window;

import java.awt.*;
import java.io.Serial;

public abstract class RecordEntry implements java.io.Serializable {
    @Serial
    private static final long serialVersionUID = 1;
    private final long time;

    public RecordEntry(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public abstract void replay(Robot robot, Window window);
}
