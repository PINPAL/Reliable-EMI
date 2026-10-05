package com.evandev.remi.util;

import com.evandev.remi.platform.Services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

// TODO: Shouldn't this just be part of the platform helper?
public class ClassScannerUtils {

	/**
	 * Walks all mod root paths provided by the platform helper and feeds loaded classes to the consumer.
	 */
	public static void scanModClasses(ClassConsumer consumer) {
		Map<String, List<Path>> modPaths = Services.PLATFORM.getModRootPaths();

		for (Map.Entry<String, List<Path>> entry : modPaths.entrySet()) {
			String modId = entry.getKey();
			for (Path rootPath : entry.getValue()) {
				try (var stream = Files.walk(rootPath)) {
					stream.filter(p -> p.toString().endsWith(".class")).forEach(path -> {
						String className = rootPath.relativize(path).toString().replace('/', '.').replace('\\', '.')
						                           .replaceAll("\\.class$", "");
						try {
							Class<?> clazz = Class.forName(className, false, ClassScannerUtils.class.getClassLoader());
							consumer.accept(modId, clazz);
						} catch (Throwable ignored) {
							// Ignore classes that fail to load during scanning
						}
					});
				} catch (Exception ignored) {
				}
			}
		}
	}

	public interface ClassConsumer {
		void accept(String modId, Class<?> clazz);
	}
}
