package unblonded.fullbright;

import imgui.type.ImBoolean;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Config {
    public static boolean showMenu = true;
    public static boolean showAll = true;
    public static ImBoolean fontSizeOverride = new ImBoolean(false);
    public static float[] fontSize = {18f};

    public static final List<BlockPos> scanLog = new CopyOnWriteArrayList<>();
}
