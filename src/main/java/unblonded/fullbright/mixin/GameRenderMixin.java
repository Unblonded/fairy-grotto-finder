package unblonded.fullbright.mixin;

import imgui.ImGui;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import unblonded.fullbright.Config;
import unblonded.fullbright.Fullbright;
import unblonded.fullbright.imgui.ImGuiImpl;
import unblonded.fullbright.imgui.Menu;
import unblonded.fullbright.util.GuiBackground;

@Mixin(GameRenderer.class)
public class GameRenderMixin {
    @Inject(method = "render", at = @At("RETURN"))
    private void render(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        ImGuiImpl.beginImGuiRendering();
        Menu.render();
        ImGuiImpl.endImGuiRendering();

        Config.showMenu = Fullbright.client.currentScreen instanceof GuiBackground;
        Config.showAll = Fullbright.client.world != null;

        if (Config.fontSizeOverride.get()) ImGui.getIO().setFontGlobalScale(Config.fontSize[0]);
        else ImGui.getIO().setFontGlobalScale(Math.max(1.1f, (ImGui.getIO().getDisplaySize().x / 1920.0f) * 0.85f));
    }
}