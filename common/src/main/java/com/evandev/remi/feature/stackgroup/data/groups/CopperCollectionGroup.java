package com.evandev.remi.feature.stackgroup.data.groups;

import com.evandev.remi.feature.stackgroup.data.StackGroup;
import com.evandev.remi.util.ClassScannerUtils;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopperCollection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class CopperCollectionGroup extends StackGroup {

	private static final Map<String, WeatheringCopperCollection<?>> INDEXED_COLLECTIONS = new HashMap<>();
	private static boolean isIndexed = false;

	private final Set<Identifier> targetIds;

	public CopperCollectionGroup(Identifier id, String namespace, String key) {
		super(id, null);
		ensureIndexed();

		String lookupKey = namespace.toLowerCase(Locale.ROOT) + ":" + key.toUpperCase(Locale.ROOT);
		WeatheringCopperCollection<?> collection = INDEXED_COLLECTIONS.get(lookupKey);

		if (collection == null) {
			throw new IllegalArgumentException("No WeatheringCopperCollection registered for " + lookupKey);
		}

		this.targetIds = extractIdsFromCollection(collection);
		// TODO: validate the stack group and then save it to a JSON config on first time we index it (avoid end user fiddling with JSON files)
	}

	private static synchronized void ensureIndexed() {
		if (isIndexed) return;
		isIndexed = true;

		// TODO: again, why is this not part of the platform helper?
		ClassScannerUtils.scanModClasses((modId, clazz) -> {
			for (Field field : clazz.getDeclaredFields()) {
				if (Modifier.isStatic(field.getModifiers()) &&
				    WeatheringCopperCollection.class.isAssignableFrom(field.getType())) {
					try {
						field.setAccessible(true);
						Object value = field.get(null);
						if (value instanceof WeatheringCopperCollection<?> collection) {
							String key = modId.toLowerCase(Locale.ROOT) + ":" +
							             field.getName().toUpperCase(Locale.ROOT);
							INDEXED_COLLECTIONS.put(key, collection);
						}
					} catch (Throwable ignored) {
					}
				}
			}
		});
	}

	private static Set<Identifier> extractIdsFromCollection(WeatheringCopperCollection<?> collection) {
		Set<Identifier> ids = new HashSet<>();

		// Convert collection to list or stream
		// FIXME: this is cursed
		for (Object obj : collection.asList()) {
			if (obj == null) continue;
			extractAndAddIdentifier(ids, obj);
		}

		return Set.copyOf(ids);
	}

	// TODO: this is disgusting edge case if statement spaghetti even AI could do better smh
	private static void extractAndAddIdentifier(Set<Identifier> ids, Object obj) {
		// 1. Direct Identifier
		if (obj instanceof Identifier id) {
			ids.add(id);
			return;
		}

		// 2. Direct Item / Block
		if (obj instanceof Item item) {
			ids.add(BuiltInRegistries.ITEM.getKey(item));
			return;
		}
		if (obj instanceof Block block) {
			ids.add(BuiltInRegistries.ITEM.getKey(block.asItem()));
			return;
		}

		// 3. Handle BlockFamily (Extract base block + variant blocks like stairs, slabs, etc.)
		if (obj instanceof net.minecraft.data.BlockFamily family) {
			// Base block
			ids.add(BuiltInRegistries.ITEM.getKey(family.getBaseBlock().asItem()));
			// Variant blocks (stairs, slabs, walls, etc.)
			family.getVariants().values().forEach(block -> ids.add(BuiltInRegistries.ITEM.getKey(block.asItem())));
			return;
		}

		// 4. Reflective fallback (for BlockItemId, Holder, or custom Mod wrappers)
		try {
			// Try getting Item directly (e.g., obj.item())
			Method itemMethod = obj.getClass().getMethod("item");
			Object itemObj = itemMethod.invoke(obj);
			extractAndAddIdentifier(ids, itemObj);
			return;
		} catch (Exception ignored) {}

		try {
			// Try getting Identifier directly (e.g., obj.identifier() or obj.getId())
			Method idMethod = obj.getClass().getMethod("identifier");
			if (idMethod.invoke(obj) instanceof Identifier id) {
				ids.add(id);
				return;
			}
		} catch (Exception ignored) {}

		try {
			// Try getting Block directly (e.g., obj.block() or obj.getBlock())
			Method blockMethod = obj.getClass().getMethod("block");
			Object blockObj = blockMethod.invoke(obj);
			extractAndAddIdentifier(ids, blockObj);
		} catch (Exception ignored) {}
	}

	@Override
	public boolean match(EmiIngredient ingredient) {
		return ingredient instanceof EmiStack stack && stack.getId() != null && targetIds.contains(stack.getId());
	}

	@Override
	public Set<Identifier> getOptimizedIds() {
		return targetIds;
	}

}