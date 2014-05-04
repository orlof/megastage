package org.megastage.ecs;

import java.lang.reflect.Field;

public class CompType {
    public static int size = 0;
    public static final int NONE = size++;
    
    public static final int AffectedByGravityFlag = size++;
    public static final int DeleteFlag = size++;
    public static final int GravityFieldFlag = size++;
    public static final int InitializeFlag = size++;
    public static final int PersistenceFlag = size++;
    public static final int SynchronizeFlag = size++;
    public static final int ReplicateFlag = size++;
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
    
    public static final int VirtualBattery = size;
    public static final int VirtualClock = size;
    public static final int VirtualEngine = size;
    public static final int VirtualFloppyDrive = size;
    public static final int VirtualForceField = size;
    public static final int VirtualGravitySensor = size;
    public static final int VirtualGyroscope = size;
    public static final int VirtualKeyboard = size;
    public static final int VirtualMonitor = size;
    public static final int VirtualPPS = size;
    public static final int VirtualPowerController = size;
    public static final int VirtualPowerPlant = size;
    public static final int VirtualRadar = size;
    public static final int VirtualThermalLaser = size;
    public static final int DCPUHardware = size++;

    public static final int BindTo = size++;

    public static final int BatteryGeometry = size;
    public static final int CharacterGeometry = size;
    public static final int EngineGeometry = size;
    public static final int FloppyDriveGeometry = size;
    public static final int ForceFieldGeometry = size;
    public static final int GyroscopeGeometry = size;
    public static final int ImposterGeometry = size;
    public static final int MonitorGeometry = size;
    public static final int PPSGeometry = size;
    public static final int PlanetGeometry = size;
    public static final int PowerPlantGeometry = size;
    public static final int RadarGeometry = size;
    public static final int ShipGeometry = size;
    public static final int SunGeometry = size;
    public static final int ThermalLaserGeometry = size;
    public static final int VoidGeometry = size;
    public static final int Geometry = size++;
    
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
