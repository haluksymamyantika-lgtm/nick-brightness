package pl.nickbrightness;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class NickBrightnessScreen extends Screen {
	private BrightnessSlider slider;

	public NickBrightnessScreen() {
		super(Text.literal("Jasność nicków"));
	}

	@Override
	protected void init() {
		int cx = this.width / 2;
		int cy = this.height / 2;

		slider = new BrightnessSlider(cx - 100, cy - 10, 200, 20);
		this.addDrawableChild(slider);

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Resetuj (100%)"), b -> slider.setPercent(100))
				.dimensions(cx - 100, cy + 20, 200, 20).build());

		this.addDrawableChild(ButtonWidget.builder(Text.literal("Gotowe"), b -> this.close())
				.dimensions(cx - 100, cy + 44, 200, 20).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 40, 0xFFFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer,
				Text.literal("0% = ciemno, 100% = normalnie, 200% = jasno"), this.width / 2, this.height / 2 - 28, 0xFFAAAAAA);
	}

	private static class BrightnessSlider extends SliderWidget {
		BrightnessSlider(int x, int y, int w, int h) {
			super(x, y, w, h, Text.empty(), NickBrightnessConfig.brightness / (double) NickBrightnessConfig.MAX);
			updateMessage();
		}

		void setPercent(int percent) {
			this.value = percent / (double) NickBrightnessConfig.MAX;
			applyValue();
			updateMessage();
		}

		@Override
		protected void updateMessage() {
			int percent = (int) Math.round(this.value * NickBrightnessConfig.MAX);
			setMessage(Text.literal("Jasność nicków: " + percent + "%"));
		}

		@Override
		protected void applyValue() {
			NickBrightnessConfig.set((int) Math.round(this.value * NickBrightnessConfig.MAX));
		}
	}
}
