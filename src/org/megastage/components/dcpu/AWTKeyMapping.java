package org.megastage.components.dcpu;

public class AWTKeyMapping extends KeyMapping
{
    public AWTKeyMapping(boolean useLowercase)
    {
        //TODO Find more keycodes?
        //backspace
        map(0x08, 0x10);
        //return
        map(0x0A, 0x11);
        //insert No Type
        map(0x9B, 0x12);
        //delete
        map(0x7F, 0x13);
        //ASCII 0x20-0x7F
        map(0x20, 0x20);
        map(0xDE, 0x27);
        map(0x2C, 0x2C);
        map(0x2D, 0x2D);
        map(0x2E, 0x2E);
        map(0x2F, 0x2F);
        map(0x30, 0x30);
        map(0x31, 0x31);
        map(0x32, 0x32);
        map(0x33, 0x33);
        map(0x34, 0x34);
        map(0x35, 0x35);
        map(0x36, 0x36);
        map(0x37, 0x37);
        map(0x38, 0x38);
        map(0x39, 0x39);

        map(0x3B, 0x3B);

        map(0x3D, 0x3D);
        //Note: Notch uses lowercase characters for key codes, but many in the community seem to prefer uppercase
        //The specification for Generic Keyboard (compatible) doesn't specify which to use. TODO Make it an option.
        map(0x41, 0x61-(useLowercase?0:0x20));
        map(0x42, 0x62-(useLowercase?0:0x20));
        map(0x43, 0x63-(useLowercase?0:0x20));
        map(0x44, 0x64-(useLowercase?0:0x20));
        map(0x45, 0x65-(useLowercase?0:0x20));
        map(0x46, 0x66-(useLowercase?0:0x20));
        map(0x47, 0x67-(useLowercase?0:0x20));
        map(0x48, 0x68-(useLowercase?0:0x20));
        map(0x49, 0x69-(useLowercase?0:0x20));
        map(0x4A, 0x6A-(useLowercase?0:0x20));
        map(0x4B, 0x6B-(useLowercase?0:0x20));
        map(0x4C, 0x6C-(useLowercase?0:0x20));
        map(0x4D, 0x6D-(useLowercase?0:0x20));
        map(0x4E, 0x6E-(useLowercase?0:0x20));
        map(0x4F, 0x6F-(useLowercase?0:0x20));
        map(0x50, 0x70-(useLowercase?0:0x20));
        map(0x51, 0x71-(useLowercase?0:0x20));
        map(0x52, 0x72-(useLowercase?0:0x20));
        map(0x53, 0x73-(useLowercase?0:0x20));
        map(0x54, 0x74-(useLowercase?0:0x20));
        map(0x55, 0x75-(useLowercase?0:0x20));
        map(0x56, 0x76-(useLowercase?0:0x20));
        map(0x57, 0x77-(useLowercase?0:0x20));
        map(0x58, 0x78-(useLowercase?0:0x20));
        map(0x59, 0x79-(useLowercase?0:0x20));
        map(0x5A, 0x7A-(useLowercase?0:0x20));
        map(0x5B, 0x5B);
        map(0x5C, 0x5C);
        map(0x5D, 0x5D);


        map(0xC0, 0x60);

        //Already mapped to 0x13
        //map(0x7F, 0x7F);

        //Arrow up No Type
        map(0x26, 0x80);
        //Arrow down No Type
        map(0x28, 0x81);
        //Arrow left No Type
        map(0x25, 0x82);
        //Arrow right No Type
        map(0x27, 0x83);
        //Shift No Type
        map(0x10, 0x90);
        //Control No Type
        map(0x11, 0x91);

        //NUMPAD - NUMLOCK ON
        map(0x60, 0x30); //0
        map(0x61, 0x31); //1
        map(0x62, 0x32); //2
        map(0x63, 0x33); //3
        map(0x64, 0x34); //4
        map(0x65, 0x35); //5
        map(0x66, 0x36); //6
        map(0x67, 0x37); //7
        map(0x68, 0x38); //8
        map(0x69, 0x39); //9
        map(0x6A, 0x2A); //*
        map(0x6B, 0x2B); //+
        map(0x6D, 0x2D); //-
        map(0x6E, 0x2E); //.
        map(0x6F, 0x2F); ///

//  	map(32, 32);
//  	
//    map(38, 128);
//    map(40, 129);
//    map(37, 130);
//    map(39, 131);
//
//    map(10, 17);
//    map(8, 16);
//    map(155, 18);
//    map(127, 19);
//
//    map(16, 144);
//    map(17, 145);
//    map(18, 145);
//    map(65406, 145);
//
//    map(49, 49);
//    map(50, 50);
//    map(51, 51);
//    map(52, 52);
//    map(53, 53);
//    map(54, 54);
//    map(55, 55);
//    map(56, 56);
//    map(57, 57);
//    map(48, 48);
//
//    map(81, 113);
//    map(87, 119);
//    map(69, 101);
//    map(82, 114);
//    map(84, 116);
//    map(89, 121);
//    map(85, 117);
//    map(73, 105);
//    map(79, 111);
//    map(80, 112);
//
//    map(65, 97);
//    map(83, 115);
//    map(68, 100);
//    map(70, 102);
//    map(71, 103);
//    map(72, 104);
//    map(74, 106);
//    map(75, 107);
//    map(76, 108);
//
//    map(90, 122);
//    map(88, 120);
//    map(67, 99);
//    map(86, 118);
//    map(66, 98);
//    map(78, 110);
//    map(77, 109);
    }
}