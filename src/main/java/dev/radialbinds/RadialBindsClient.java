package dev.radialbinds;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class RadialBindsClient implements ClientModInitializer {
    private static KeyBinding openKey;
    private static boolean wasDown;
    @Override public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.radialbinds.open", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_MIDDLE, KeyBinding.Category.MISC));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            boolean down = GLFW.glfwGetMouseButton(client.getWindow().getHandle(), GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;
            if (down && !wasDown && client.currentScreen == null) {
                Entity target = client.targetedEntity;
                client.setScreen(new RadialScreen(target));
            }
            wasDown = down;
        });
    }
    static void runCommand(String command, boolean playerMode, Entity target) {
        MinecraftClient c = MinecraftClient.getInstance();
        if (c.player == null || command == null || command.isBlank()) return;
        if (playerMode) {
            if (target == null) return;
            command = command.trim() + " " + target.getName().getString();
        } else command = command.trim();
        if (command.startsWith("/")) command = command.substring(1);
        c.player.networkHandler.sendChatCommand(command);
    }
}
