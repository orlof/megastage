package org.megastage.ecs;

import org.jdom2.Element;
import org.megastage.util.XmlUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

public class CompType {
    public static int size;
    
    public static CompSpec[] specs;
    public static final HashMap<String, CompSpec> specMap = new HashMap<>();

    public static void init(Element root) {
        ArrayList<CompSpec> list = new ArrayList<>();
        for(Element comp: root.getChildren()) {
            try {
                CompSpec spec = new CompSpec(
                        comp.getName(), 
                        Class.forName(XmlUtil.getStringValue(comp, "class")),
                        size++,
                        XmlUtil.getBooleanValue(comp, "replicable"));

                list.add(spec);
                specMap.put(spec.name, spec);
                
                Field field = CompType.class.getField(spec.name);
                field.setInt(null, spec.cid);
            } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        specs = list.toArray(new CompSpec[list.size()]);
    }

    public static CompSpec getSpec(int cid) {
        return specs[cid];
    }
    public static CompSpec getSpec(String name) {
        return specMap.get(name);
    }

    public static int FlagReplicate;
    public static int NONE;
    
    public static int FlagDelete = 11;
    public static int FlagInitialize = 13;
    public static int FlagPersistence = 14;
    public static int FlagSynchronize = 15;
    public static int FlagUsable = 16;
    
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
