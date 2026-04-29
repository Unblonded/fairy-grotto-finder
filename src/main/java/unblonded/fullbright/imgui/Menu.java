package unblonded.fullbright.imgui;

import imgui.*;
import imgui.flag.ImGuiWindowFlags;
import imgui.flag.ImGuiTabBarFlags;
import net.minecraft.util.math.BlockPos;
import unblonded.fullbright.BlockScanner;
import unblonded.fullbright.Config;
import unblonded.fullbright.render.RenderCallback;
import unblonded.fullbright.util.Color;

public class Menu {

    // Scan settings — hook these up to UI later
    private static int scanRadius = 20;
    private static Color scanColor = new Color(1f, 0.03f, 0.85f, 1f);
    private static boolean[] showTracers = {true};
    private static boolean[] showOutlines = {true};
    private static boolean[] depthTest = {false};

    public static void render() {
        if (!Config.showAll) return;
        Alert.renderAll();
        if (!Config.showMenu) return;

        // ── Main window ───────────────────────────────────────────────
        ImGui.setNextWindowSize(320, 0, imgui.flag.ImGuiCond.Once);
        ImGui.begin(icons.MAGNIFYING_GLASS + "  Grotto Finder",
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoScrollbar | ImGuiWindowFlags.NoScrollWithMouse);

        // Status badge
        int queueSize = RenderCallback.queueSize();
        if (queueSize > 0) {
            ImGui.sameLine(ImGui.getContentRegionAvailX() - 60);
            ImGui.textColored(0.4f, 1f, 0.4f, 1f, icons.CIRCLE + "  " + queueSize);
        }

        ImGui.spacing();

        // ── Tab bar ───────────────────────────────────────────────────
        if (ImGui.beginTabBar("##tabs", ImGuiTabBarFlags.None)) {

            // ── Scanner tab ──────────────────────────────────────────
            if (ImGui.beginTabItem(icons.MAGNIFYING_GLASS + "  Scanner")) {
                ImGui.spacing();

                // Color picker
                ImGui.text("Highlight Color");
                ImGui.sameLine();
                ImGui.colorEdit4("##color", scanColor.asFloatArr(),
                        imgui.flag.ImGuiColorEditFlags.NoLabel |
                                imgui.flag.ImGuiColorEditFlags.AlphaBar);

                // Radius slider
                ImGui.text("Scan Radius   ");
                ImGui.sameLine();
                int[] radiusArr = {scanRadius};
                ImGui.sliderInt("##radius", radiusArr, 1, 30);
                scanRadius = radiusArr[0];

                // Toggles
                ImGui.checkbox("Show Tracers", showTracers[0]);
                ImGui.sameLine();
                ImGui.checkbox("Show Outlines", showOutlines[0]);
                ImGui.checkbox("No Depth Test", depthTest[0]);

                ImGui.spacing();
                ImGui.separator();
                ImGui.spacing();

                // Scan / Clear buttons
                float btnW = (ImGui.getContentRegionAvailX() - ImGui.getStyle().getItemSpacingX()) / 2f;

                if (ImGui.button(icons.MAGNIFYING_GLASS + "  Scan Grotto", btnW, 0)) {
                    RenderCallback.clearQueue();
                    RenderCallback.clearTracers();
                    BlockScanner.scan("minecraft:magenta_stained_glass_pane", scanColor, scanRadius)
                            .execute().await()
                            .forEach(pc -> {
                                if (showOutlines[0]) RenderCallback.addToQueue(pc);
                                if (showTracers[0])  RenderCallback.addTracer(pc);
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
                    ImGui.beginChild("##scanlog_scroll", 0, 200, false);

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
                ImGui.spacing();
                ImGui.textDisabled("More settings coming soon...");
                ImGui.spacing();
                // TODO: keybind config, theme picker, etc
                ImGui.endTabItem();
            }

            ImGui.endTabBar();
        }

        ImGui.end();
    }
}