package pl.nickbrightness;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class NickBrightnessClient implements ClientModInitializer {
	private static final int STEP = 10;
	private static KeyBinding upKey;
	private static KeyBinding downKey;

	@Override
	public void onInitializeClient() {
		NickBrightnessConfig.load();

		upKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.nickbrightness.up", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_BRACKET, "category.nickbrightness"));
		downKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.nickbrightness.down", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_BRACKET, "category.nickbrightness"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (upKey.wasPressed()) change(client, +STEP);
			while (downKey.wasPressed()) change(client, -STEP);
		});

		// /nickjasnosc          -> pokazuje aktualną wartość
		// /nickjasnosc <0-200>  -> ustawia (100 = normalnie)
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
				dispatcher.register(ClientCommandManager.literal("nickjasnosc")
						.executes(ctx -> {
							ctx.getSource().sendFeedback(Text.literal("Jasność nicków: " + NickBrightnessConfig.brightness + "%"));
							return 1;
						})
						.then(ClientCommandManager.argument("procent",
										IntegerArgumentType.integer(NickBrightnessConfig.MIN, NickBrightnessConfig.MAX))
								.executes(ctx -> {
									NickBrightnessConfig.set(IntegerArgumentType.getInteger(ctx, "procent"));
									ctx.getSource().sendFeedback(Text.literal("Jasność nicków: " + NickBrightnessConfig.brightness + "%"));
									return 1;
								}))));
	}

	private static void change(MinecraftClient client, int delta) {
		NickBrightnessConfig.set(NickBrightnessConfig.brightness + delta);
		if (client.player != null) {
			client.player.sendMessage(Text.literal("Jasność nicków: " + NickBrightnessConfig.brightness + "%"), true);
		}
	}
}
