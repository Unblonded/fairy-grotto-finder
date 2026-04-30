package unblonded.fullbright;

import imgui.type.ImBoolean;
import net.minecraft.util.math.BlockPos;
import unblonded.fullbright.util.Color;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Config {
    public static boolean showMenu = true;
    public static boolean showAll = true;
    public static ImBoolean fontSizeOverride = new ImBoolean(false);
    public static float[] fontSize = {18f};

    public static final List<BlockPos> scanLog = new CopyOnWriteArrayList<>();

    public static Color scanColor = new Color(1f, 0.03f, 0.85f, 1f);
    public static int[] scanRadius = {20};
    public static ImBoolean showTracers = new ImBoolean(true);
    public static ImBoolean showOutlines = new ImBoolean(true);
    public static ImBoolean drawMode = new ImBoolean(true);
}
