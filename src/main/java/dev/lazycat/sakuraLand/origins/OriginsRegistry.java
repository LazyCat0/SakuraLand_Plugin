package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;

import java.util.HashMap;
import java.util.Map;

public class OriginsRegistry {
    private static final Map<String, Origin> origins = new HashMap<>();

    public static void register(Origin origin) {
        origins.put(origin.getId(), origin);
    }

    public static Origin get(String id) {
        return origins.get(id);
    }

    public static java.util.Collection<Origin> getAllOrigins() {
        return origins.values();
    }

}
