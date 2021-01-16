package com.github.thedeathlycow.thimbleprotect.events;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class ThimbleEventSerializer implements JsonSerializer<ThimbleEvent> {

    final Type objectType = new TypeToken<Map<String, Object>>() {}.getType();

    @Override
    public JsonElement serialize(ThimbleEvent event, Type typeOfSrc, JsonSerializationContext context) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("causingEntity", event.getCausingEntity().getUuidAsString());

        // handle serialisation for block update events
        if (event instanceof ThimbleBlockUpdateEvent) {
            ThimbleBlockUpdateEvent updateEvent = (ThimbleBlockUpdateEvent) event;

            map.put("preState", updateEvent.getPreState().toString());
            map.put("postState", updateEvent.getPostState().toString());
        }


        return context.serialize(map, objectType);
    }
}
