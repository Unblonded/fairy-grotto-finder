package unblonded.fullbright.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unblonded.fullbright.Fullbright;
import unblonded.fullbright.BlockScanner;
import unblonded.fullbright.render.RenderCallback;
import unblonded.fullbright.util.Color;
import unblonded.fullbright.util.GuiBackground;
import unblonded.fullbright.util.Keybinds;

@Mixin(MinecraftClient.class)
public class TickMixin {

    @Unique int tick = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = Fullbright.client;
        if (tick > Integer.MAX_VALUE - 0xFFF) tick = 0; else tick++;

        if (Keybinds.openGui.wasPressed())
            client.setScreen(new GuiBackground());
    }
}