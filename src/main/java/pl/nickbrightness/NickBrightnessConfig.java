package pl.nickbrightness;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.LightmapTextureManager;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class NickBrightnessConfig {
	public static final int MIN = 0;
	public static final int MAX = 200;
	/** 100 = bez zmian (jak w grze), <100 ciemniej, >100 jaśniej (aż do pełnego światła). */
	public static int brightness = 100;

	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("nickbrightness.properties");

	private NickBrightnessConfig() {}

	public static void set(int value) {
		brightness = Math.max(MIN, Math.min(MAX, value));
		save();
	}

	public static void load() {
		if (!Files.exists(FILE)) return;
		try (Reader r = Files.newBufferedReader(FILE)) {
			Properties p = new Properties();
			p.load(r);
			brightness = Math.max(MIN, Math.min(MAX, Integer.parseInt(p.getProperty("brightness", "100").trim())));
		} catch (IOException | NumberFormatException ignored) {
			brightness = 100;
		}
	}

	public static void save() {
		try (Writer w = Files.newBufferedWriter(FILE)) {
			Properties p = new Properties();
			p.setProperty("brightness", Integer.toString(brightness));
			p.store(w, "Nick Brightness (0-200, 100 = normalnie)");
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
}
