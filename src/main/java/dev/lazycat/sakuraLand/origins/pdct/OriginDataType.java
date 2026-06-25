package dev.lazycat.sakuraLand.origins.pdct;

import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;

public class OriginDataType implements PersistentDataType<String, Origin> {

    public static final OriginDataType INSTANCE = new OriginDataType();
    private OriginDataType() {}

    @Override
    public @NonNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NonNull Class<Origin> getComplexType() {
        return Origin.class;
    }

    @Override
    public @NonNull String toPrimitive(@NonNull Origin complex, @NonNull PersistentDataAdapterContext context) {
        return complex.getId();
    }

    @Override
    public Origin fromPrimitive(@NonNull String primitive, @NonNull PersistentDataAdapterContext context) {
        return OriginsRegistry.get(primitive);
    }
}