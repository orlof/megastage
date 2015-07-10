package org.megastage.util;

import com.jme3.math.Vector3f;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.ecs.CompType;
import org.megastage.ecs.World;

public class DCPUUtil {
    public static char writeFloatToMemory(char[] mem, char ptr, float val) {
        int bits = Float.floatToIntBits(val);

        mem[ptr++] = (char) (bits >> 16);
        mem[ptr++] = (char) bits;

        return ptr;
    }

    public static char getChar(float val, float min, float max) {
        int result = (int) ((val - min) / (max - min) * Character.MAX_VALUE);

        if(result < 0) return 0;
        if(result > Character.MAX_VALUE) return Character.MAX_VALUE;

        return (char) result;
    }

    public static float getFloat(char val, float min, float max) {
        return (((float) val) / Character.MAX_VALUE) * (max - min) + min;
    }

    public static char writeRadiansToMemory(char[] mem, char ptr, double rad) {
        // sign bit
        char result = rad < 0 ? (char) 0x8000: 0x0000;

        double degrees = Math.abs(Math.toDegrees(rad));
        result |= (Math.round(degrees) % 360) << 6;
        result |= Math.round(60.0 * degrees) % 60;

        mem[ptr++] = result;

        return ptr;
    }

    public static char writePitchAndYawToMemory(char[] mem, char ptr, int shipEid, int targetEid) {
        // target direction
        Rotation shipRot = (Rotation) World.INSTANCE.getComponent(shipEid, CompType.Rotation);

        // vector from me to target in global coordinate system
        Position shipPos = (Position) World.INSTANCE.getComponent(shipEid, CompType.Position);

        Position targetPos = (Position) World.INSTANCE.getComponent(targetEid, CompType.Position);

        Vector3f delta = targetPos.get().subtract(shipPos.get());

        shipRot.rotateLocal(delta);

        double pitch = Math.atan2(delta.y, Math.sqrt(delta.x*delta.x + delta.z*delta.z));
        double yaw = Math.atan2(delta.x, -delta.z);

        //Log.info(""+Math.toDegrees(pitch));
        //Log.info(""+Math.toDegrees(yaw));

        ptr = writeRadiansToMemory(mem, ptr, pitch);
        ptr = writeRadiansToMemory(mem, ptr, yaw);

        return ptr;
    }

}
