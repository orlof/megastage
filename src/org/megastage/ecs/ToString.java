package org.megastage.ecs;

import org.megastage.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class ToString {
    private static transient HashMap<Class, Field[]> cache = new HashMap<>();

    private static boolean copyableField(Field f) {
        int modifiers = f.getModifiers();
        return !(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers));
    }

    private static Field[] getFields(Class clazz) {
        Field[] fields = cache.get(clazz);
        if (fields != null) {
            return fields;
        }
        List<Field> list = loadFields(clazz);
        fields = list.toArray(new Field[list.size()]);
        cache.put(clazz, fields);
        return fields;
    }

    private static List<Field> loadFields(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        List<Field> result = new ArrayList<>(declaredFields.length);
        for (Field f : declaredFields) {
            if (copyableField(f)) {
                f.setAccessible(true);
                result.add(f);
            }
        }
        clazz = clazz.getSuperclass();
        if (clazz != null) {
            result.addAll(loadFields(clazz));
        }
        return result;
    }

    public static String make(Object obj) {
        Class clazz = obj.getClass();
        StringBuilder sb = new StringBuilder();
        sb.append(clazz.getSimpleName()).append("(");
        Field[] fields = getFields(clazz);
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Field f = fields[i];
            try {
                sb.append(f.getName()).append(": ");
                if (f.getType().equals(char.class)) {
                    sb.append((int) f.getChar(obj));
                } else {
                    sb.append(f.get(obj));
                }
            } catch (IllegalArgumentException ex) {
                Log.error("Cannot get fields", ex);
            } catch (IllegalAccessException ex) {
                Log.error("Cannot get fields", ex);
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
}
