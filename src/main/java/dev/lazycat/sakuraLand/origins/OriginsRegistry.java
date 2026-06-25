package dev.lazycat.sakuraLand.origins;

import java.util.HashMap;
import java.util.Map;

/**
 * Регистр рас
 */
public class OriginsRegistry {
    /**
     * Map для рас. Позволяет хранить их в неком регистре.
     */
    private static final Map<String, Origin> origins = new HashMap<>();

    /**
     * Регистраиця рассы.
     * @param origin - ссылка на рассу которую вы хотите зарегистрировать
     */
    public static void register(Origin origin) {
        origins.put(origin.getId(), origin);
    }

    /**
     * Получение рассы по id
     *
     * @param id id рассы
     * @return рассу
     */
    public static Origin get(String id) {
        return origins.get(id);
    }

    public static java.util.Collection<Origin> getAllOrigins() {
        return origins.values();
    }

}
