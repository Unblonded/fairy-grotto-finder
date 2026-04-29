package unblonded.fullbright;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import unblonded.fullbright.render.RenderCallback;
import unblonded.fullbright.util.Keybinds;

public class Fullbright implements ClientModInitializer {
    public static MinecraftClient client;

    @Override
    public void onInitializeClient() {
        client = MinecraftClient.getInstance();
        Keybinds.onInitializeClient();

        WorldRenderEvents.END_MAIN.register(ctx -> {
            RenderCallback.renderBlockOutline();
            RenderCallback.renderTracers();
        });
    }
}