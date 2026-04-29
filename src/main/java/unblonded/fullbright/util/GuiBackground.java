package unblonded.fullbright.util;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class GuiBackground extends Screen {
    public GuiBackground() { super(Text.of("imgui"));}
    @Override public boolean shouldPause() { return false; }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (input.key() == Keybinds.getKeycode(Keybinds.openGui) || input.key() == GLFW.GLFW_KEY_ESCAPE) {
            MinecraftClient.getInstance().setScreen(null);
            return true;
        }
        return false;
    }
}
