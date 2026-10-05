package com.evandev.remi.platform;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public interface IPlatformHelper {
    Path getConfigDirectory();

	/**
	 * Returns a map of mod id's to a list of root file paths for each mod's classes.
	 */
	Map<String, List<Path>> getModRootPaths();
}
