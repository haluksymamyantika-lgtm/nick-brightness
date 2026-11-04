package pl.nickbrightness;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.function.IntConsumer;

public class NickBrightnessScreen extends Screen {
	private ConfigSlider lightSlider;
	private ConfigSlider colorSlider;
	private ConfigSlider bgSlider;

	public NickBrightnessScreen() {
		super(Text.literal("Jasność nicków"));
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int top = this.height / 2 - 70;

		lightSlider = new ConfigSlider(cx - 100, top, 200, 20, "Światło nicku",
				NickBrightnessConfig.MIN, NickBrightnessConfig.MAX, NickBrightnessConfig.brightness, NickBrightnessConfig::set);
		colorSlider = new ConfigSlider(cx - 100, top + 24, 200, 20, "Jasność kolorów",
				NickBrightnessConfig.COLOR_MIN, NickBrightnessConfig.COLOR_MAX, NickBrightnessConfig.colorBoost, NickBrightnessConfig::setColor);
		bgSlider = new ConfigSlider(cx - 100, top + 48, 200, 20, "Tło nicku",
				NickBrightnessConfig.BG_MIN, NickBrightnessConfig.BG_MAX, NickBrightnessConfig.background, NickBrightnessConfig::setBackground);

		this.addDrawableChild(lightSlider);
		this.addDrawableChild(colorSlider);
		this.addDrawableChild(bgSlider);

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Maksymalna jasność nicka"), b -> {
			lightSlider.setPercent(NickBrightnessConfig.MAX);
			colorSlider.setPercent(NickBrightnessConfig.COLOR_MAX);
		}).dimensions(cx - 100, top + 78, 200, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Resetuj wszystko"), b -> {
			lightSlider.setPercent(100);
			colorSlider.setPercent(100);
			bgSlider.setPercent(100);
		}).dimensions(cx - 100, top + 102, 200, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Gotowe"), b -> this.close())
				.dimensions(cx - 100, top + 126, 200, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		int top = this.height / 2 - 70;
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, top - 26, 0xFFFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer,
				Text.literal("Światło: 200% = pełne | Kolory: do 300% | Tło: 0% = brak"),
				this.width / 2, top - 14, 0xFFAAAAAA);
	}

	private static class ConfigSlider extends SliderWidget {
		private final String label;
		private final int min;
		private final int max;
		private final IntConsumer setter;

		ConfigSlider(int x, int y, int w, int h, String label, int min, int max, int current, IntConsumer setter) {
			super(x, y, w, h, Text.empty(), (current - min) / (double) (max - min));
			this.label = label;
			this.min = min;
			this.max = max;
			this.setter = setter;
			updateMessage();
		}

		private int percent() {
			return min + (int) Math.round(this.value * (max - min));
		}

		void setPercent(int p) {
			this.value = (p - min) / (double) (max - min);
			applyValue();
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			if (label == null) return;
			setMessage(Text.literal(label + ": " + percent() + "%"));
		}

		@Override
		protected void applyValue() {
			if (setter == null) return;
			setter.accept(percent());
		}
	}
}
