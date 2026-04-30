package unblonded.fullbright.imgui;

import imgui.*;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiTabBarFlags;
import imgui.type.ImInt;
import net.minecraft.util.math.BlockPos;
import unblonded.fullbright.BlockScanner;
import unblonded.fullbright.Config;
import unblonded.fullbright.render.RenderCallback;
import unblonded.fullbright.util.Color;

public class Menu {
    private static float[] colorBuffer = Config.scanColor.asFloatArr();
    private static final String[] themes = {"Cyberpunk", "Simple"};
    private static ImInt themeRef = new ImInt(1);
    private static int lastTheme = -1;

    public static void render() {
        if (!Config.showAll) return;
        Alert.renderAll();
        if (!Config.showMenu) return;

        // ── Main window ───────────────────────────────────────────────
        ImGui.begin(icons.MAGNIFYING_GLASS + "  Grotto Finder",
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        // ── Tab bar ───────────────────────────────────────────────────
        if (ImGui.beginTabBar("##tabs", ImGuiTabBarFlags.None)) {

            // ── Scanner tab ──────────────────────────────────────────
            if (ImGui.beginTabItem(icons.MAGNIFYING_GLASS + "  Scanner")) {
                ImGui.spacing();

                // Color picker
                ImGui.text("Highlight Color");
                ImGui.sameLine();
                ImGui.colorEdit4("##color", colorBuffer,
                        imgui.flag.ImGuiColorEditFlags.NoLabel |
                                imgui.flag.ImGuiColorEditFlags.AlphaBar | imgui.flag.ImGuiColorEditFlags.NoInputs);

                Color newColor = new Color(colorBuffer[0], colorBuffer[1], colorBuffer[2], colorBuffer[3]);
                if (!newColor.equals(RenderCallback.activeColor)) RenderCallback.activeColor = newColor;

                // Radius slider
                ImGui.text("Scan Radius   ");
                ImGui.sameLine();
                ImGui.sliderInt("##radius", Config.scanRadius, 1, 30);

                // Toggles
                ImGui.checkbox("Show Tracers", Config.showTracers);
                ImGui.sameLine();
                ImGui.checkbox(("Show " + (Config.drawMode.get() ? "Glow" : "Outlines")), Config.showOutlines);
                ImGui.sameLine();
                if (ImGui.button(Config.drawMode.get() ? "Glow" : "Box")) Config.drawMode.set(!Config.drawMode.get());

                ImGui.spacing();
                ImGui.separator();
                ImGui.spacing();

                // Scan / Clear buttons
                float btnW = (ImGui.getContentRegionAvailX() - ImGui.getStyle().getItemSpacingX()) / 2f;

                if (ImGui.button(icons.MAGNIFYING_GLASS + "  Scan Grotto", btnW, 0)) {
                    RenderCallback.clearQueue();
                    RenderCallback.clearTracers();
                    BlockScanner.scan("minecraft:magenta_stained_glass_pane", Config.scanColor, Config.scanRadius[0])
                            .execute().await()
                            .forEach(pc -> {
                                RenderCallback.addToQueue(pc);
                                RenderCallback.addTracer(pc);
                            });
                }

                ImGui.sameLine();

                if (ImGui.button(icons.TRASH + "  Clear", btnW, 0)) {
                    RenderCallback.clearQueue();
                    RenderCallback.clearTracers();
                    Config.scanLog.clear();
                }

                ImGui.spacing();
                ImGui.endTabItem();
            }

            // ── Results tab ──────────────────────────────────────────
            if (ImGui.beginTabItem(icons.LIST + "  Results (" + Config.scanLog.size() + ")")) {
                ImGui.spacing();

                if (Config.scanLog.isEmpty()) {
                    ImGui.textDisabled("No results yet — run a scan first.");
                } else {
                    ImGui.textDisabled(Config.scanLog.size() + " blocks found");
                    ImGui.separator();
                    ImGui.spacing();

                    float bottomReserved = ImGui.getFrameHeightWithSpacing() + ImGui.getStyle().getItemSpacingY() + 4;
                    ImGui.beginChild("##scanlog_scroll", 0, ImGui.getContentRegionAvailY() - bottomReserved, false);

                    for (int i = 0; i < Config.scanLog.size(); i++) {
                        BlockPos pos = Config.scanLog.get(i);

                        if (i % 2 == 0) {
                            ImVec2 cp = ImGui.getCursorScreenPos();
                            ImGui.getWindowDrawList().addRectFilled(
                                    cp.x, cp.y,
                                    cp.x + ImGui.getContentRegionAvailX(),
                                    cp.y + ImGui.getTextLineHeightWithSpacing(),
                                    ImGui.colorConvertFloat4ToU32(1f, 1f, 1f, 0.03f)
                            );
                        }

                        ImGui.textColored(0.4f, 0.8f, 1f, 1f, "X");
                        ImGui.sameLine(0, 2);
                        ImGui.text(String.valueOf(pos.getX()));
                        ImGui.sameLine(0, 8);
                        ImGui.textColored(0.4f, 1f, 0.4f, 1f, "Y");
                        ImGui.sameLine(0, 2);
                        ImGui.text(String.valueOf(pos.getY()));
                        ImGui.sameLine(0, 8);
                        ImGui.textColored(1f, 0.6f, 0.4f, 1f, "Z");
                        ImGui.sameLine(0, 2);
                        ImGui.text(String.valueOf(pos.getZ()));
                    }

                    if (ImGui.getScrollY() >= ImGui.getScrollMaxY())
                        ImGui.setScrollHereY(1.0f);

                    ImGui.endChild();

                    ImGui.separator();
                    if (ImGui.button(icons.TRASH + "  Clear Results", -1, 0))
                        Config.scanLog.clear();
                }

                ImGui.spacing();
                ImGui.endTabItem();
            }

            // ── Settings tab (placeholder for future options) ────────
            if (ImGui.beginTabItem(icons.GEAR + "  Settings")) {
                ImGui.text("Themes");
                ImGui.setNextItemWidth(200);
                ImGui.combo("##theme", themeRef, themes, themes.length);
                ImGui.spacing();
                ImGui.text("More settings coming soon...");
                ImGui.endTabItem();
            }

            ImGui.endTabBar();

            if (themeRef.get() != lastTheme) {
                lastTheme = themeRef.get();
                switch (lastTheme) {
                    case 0 -> ImGuiThemes.cyberpunk();
                    case 1 -> ImGuiThemes.simple();
                }
            }
        }

        ImGui.end();
    }
}