package org.megastage.ecs;

import org.megastage.util.MegastageException;

import java.lang.reflect.Field;

public class CompType {
    public static int size;

    static {
        Field[] declaredFields = CompType.class.getDeclaredFields();
        for(Field f: declaredFields) {
            if(!f.getName().equals("size")) {
                try {
                    f.setInt(null, size++);
                } catch (IllegalAccessException e) {
                    throw new MegastageException(e);
                }
            }
        }
    }

    public static int None;

    public static int FlagReplicate;
    public static int FlagDelete;
    public static int FlagInitialize;
    public static int FlagUsable;

    public static int ChatMessage;
    public static int PlayerCharacter;
    public static int SpawnPoint;

    public static int BindTo;
    public static int Geometry;
    public static int Identifier;
    public static int Position;
    public static int Rotation;

    public static int DCPU;
    public static int DCPUConnection;
    public static int DCPUInterface;

    public static int DeviceBattery;
    public static int InterfaceBattery;

    public static int DeviceClock;
    public static int InterfaceClock;

    public static int DeviceEngine;
    public static int InterfaceEngine;

    public static int DeviceFloppyDrive;
    public static int InterfaceFloppyDrive;

    public static int DeviceForceField;
    public static int InterfaceForceField;

    public static int DeviceGyroscope;
    public static int InterfaceGyroscope;

    public static int DeviceKeyboard;
    public static int InterfaceKeyboard;

    public static int DeviceMonitor;
    public static int InterfaceMonitor;

    public static int DevicePowerController;
    public static int InterfacePowerController;

    public static int DevicePowerPlant;
    public static int InterfacePowerPlant;

    public static int DevicePPS;
    public static int InterfacePPS;

    public static int DeviceRadar;
    public static int InterfaceRadar;

    public static int DeviceThermalLaser;
    public static int InterfaceThermalLaser;


    public static int getCID(BaseComponent bc) {
        try {
            return CompType.class.getField(bc.getClass().getSimpleName()).getInt(bc);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new MegastageException(e);
        }
    }
}
