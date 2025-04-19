package me.luucka.teleportbow.js;

import lombok.Getter;
import me.luucka.teleportbow.TeleportBow;
import me.luucka.teleportbow.data.ParticleEffectsData;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.script.*;

public final class JavaScriptManager {

	private final ScriptEngineManager manager;
	private ScriptEngineFactory engineFactory;
	private final ScriptEngine engine;
	
	@Getter
	private static final JavaScriptManager instance = new JavaScriptManager();

	private JavaScriptManager() {
		manager = new ScriptEngineManager();
		try {
			engineFactory = (ScriptEngineFactory) Class.forName("org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory").newInstance();
		} catch (final ReflectiveOperationException ex) {
			ex.printStackTrace();

			TeleportBow.getInstance().getLogger().severe("Engine error: " + ex.getMessage());
		}
		manager.registerEngineName("Nashorn-TeleportBow", engineFactory);
		engine = manager.getEngineByName("Nashorn-TeleportBow");
	}

	public void eval(final ParticleEffectsData particleEffectsData, final Player player, final Location location) {
		final Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.clear();
		bindings.put("player", player);
		bindings.put("location", location);
		bindings.put("particle", particleEffectsData.getParticle());
		bindings.put("plugin", TeleportBow.getInstance());

		try {
			final Object result = /*engine.eval(String.join("\n", lines))*/ engine.eval(particleEffectsData.getScript());

//			TeleportBow.getInstance().getLogger().severe(result == null ? "No result." : "Got (" + result.getClass().getSimpleName() + "): " + result);

		} catch (final Exception ex) {
			ex.printStackTrace();

			TeleportBow.getInstance().getLogger().severe("Engine error: " + ex.getMessage());
		}
	}
}
