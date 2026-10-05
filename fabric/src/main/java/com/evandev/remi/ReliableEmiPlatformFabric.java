package com.evandev.remi;

import com.evandev.remi.platform.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReliableEmiPlatformFabric implements IPlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

	@Override
	public Map<String, List<Path>> getModRootPaths() {
		Map<String, List<Path>> modPaths = new HashMap<>();
		for (ModContainer mod : FabricLoader.getInstance().getAllMods()) {
			modPaths.put(mod.getMetadata().getId(), mod.getRootPaths());
		}
		return modPaths;
	}
}
