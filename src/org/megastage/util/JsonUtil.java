package org.megastage.util;

import com.esotericsoftware.jsonbeans.Json;
import com.esotericsoftware.jsonbeans.JsonValue;
import com.jme3.math.Vector3f;
import org.megastage.ecs.Entity;

public class JsonUtil {
    public static Json create() {
        Json json = new Json();

        json.setSerializer(Vector3f.class, new Json.ReadOnlySerializer<Vector3f>() {
            @Override
            public Vector3f read(Json json, JsonValue jsonValue, Class aClass) {
                return toVector3f(jsonValue);
            }
        });

        json.setSerializer(Entity.class, new Json.ReadOnlySerializer<Entity>() {
            @Override
            public Entity read(Json json, JsonValue jsonValue, Class aClass) {
                return null;
            }
        });

        return json;
    }

    public static Vector3f toVector3f(JsonValue jsonValue) {
        String[] val = jsonValue.getString("vector", "0.0 0.0 0.0").split(" ");
        return new Vector3f(
                Float.parseFloat(val[0]),
                Float.parseFloat(val[1]),
                Float.parseFloat(val[2])
        );
    }
}
