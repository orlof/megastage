
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class Deps {

    public static void main(String... args) throws Exception {
        for(String arg: args) {
            System.out.println(arg.toUpperCase());
            Class<?> c = Class.forName(arg);
            printFields(c);

            c = c.getSuperclass();
            while(c != null) {
                printFields(c);
                c = c.getSuperclass();
            }
        }
    }
    
    private static void printFields(Class c) {
        boolean nothing = true;

        Field[] flds = c.getDeclaredFields();
        for(Field f: flds) {
            if(f.getType().isPrimitive()) continue;
            
            Field canSave;
            try {
                canSave = f.getType().getField("CAN_SAVE");
                Object val = canSave.get(null);
                if((boolean) val) continue;
            } catch (Exception ex) {
            }
            if(nothing) {
                nothing = false;
                System.out.println("Checking: " + c.getName());
            }
            System.out.println("    " + f.getType().getName() + ": " + f.getName());
        }
    }
    
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isWrapperType(Class<?> clazz)
    {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
    
}