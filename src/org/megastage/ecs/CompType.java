package org.megastage.ecs;

import org.megastage.util.Log;
import java.lang.reflect.Field;

public class CompType {
    public static transient int size = 100;
    public static transient final int[] parent = new int[100];
    
    public static final int ReplicateFlag = 0;
    public static final int NONE = 0;
    
    public static final int AffectedByGravityFlag = 10;
    public static final int DeleteFlag = 11;
    public static final int GravityFieldFlag = 12;
    public static final int InitializeFlag = 13;
    public static final int PersistenceFlag = 14;
    public static final int SynchronizeFlag = 15;
    public static final int UsableFlag = 16;
    
    public static final int Acceleration = 20;
    public static final int ClientRaster = 21;
    public static final int ClientVideoMemory = 22;
    public static final int CollisionSphere = 23;
    public static final int CollisionType = 24;
    public static final int Energy = 25;
    public static final int EngineData = 26;
    public static final int Explosion = 27;
    public static final int FixedRotation = 28;
    public static final int ForceFieldData = 29;
    public static final int GyroscopeData = 30;
    public static final int Identifier = 31;
    public static final int Mass = 32;
    public static final int Mode = 33;
    public static final int NodeComponent = 34;
    public static final int Orbit = 35;
    public static final int Position = 36;
    public static final int PositionOffset = 37;
    public static final int RadarEcho = 38;
    public static final int RadarTargetData = 39;
    public static final int Rotation = 40;
    public static final int SpawnPoint = 41;
    public static final int SphereOfInfluence = 42;
    public static final int ThermalLaserData = 43;
    public static final int Velocity = 44;
    
    public static final int DCPU = 50;
    
    public static final int DCPUHardware = 51;

    public static final int VirtualBattery = 52;
    static { parent[52] = 51; }
    public static final int VirtualClock = 53;
    static { parent[53] = 51; }
    public static final int VirtualEngine = 54;
    static { parent[54] = 51; }
    public static final int VirtualFloppyDrive = 55;
    static { parent[55] = 51; }
    public static final int VirtualForceField = 56;
    static { parent[56] = 51; }
    public static final int VirtualGravitySensor = 57;
    static { parent[57] = 51; }
    public static final int VirtualGyroscope = 58;
    static { parent[58] = 51; }
    public static final int VirtualKeyboard = 59;
    static { parent[59] = 51; }
    public static final int VirtualMonitor = 60;
    static { parent[60] = 51; }
    public static final int VirtualPPS = 61;
    static { parent[61] = 51; }
    public static final int VirtualPowerController = 62;
    static { parent[62] = 51; }
    public static final int VirtualPowerPlant = 63;
    static { parent[63] = 51; }
    public static final int VirtualRadar = 64;
    static { parent[64] = 51; }
    public static final int VirtualThermalLaser = 65;
    static { parent[65] = 51; }
 
    public static final int BindTo = 69;

    public static final int GeometryComponent = 70;
 
    public static final int BatteryGeometry = 71;
    static { parent[71] = 70; }
    public static final int CharacterGeometry = 72;
    static { parent[72] = 70; }
    public static final int EngineGeometry = 73;
    static { parent[73] = 70; }
    public static final int FloppyDriveGeometry = 74;
    static { parent[74] = 70; }
    public static final int ForceFieldGeometry = 75;
    static { parent[75] = 70; }
    public static final int GyroscopeGeometry = 76;
    static { parent[76] = 70; }
    public static final int ImposterGeometry = 77;
    static { parent[77] = 0; }
    public static final int MonitorGeometry = 78;
    static { parent[78] = 70; }
    public static final int PPSGeometry = 79;
    static { parent[79] = 70; }
    public static final int PlanetGeometry = 80;
    static { parent[80] = 70; }
    public static final int PowerPlantGeometry = 81;
    static { parent[81] = 70; }
    public static final int RadarGeometry = 82;
    static { parent[82] = 70; }
    public static final int ShipGeometry = 83;
    static { parent[83] = 70; }
    public static final int SunGeometry = 84;
    static { parent[84] = 70; }
    public static final int ThermalLaserGeometry = 85;
    static { parent[85] = 70; }
    public static final int VoidGeometry = 86;
    static { parent[86] = 70; }

    public static final int BlockChanges = 90;

    public static transient final String[] map = new String[100];

    static {
        Field[] declaredFields = CompType.class.getDeclaredFields();
        for (Field field : declaredFields) {
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                    !java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                try {
                    //Log.info(field.getInt(null) + " " + field.getName());
                    map[field.getInt(null)] = field.getName();
                } catch (IllegalArgumentException ex) {
                    Log.error(ex.toString());
                } catch (IllegalAccessException ex) {
                    Log.error(ex.toString());
                }
            }
        }
    }
    
    public static int cid(String simpleName) {
        try {
            Field f = CompType.class.getField(simpleName);
            return f.getInt(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }
}
