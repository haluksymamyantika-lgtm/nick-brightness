package pl.nickbrightness.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pl.nickbrightness.NickBrightnessConfig;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
	// Światło nicku (jedyny argument int w renderLabelIfPresent).
	@ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true)
	private int nickbrightness$modifyLight(int light) {
		return NickBrightnessConfig.apply(light);
	}

	// Tekst nicku -> rozjaśnione kolory.
	@ModifyArg(method = "renderLabelIfPresent",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"),
			index = 0)
	private Text nickbrightness$modifyText(Text text) {
		return NickBrightnessConfig.boostText(text);
	}

	// Tło nicku -> przezroczystość.
	@ModifyArg(method = "renderLabelIfPresent",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"),
			index = 8)
	private int nickbrightness$modifyBackground(int background) {
		return NickBrightnessConfig.applyBackground(background);
	}
}
