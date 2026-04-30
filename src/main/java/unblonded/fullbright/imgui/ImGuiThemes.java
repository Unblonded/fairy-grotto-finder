package unblonded.fullbright.imgui;

import imgui.*;
import imgui.flag.ImGuiCol;

public class ImGuiThemes{

    public static void cyberpunk() {
        ImGuiStyle style = ImGui.getStyle();

        ImGui.styleColorsDark();

        style.setWindowPadding(12, 12); 
        style.setFramePadding(8, 6);    
        style.setItemSpacing(8, 6);     
        style.setItemInnerSpacing(6, 6);
        style.setTouchExtraPadding(2, 2);            
        style.setIndentSpacing(20.0f);  
        style.setScrollbarSize(14.0f);  
        style.setGrabMinSize(12.0f);    
        style.setTabMinWidthForCloseButton(0.0f);
        
        style.setWindowRounding(6.0f);  
        style.setChildRounding(5.0f);   
        style.setFrameRounding(5.0f);   
        style.setPopupRounding(6.0f);   
        style.setScrollbarRounding(10.0f);           
        style.setGrabRounding(4.0f);    
        style.setTabRounding(5.0f);     
        style.setWindowTitleAlign(0.5f, 0.5f);       

        style.setWindowBorderSize(1.0f);
        style.setChildBorderSize(1.0f); 
        style.setPopupBorderSize(1.0f); 
        style.setFrameBorderSize(1.0f); 
        style.setTabBorderSize(1.0f);
        
        final ImVec4 voidBlack = new ImVec4(0.012f, 0.012f, 0.06f, 0.97f);          
        final ImVec4 shadowPurple = new ImVec4(0.055f, 0.025f, 0.14f, 0.92f);       
        final ImVec4 deepViolet = new ImVec4(0.09f, 0.05f, 0.19f, 0.90f);
        final ImVec4 plasmaPink = new ImVec4(1.00f, 0.07f, 0.70f, 0.95f);           
        final ImVec4 electricCyan = new ImVec4(0.05f, 0.95f, 1.00f, 0.95f);         
        final ImVec4 voidPurple = new ImVec4(0.80f, 0.30f, 1.00f, 0.95f);           
        final ImVec4 matrixGreen = new ImVec4(0.28f, 1.00f, 0.38f, 0.95f);
        final ImVec4 solidGreen = new ImVec4(0.28f, 1.00f, 0.38f, 1.00f);
        final ImVec4 hologramWhite = new ImVec4(0.99f, 0.99f, 1.00f, 1.00f);
        final ImVec4 whisperGray = new ImVec4(0.58f, 0.60f, 0.68f, 0.75f);         

        style.setColor(ImGuiCol.Text, hologramWhite.x, hologramWhite.y, hologramWhite.z, hologramWhite.w);
        style.setColor(ImGuiCol.TextDisabled, whisperGray.x, whisperGray.y, whisperGray.z, whisperGray.w);
        style.setColor(ImGuiCol.WindowBg, voidBlack.x, voidBlack.y, voidBlack.z, voidBlack.w);
        style.setColor(ImGuiCol.ChildBg, shadowPurple.x, shadowPurple.y, shadowPurple.z, 0.65f);
        style.setColor(ImGuiCol.PopupBg, deepViolet.x, deepViolet.y, deepViolet.z, 0.97f);

        style.setColor(ImGuiCol.Border, voidPurple.x, voidPurple.y, voidPurple.z, 0.60f);
        style.setColor(ImGuiCol.BorderShadow, plasmaPink.x, plasmaPink.y, plasmaPink.z, 0.15f);
        
        style.setColor(ImGuiCol.FrameBg, deepViolet.x, deepViolet.y, deepViolet.z, 0.70f);
        style.setColor(ImGuiCol.FrameBgHovered, voidPurple.x * 0.7f, voidPurple.y * 0.7f, voidPurple.z * 0.7f, 0.50f);
        style.setColor(ImGuiCol.FrameBgActive, voidPurple.x * 0.9f, voidPurple.y * 0.9f, voidPurple.z * 0.9f, 0.70f);

        style.setColor(ImGuiCol.TitleBg, shadowPurple.x, shadowPurple.y, shadowPurple.z, 0.90f);
        style.setColor(ImGuiCol.TitleBgActive, voidPurple.x * 0.5f, voidPurple.y * 0.5f, voidPurple.z * 0.5f, 0.95f);
        style.setColor(ImGuiCol.TitleBgCollapsed, shadowPurple.x, shadowPurple.y, shadowPurple.z, 0.50f);

        style.setColor(ImGuiCol.Button, plasmaPink.x * 0.8f, plasmaPink.y * 0.8f, plasmaPink.z * 0.8f, 0.75f);
        style.setColor(ImGuiCol.ButtonHovered, plasmaPink.x * 1.1f, plasmaPink.y * 1.1f, plasmaPink.z * 1.1f, 0.90f);
        style.setColor(ImGuiCol.ButtonActive, Math.min(1.0f, plasmaPink.x + 0.20f), Math.min(1.0f, plasmaPink.y + 0.20f), Math.min(1.0f, plasmaPink.z + 0.20f), 1.00f);
        
        style.setColor(ImGuiCol.Header, electricCyan.x * 0.7f, electricCyan.y * 0.7f, electricCyan.z * 0.7f, 0.70f);
        style.setColor(ImGuiCol.HeaderHovered, electricCyan.x * 1.1f, electricCyan.y * 1.1f, electricCyan.z * 1.1f, 0.85f);
        style.setColor(ImGuiCol.HeaderActive, Math.min(1.0f, electricCyan.x + 0.15f), Math.min(1.0f, electricCyan.y + 0.15f), Math.min(1.0f, electricCyan.z + 0.15f), 0.95f);
        
        style.setColor(ImGuiCol.Tab, deepViolet.x * 1.2f, deepViolet.y * 1.2f, deepViolet.z * 1.2f, 0.80f);
        style.setColor(ImGuiCol.TabHovered, voidPurple.x * 1.1f, voidPurple.y * 1.1f, voidPurple.z * 1.1f, 0.90f);
        style.setColor(ImGuiCol.TabActive, voidPurple.x, voidPurple.y, voidPurple.z, 0.95f);
        style.setColor(ImGuiCol.TabUnfocused, shadowPurple.x * 0.8f, shadowPurple.y * 0.8f, shadowPurple.z * 0.8f, 0.70f);
        style.setColor(ImGuiCol.TabUnfocusedActive, deepViolet.x * 1.2f, deepViolet.y * 1.2f, deepViolet.z * 1.2f, 0.80f);

        style.setColor(ImGuiCol.Separator, electricCyan.x, electricCyan.y, electricCyan.z, 0.65f);
        style.setColor(ImGuiCol.SeparatorHovered, plasmaPink.x, plasmaPink.y, plasmaPink.z, 0.85f);
        style.setColor(ImGuiCol.SeparatorActive, matrixGreen.x, matrixGreen.y, matrixGreen.z, 0.95f);

        style.setColor(ImGuiCol.ScrollbarBg, shadowPurple.x, shadowPurple.y, shadowPurple.z, 0.45f);
        style.setColor(ImGuiCol.ScrollbarGrab, voidPurple.x, voidPurple.y, voidPurple.z, 0.75f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, Math.min(1.0f, voidPurple.x + 0.20f), Math.min(1.0f, voidPurple.y + 0.20f), Math.min(1.0f, voidPurple.z + 0.20f), 0.90f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, plasmaPink.x, plasmaPink.y, plasmaPink.z, 1.00f);

        style.setColor(ImGuiCol.CheckMark, solidGreen.x, solidGreen.y, solidGreen.z, solidGreen.w);
        style.setColor(ImGuiCol.SliderGrab, plasmaPink.x, plasmaPink.y, plasmaPink.z, 0.90f);
        style.setColor(ImGuiCol.SliderGrabActive, electricCyan.x, electricCyan.y, electricCyan.z, 1.00f);

        style.setColor(ImGuiCol.TextSelectedBg, electricCyan.x, electricCyan.y, electricCyan.z, 0.40f);

        style.setColor(ImGuiCol.DragDropTarget, matrixGreen.x, matrixGreen.y, matrixGreen.z, 0.85f);

        style.setColor(ImGuiCol.NavHighlight, plasmaPink.x, plasmaPink.y, plasmaPink.z, 0.90f);
        style.setColor(ImGuiCol.NavWindowingHighlight, electricCyan.x, electricCyan.y, electricCyan.z, 0.80f);
        style.setColor(ImGuiCol.NavWindowingDimBg, 0.15f, 0.15f, 0.20f, 0.65f);

        style.setColor(ImGuiCol.ModalWindowDimBg, 0.06f, 0.06f, 0.12f, 0.75f);

        style.setColor(ImGuiCol.TableHeaderBg, deepViolet.x * 1.3f, deepViolet.y * 1.3f, deepViolet.z * 1.3f, 0.85f);
        style.setColor(ImGuiCol.TableBorderStrong, voidPurple.x, voidPurple.y, voidPurple.z, 0.75f);
        style.setColor(ImGuiCol.TableBorderLight, voidPurple.x, voidPurple.y, voidPurple.z, 0.40f);
        style.setColor(ImGuiCol.TableRowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TableRowBgAlt, shadowPurple.x, shadowPurple.y, shadowPurple.z, 0.30f);

        style.setColor(ImGuiCol.ResizeGrip, voidPurple.x, voidPurple.y, voidPurple.z, 0.50f);
        style.setColor(ImGuiCol.ResizeGripHovered, plasmaPink.x, plasmaPink.y, plasmaPink.z, 0.75f);
        style.setColor(ImGuiCol.ResizeGripActive, electricCyan.x, electricCyan.y, electricCyan.z, 0.95f);

        style.setColor(ImGuiCol.PlotLines, matrixGreen.x, matrixGreen.y, matrixGreen.z, 0.90f);
        style.setColor(ImGuiCol.PlotLinesHovered, plasmaPink.x, plasmaPink.y, plasmaPink.z, 1.00f);
        style.setColor(ImGuiCol.PlotHistogram, electricCyan.x, electricCyan.y, electricCyan.z, 0.90f);
        style.setColor(ImGuiCol.PlotHistogramHovered, voidPurple.x, voidPurple.y, voidPurple.z, 1.00f);

        style.setAlpha(0.97f);
    }

    static void simple() {
        ImGuiStyle style = ImGui.getStyle();

        ImGui.styleColorsDark();

        style.setWindowPadding(12, 12);
        style.setFramePadding(8, 6);
        style.setItemSpacing(8, 6);
        style.setItemInnerSpacing(6, 6);
        style.setTouchExtraPadding(2, 2);
        style.setIndentSpacing(20.0f);
        style.setScrollbarSize(14.0f);
        style.setGrabMinSize(12.0f);
        style.setTabMinWidthForCloseButton(0.0f);

        style.setWindowRounding(6.0f);
        style.setChildRounding(5.0f);
        style.setFrameRounding(5.0f);
        style.setPopupRounding(6.0f);
        style.setScrollbarRounding(10.0f);
        style.setGrabRounding(4.0f);
        style.setTabRounding(5.0f);
        style.setWindowTitleAlign(0.5f, 0.5f);

        style.setWindowBorderSize(1.0f);
        style.setChildBorderSize(1.0f);
        style.setPopupBorderSize(1.0f);
        style.setFrameBorderSize(1.0f);
        style.setTabBorderSize(1.0f);

        style.setAlpha(0.97f);
    }
}