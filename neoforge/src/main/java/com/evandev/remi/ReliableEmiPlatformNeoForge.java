package com.evandev.remi;

import com.evandev.remi.platform.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.language.IModInfo;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReliableEmiPlatformNeoForge implements IPlatformHelper {
    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

	@Override
	public Map<String, List<Path>> getModRootPaths() {
		Map<String, List<Path>> modPaths = new HashMap<>();
		for (IModInfo mod : ModList.get().getMods()) {
			String modId = mod.getModId();
			Path rootPath = ModList.get().getModFileById(modId).getFile().getFilePath();
			modPaths.put(modId, List.of(rootPath));
		}
		return modPaths;
	}
}
