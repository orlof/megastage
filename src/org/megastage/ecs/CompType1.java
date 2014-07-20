package org.megastage.ecs;

import org.megastage.util.Log;
import java.lang.reflect.Field;

public class CompType1 {
    public static transient int size = 0;
    public static transient final int[] parent = new int[100];
    
    public static final int ReplicateFlag = size;
    public static final int NONE = size++;
    
    public static final int AffectedByGravityFlag = size++;
    public static final int DeleteFlag = size++;
    public static final int GravityFieldFlag = size++;
    public static final int InitializeFlag = size++;
    public static final int PersistenceFlag = size++;
    public static final int SynchronizeFlag = size++;
    public static final int ReplicateToNewConnectionsFlag = size++;
    public static final int ReplicateToAllConnectionsFlag = size++;
    public static final int UsableFlag = size++;
    
    public static final int Acceleration = size++;
    public static final int ClientRaster = size++;
    public static final int ClientVideoMemory = size++;
    public static final int CollisionSphere = size++;
    public static final int CollisionType = size++;
    public static final int Energy = size++;
    public static final int EngineData = size++;
    public static final int Explosion = size++;
    public static final int FixedRotation = size++;
    public static final int ForceFieldData = size++;
    public static final int GyroscopeData = size++;
    public static final int Identifier = size++;
    public static final int Mass = size++;
    public static final int Mode = size++;
    public static final int Orbit = size++;
    public static final int Position = size++;
    public static final int RadarEcho = size++;
    public static final int RadarTargetData = size++;
    public static final int Rotation = size++;
    public static final int SpawnPoint = size++;
    public static final int SphereOfInfluence = size++;
    public static final int ThermalLaserData = size++;
    public static final int Velocity = size++;
    
    public static final int DCPU = size++;
    
    public static final int DCPUHardware = size++;

    public static final int VirtualBattery = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualClock = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualEngine = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualFloppyDrive = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualForceField = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualGravitySensor = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualGyroscope = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualKeyboard = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualMonitor = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualPPS = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualPowerController = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualPowerPlant = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualRadar = size;
    static { parent[size++] = DCPUHardware; }
    public static final int VirtualThermalLaser = size;
   static { parent[size++] = DCPUHardware; }
 
    public static final int BindTo = size++;

    public static final int Geometry = size++;
 
    public static final int BatteryGeometry = size(); // size;
    static { parent[size++] = Geometry; }
    public static final int CharacterGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int EngineGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int FloppyDriveGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int ForceFieldGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int GyroscopeGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int ImposterGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int MonitorGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int PPSGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int PlanetGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int PowerPlantGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int RadarGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int ShipGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int SunGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int ThermalLaserGeometry = size;
    static { parent[size++] = Geometry; }
    public static final int VoidGeometry = size;
    static { parent[size++] = Geometry; }

    public static transient final String[] map = new String[size+1];

    static {
        Field[] declaredFields = CompType1.class.getDeclaredFields();
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
            Field f = CompType1.class.getField(simpleName);
            return f.getInt(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private static int size() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
