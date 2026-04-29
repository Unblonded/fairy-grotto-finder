package unblonded.fullbright.imgui;

import imgui.*;
import imgui.flag.ImGuiWindowFlags;
import net.minecraft.util.math.BlockPos;
import unblonded.fullbright.BlockScanner;
import unblonded.fullbright.Config;
import unblonded.fullbright.render.RenderCallback;
import unblonded.fullbright.util.Color;

public class Menu {
    public static void render() {
        if (!Config.showAll) return;
        Alert.renderAll();
        if (Config.showMenu) {
            ImGui.begin("Grotto Finder", ImGuiWindowFlags.AlwaysAutoResize | ImGuiWindowFlags.NoCollapse);
            if (ImGui.button("Find Grotto")) {
                RenderCallback.clearQueue();
                RenderCallback.clearTracers();
                BlockScanner.scan("minecraft:magenta_stained_glass_pane", new Color("#ff08da"), 20).execute().await()
                        .forEach(pc -> {
                            RenderCallback.addToQueue(pc);
                            RenderCallback.addTracer(pc);
                        });
            }
            ImGui.end();

            if (!Config.scanLog.isEmpty()) {
                ImGui.setNextWindowSize(300, 500, imgui.flag.ImGuiCond.Once);
                ImGui.begin("  " + icons.MAGNIFYING_GLASS + "  Scan Log",
                        imgui.flag.ImGuiWindowFlags.NoScrollbar | imgui.flag.ImGuiWindowFlags.NoScrollWithMouse);

                ImGui.textDisabled(String.format("%d blocks found", Config.scanLog.size()));
                ImGui.separator();
                ImGui.spacing();

                float bottomReserved = ImGui.getFrameHeightWithSpacing() + ImGui.getStyle().getItemSpacingY() + 4;
                ImGui.beginChild("##scanlog_scroll", 0, -bottomReserved, false);

                for (int i = 0; i < Config.scanLog.size(); i++) {
                    BlockPos pos = Config.scanLog.get(i);

                    if (i % 2 == 0) {
                        ImVec2 cursorPos = ImGui.getCursorScreenPos();
                        ImGui.getWindowDrawList().addRectFilled(
                                cursorPos.x, cursorPos.y,
                                cursorPos.x + ImGui.getContentRegionAvailX(),
                                cursorPos.y + ImGui.getTextLineHeightWithSpacing(),
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

                if (ImGui.button(icons.DOOR_CLOSED + "  Close", -1, 0))
                    Config.scanLog.clear();

                ImGui.end();
            }
        }
    }
}