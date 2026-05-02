package unblonded.fullbright;

import imgui.type.ImBoolean;
import imgui.type.ImInt;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Config {
    public static boolean showMenu = true;
    public static boolean showAll = true;
    public static ImBoolean fontSizeOverride = new ImBoolean(false);
    public static float[] fontSize = {18f};

    public static float[] scanColor = {1f, 0.03f, 0.85f, 1f};
    public static int[] scanRadius = {20};
    public static ImBoolean showTracers = new ImBoolean(true);
    public static ImBoolean showOutlines = new ImBoolean(true);
    public static ImBoolean drawMode = new ImBoolean(true);

    public static ImInt themeRef = new ImInt(1);
}
