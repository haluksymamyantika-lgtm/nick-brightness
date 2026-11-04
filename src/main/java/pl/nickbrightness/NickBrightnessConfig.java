package pl.nickbrightness;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

public final class NickBrightnessConfig {
	/** Światło: 0-200, 100 = normalnie, 200 = pełne światło. */
	public static final int MIN = 0;
	public static final int MAX = 200;
	/** Rozjaśnienie kolorów: 100-300 (1x-3x). */
	public static final int COLOR_MIN = 100;
	public static final int COLOR_MAX = 300;
	/** Tło nicku: 0-100, 100 = normalnie, 0 = brak tła. */
	public static final int BG_MIN = 0;
	public static final int BG_MAX = 100;

	public static int brightness = 100;
	public static int colorBoost = 100;
	public static int background = 100;

	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nickbrightness.properties");

	private NickBrightnessConfig() {}

	private static int clamp(int v, int min, int max) {
		return Math.max(min, Math.min(max, v));
	}

	public static void set(int value) {
		brightness = clamp(value, MIN, MAX);
		save();
	}

	public static void setColor(int value) {
		colorBoost = clamp(value, COLOR_MIN, COLOR_MAX);
		save();
	}

	public static void setBackground(int value) {
		background = clamp(value, BG_MIN, BG_MAX);
		save();
	}

	public static void load() {
		if (!Files.exists(FILE)) return;
		try (Reader r = Files.newBufferedReader(FILE)) {
			Properties p = new Properties();
			p.load(r);
			brightness = clamp(Integer.parseInt(p.getProperty("brightness", "100").trim()), MIN, MAX);
			colorBoost = clamp(Integer.parseInt(p.getProperty("colorBoost", "100").trim()), COLOR_MIN, COLOR_MAX);
			background = clamp(Integer.parseInt(p.getProperty("background", "100").trim()), BG_MIN, BG_MAX);
		} catch (IOException | NumberFormatException ignored) {
			brightness = 100;
			colorBoost = 100;
			background = 100;
		}
	}

	public static void save() {
		try (Writer w = Files.newBufferedWriter(FILE)) {
			Properties p = new Properties();
			p.setProperty("brightness", Integer.toString(brightness));
			p.setProperty("colorBoost", Integer.toString(colorBoost));
			p.setProperty("background", Integer.toString(background));
			p.store(w, "Nick Brightness: brightness 0-200, colorBoost 100-300, background 0-100 (100 = normalnie)");
		} catch (IOException ignored) {}
	}

	/** Przelicza packed light nicku według ustawionej jasności. */
	public static int apply(int light) {
		int p = brightness;
		if (p == 100) return light;
		int block = LightmapTextureManager.getBlockLightCoordinates(light);
		int sky = LightmapTextureManager.getSkyLightCoordinates(light);
		if (p < 100) {
			float f = p / 100f;
			block = Math.round(block * f);
			sky = Math.round(sky * f);
		} else {
			float t = (p - 100) / 100f;
			block = Math.round(block + (15 - block) * t);
			sky = Math.round(sky + (15 - sky) * t);
		}
		return LightmapTextureManager.pack(block, sky);
	}

	/** Skaluje przezroczystość tła nicku (kolor ARGB). */
	public static int applyBackground(int argb) {
		if (background >= 100) return argb;
		int a = (argb >>> 24) & 0xFF;
		a = Math.round(a * (background / 100f));
		return (argb & 0x00FFFFFF) | (a << 24);
	}

	/** Rozjaśnia kolory tekstu nicku (kolory drużyn, rang itp.). */
	public static Text boostText(Text text) {
		if (colorBoost <= 100) return text;
		final float f = colorBoost / 100f;
		final MutableText out = Text.empty();
		text.<Boolean>visit((style, str) -> {
			TextColor c = style.getColor();
			Style s = c == null ? style : style.withColor(TextColor.fromRgb(brighten(c.getRgb(), f)));
			out.append(Text.literal(str).setStyle(s));
			return Optional.empty();
		}, Style.EMPTY);
		return out;
	}

	private static int brighten(int rgb, float f) {
		float t = Math.min(1f, (f - 1f) / 2f) * 0.5f; // przy 3x dodatkowo do 50% w stronę bieli
		int r = mix((rgb >> 16) & 0xFF, f, t);
		int g = mix((rgb >> 8) & 0xFF, f, t);
		int b = mix(rgb & 0xFF, f, t);
		return (r << 16) | (g << 8) | b;
	}

	private static int mix(int c, float f, float t) {
		float v = Math.min(255f, c * f);
		v = v + (255f - v) * t;
		return Math.round(Math.min(255f, v));
	}
}
