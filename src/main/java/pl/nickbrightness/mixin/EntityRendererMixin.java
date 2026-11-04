package pl.nickbrightness.mixin;

import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import pl.nickbrightness.NickBrightnessConfig;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
	// Jedyny argument typu int w renderLabelIfPresent to "light" - zmieniamy go, więc tekst nicku jest jaśniejszy/ciemniejszy.
	@ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true)
	private int nickbrightness$modifyLight(int light) {
		return NickBrightnessConfig.apply(light);
	}
}
