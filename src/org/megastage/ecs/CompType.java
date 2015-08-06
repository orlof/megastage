package org.megastage.ecs;

import java.lang.reflect.Field;

public class CompType {
    public static CompSpec[] specs = {
            new CompSpec("NONE", "org.megastage.components.generic.None", false),

            new CompSpec("FlagDelete", "org.megastage.components.generic.Flag", true),
            new CompSpec("FlagInitialize", "org.megastage.components.generic.Flag", false),
            new CompSpec("FlagPersistence", "org.megastage.components.generic.Flag", false),
            new CompSpec("FlagReplicate", "org.megastage.components.generic.Flag", false),
            new CompSpec("FlagSynchronize", "org.megastage.components.generic.Flag", false),
            new CompSpec("FlagUsable", "org.megastage.components.generic.Flag", true),

            new CompSpec("Acceleration", "org.megastage.components.generic.WrapperVector3f", false),
            new CompSpec("ClientRaster", "org.megastage.components.ClientRaster", false),
            new CompSpec("ClientVideoMemory", "org.megastage.components.ClientVideoMemory", false),
            new CompSpec("CollisionSphere", "org.megastage.components.CollisionSphere", false),
            new CompSpec("CollisionType", "org.megastage.components.CollisionType", false),
            new CompSpec("Energy", "org.megastage.components.Energy", false),
            new CompSpec("EngineData", "org.megastage.components.EngineData", false),
            new CompSpec("Explosion", "org.megastage.components.Explosion", true),
            new CompSpec("ForceFieldData", "org.megastage.components.ForceFieldData", false),
            new CompSpec("GyroscopeData", "org.megastage.components.GyroscopeData", false),
            new CompSpec("Identifier", "org.megastage.components.generic.WrapperString", true),
            new CompSpec("Mass", "org.megastage.components.Mass", false),
            new CompSpec("Mode", "org.megastage.components.Mode", true),
            new CompSpec("NodeComponent", "org.megastage.components.NodeComponent", false),
            new CompSpec("Position", "org.megastage.components.generic.WrapperVector3f", true),
            new CompSpec("RadarEcho", "org.megastage.components.generic.WrapperInteger", false),
            new CompSpec("RadarTargetData", "org.megastage.components.RadarTargetData", false),
            new CompSpec("Rotation", "org.megastage.components.generic.WrapperQuaternion", true),
            new CompSpec("SpawnPoint", "org.megastage.components.generic.WrapperVector3f", false),
            new CompSpec("ThermalLaserData", "org.megastage.components.ThermalLaserData", false),
            new CompSpec("Velocity", "org.megastage.components.generic.WrapperVector3f", false),
            new CompSpec("VectorAttack", "org.megastage.components.VectorAttack", false),

            new CompSpec("DCPU", "org.megastage.components.DCPU", false),
            new CompSpec("DCPUInterface", "org.megastage.components.device.DCPUInterface", false),
            new CompSpec("ConnectedTo", "org.megastage.components.generic.EntityReference", false),

            new CompSpec("EngineDevice", "org.megastage.components.device.EngineDevice", true),
            new CompSpec("PPSDevice", "org.megastage.components.PPSDevice", false),

            new CompSpec("DeviceBattery", "org.megastage.components.DeviceBattery", false),
            new CompSpec("DeviceClock", "org.megastage.components.DeviceClock", false),
            new CompSpec("DeviceFloppyDrive", "org.megastage.components.DeviceFloppyDrive", true),
            new CompSpec("DeviceForceField", "org.megastage.components.DeviceForceField", true),
            new CompSpec("DeviceGyroscope", "org.megastage.components.DeviceGyroscope", true),
            new CompSpec("DeviceKeyboard", "org.megastage.components.DeviceKeyboard", true),
            new CompSpec("DeviceMonitor", "org.megastage.components.DeviceMonitor", true),
            new CompSpec("DevicePowerController", "org.megastage.components.DevicePowerController", true),
            new CompSpec("DevicePowerPlant", "org.megastage.components.DevicePowerPlant", true),
            new CompSpec("DeviceRadar", "org.megastage.components.DeviceRadar", true),
            new CompSpec("DeviceThermalLaser", "org.megastage.components.DeviceThermalLaser", true),

            new CompSpec("BindTo", "org.megastage.components.BindTo", true),
            new CompSpec("Geometry", "org.megastage.components.Geometry", true),
            new CompSpec("BlockChanges", "org.megastage.components.BlockChanges", true),
            new CompSpec("CmdText", "org.megastage.components.CmdText", true),
            new CompSpec("CubeStructure", "org.megastage.components.CubeStructure", true)
    };

    static {
        for(int i=0; i<specs.length; i++) {
            specs[i].cid = i;

            try {
                Field field = CompType.class.getField(specs[i].name);
                field.setInt(null, i);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static int NONE;
    public static int FlagDelete;

    public static int FlagReplicate;
    public static int FlagInitialize;
    public static int FlagPersistence;
    public static int FlagSynchronize;
    public static int FlagUsable;

    public static int Acceleration = 20;
    public static int ClientRaster = 21;
    public static int ClientVideoMemory = 22;
    public static int CollisionSphere = 23;
    public static int CollisionType = 24;
    public static int Energy = 25;
    public static int EngineData = 26;
    public static int Explosion = 27;
    public static int FixedRotation = 28;
    public static int ForceFieldData = 29;
    public static int GyroscopeData = 30;
    public static int Identifier = 31;
    public static int Mass = 32;
    public static int Mode = 33;
    public static int NodeComponent = 34;
    public static int Position = 36;
    public static int PositionOffset = 37;
    public static int RadarEcho = 38;
    public static int RadarTargetData = 39;
    public static int Rotation = 40;
    public static int SpawnPoint = 41;
    public static int ThermalLaserData = 43;
    public static int Velocity = 44;
    public static int VectorAttack = 45;

    public static int DCPU = 50;
    public static int DCPUInterface = 51;
    public static int EngineDevice = 51;
    public static int PPSDevice = 51;

    public static int DeviceBattery = 52;
    public static int DeviceClock = 53;
    public static int DeviceFloppyDrive = 55;
    public static int DeviceForceField = 56;
    public static int DeviceGyroscope = 58;
    public static int DeviceKeyboard = 59;
    public static int DeviceMonitor = 60;
    public static int DevicePowerController = 62;
    public static int DevicePowerPlant = 63;
    public static int DeviceRadar = 64;
    public static int DeviceThermalLaser = 65;

    public static int DCPUConnection = 68;
    public static int BindTo = 69;
    public static int Geometry = 70;
    public static int BlockChanges = 90;
    public static int CmdText = 91;
    public static int CubeStructure = 92;

}
