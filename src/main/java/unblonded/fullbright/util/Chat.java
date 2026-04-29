package unblonded.fullbright.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Chat {
    public static String prefix = "§6§l>§Delusion§6§l<§7:§r";

    public static void sendMessage(String message) {
        if (message == null || message.isEmpty()) return;
        MinecraftClient.getInstance().player.sendMessage(Text.literal(message), false);
    }
}
