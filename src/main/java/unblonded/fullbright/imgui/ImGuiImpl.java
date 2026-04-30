package unblonded.fullbright.imgui;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import imgui.*;
import imgui.extension.implot.ImPlot;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GlBackend;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL30C;
import unblonded.fullbright.Fullbright;

import java.io.IOException;
import java.io.InputStream;

public final class ImGuiImpl {

    private final static ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final static ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();

    public static void create(final long handle) {
        ImGui.createContext();
        ImPlot.createContext();

        final ImGuiIO data = ImGui.getIO();
        data.setIniFilename("menu.ini");

        data.getFonts().clear();

        try {loadFont("assets/fullbright/fonts/segoeui.ttf", 18); }
        catch (Exception ignored) { data.getFonts().addFontDefault(); }

        try { loadFontAwesome("assets/fullbright/fonts/fa-solid-900.ttf", 18); }
        catch (Exception ignored) {}

        data.getFonts().build();

        //ImGuiThemes.cyberpunk();
        //ImGuiThemes.simple();

        data.setConfigFlags(ImGuiConfigFlags.DockingEnable);

        imGuiImplGlfw.init(handle, true);
        imGuiImplGl3.init();
    }

    public static void beginImGuiRendering() {
        final Framebuffer framebuffer = MinecraftClient.getInstance().getFramebuffer();
        GlStateManager._glBindFramebuffer(GL30C.GL_FRAMEBUFFER, ((GlTexture) framebuffer.getColorAttachment()).getOrCreateFramebuffer(((GlBackend) RenderSystem.getDevice()).getBufferManager(), null));
        GL11C.glViewport(0, 0, framebuffer.textureWidth, framebuffer.textureHeight);

        imGuiImplGl3.newFrame();
        imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public static void endImGuiRendering() {
        ImGui.render();
        imGuiImplGl3.renderDrawData(ImGui.getDrawData());

        GlStateManager._glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long pointer = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            GLFW.glfwMakeContextCurrent(pointer);
        }
    }

    public static void loadFont(String resourcePath, float fontSize) {
        try (InputStream is = Fullbright.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) return;

            ImGuiIO io = ImGui.getIO();
            io.getFonts().addFontFromMemoryTTF(
                    is.readAllBytes(),
                    fontSize
            );
            io.setFonts(io.getFonts());
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void loadFontAwesome(String fontloc, float fontSize) {
        ImGuiIO io = ImGui.getIO();

        try (InputStream is = Fullbright.class.getClassLoader().getResourceAsStream(fontloc)) {
            if (is == null) return;

            ImFontConfig fontConfig = new ImFontConfig();
            fontConfig.setMergeMode(true);
            fontConfig.setPixelSnapH(true);

            ImFontGlyphRangesBuilder rangeBuilder = new ImFontGlyphRangesBuilder();
            rangeBuilder.addRanges(new short[]{(short) icons.ICON_MIN, (short) icons.ICON_MAX, 0});
            short[] ranges = rangeBuilder.buildRanges();

            io.getFonts().addFontFromMemoryTTF(
                    is.readAllBytes(),
                    fontSize,
                    fontConfig,
                    ranges
            );

            fontConfig.destroy();

        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void dispose() {
        imGuiImplGl3.shutdown();
        imGuiImplGlfw.shutdown();

        ImPlot.destroyContext();
        ImGui.destroyContext();
    }
}