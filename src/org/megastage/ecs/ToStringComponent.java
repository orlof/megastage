package org.megastage.ecs;

import org.megastage.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToStringComponent {
    protected static transient HashMap<Class, Field[]> cache = new HashMap<>();

    protected static boolean copyableField(Field f) {
        int modifiers = f.getModifiers();
        return !(Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers));
    }

    protected Field[] getFields() {
        Field[] fields = cache.get(getClass());
        if (fields != null) {
            return fields;
        }
        Class currentClass = getClass();
        List<Field> list = loadFields(currentClass);
        fields = list.toArray(new Field[list.size()]);
        cache.put(currentClass, fields);
        return fields;
    }

    protected List<Field> loadFields(Class<?> klass) {
        Field[] declaredFields = klass.getDeclaredFields();
        List<Field> result = new ArrayList<>(declaredFields.length);
        for (Field f : declaredFields) {
            if (copyableField(f)) {
                f.setAccessible(true);
                result.add(f);
            }
        }
        klass = klass.getSuperclass();
        if (klass != null) {
            result.addAll(loadFields(klass));
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName()).append("(");
        Field[] fields = getFields();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Field f = fields[i];
            try {
                sb.append(f.getName()).append(": ");
                if (f.getType().equals(char.class)) {
                    sb.append((int) f.getChar(this));
                } else {
                    sb.append(f.get(this));
                }
            } catch (IllegalArgumentException ex) {
                Log.error("Cannot get fields", ex);
                //ex.printStackTrace();
            } catch (IllegalAccessException ex) {
                Log.error("Cannot get fields", ex);
                //ex.printStackTrace();
            }
        }
        sb.append(")");
        return sb.toString();
    }
    
}
