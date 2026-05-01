package unblonded.fullbright;

import imgui.ImGui;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import unblonded.fullbright.render.RenderCallback;
import unblonded.fullbright.util.ConfigManager;
import unblonded.fullbright.util.Keybinds;

import java.io.File;

public class Fullbright implements ClientModInitializer {
    public static MinecraftClient client;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        Keybinds.onInitializeClient();

        WorldRenderEvents.END_MAIN.register(ctx -> {
            if (client.world != null) {
                if (!Config.drawMode.get()) RenderCallback.renderBlockOutline();
                else RenderCallback.renderGlow();
                RenderCallback.renderTracers();
            }
        });

        ConfigManager.loadConfig();
    }

    public static File workDir() {
        return new File(client.runDirectory, "unblonded");
    }
}