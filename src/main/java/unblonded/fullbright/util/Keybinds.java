package unblonded.fullbright.util;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class Keybinds {
    public static final KeyBinding openGui = new KeyBinding("key.fullbright.open_gui", GLFW.GLFW_KEY_G, KeyBinding.Category.MISC);
    public static final KeyBinding test = new KeyBinding("key.fullbright.test", GLFW.GLFW_KEY_INSERT, KeyBinding.Category.MISC);

    public static void onInitializeClient() {
        KeyBindingHelper.registerKeyBinding(openGui);
    }

    public static int getKeycode(KeyBinding key) {
        return key.getDefaultKey().getCode();
    }
}
