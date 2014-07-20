package org.megastage.systems.client;

import org.megastage.util.Log;
import com.jme3.math.ColorRGBA;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.client.ClientVideoMemory;
import org.megastage.client.ClientGlobals;
import org.megastage.ecs.CompType;
import org.megastage.ecs.Processor;
import org.megastage.ecs.World;

public class ClientMonitorRenderSystem extends Processor {
    public ClientMonitorRenderSystem(World world, long interval) {
        super(world, interval, CompType.ClientVideoMemory, CompType.ClientRaster);
    }

    @Override
    protected void process(int eid) {
        ClientVideoMemory clientVideoMemory = (ClientVideoMemory) world.getComponent(eid, CompType.ClientVideoMemory);
        
        boolean blink = true;
        
        if(ClientGlobals.gfxSettings.ENABLE_LEM_BLINKING) {
            long time = System.currentTimeMillis() / 16L;
            blink = time / 20L % 2L == 0L;
        }
        
        if(clientVideoMemory.isDirty || blink != clientVideoMemory.blink) {
            clientVideoMemory.isDirty = false;
            clientVideoMemory.blink = blink;
            
            ClientRaster clientRaster = (ClientRaster) world.getComponent(eid, CompType.ClientRaster);
            render(clientVideoMemory, blink, clientRaster);
        }
    }

    public void render(ClientVideoMemory videoMemory, boolean blink, ClientRaster rasterComponent) {
        try {
            for (int row = 0; row < 12; row++) {
                for (int col = 0; col < 32; col++) {
                    int dat = videoMemory.screenMemRam[col + row * 32];
                    int charValue = dat & 0x7F;
                    int charOffsetInFontMemory = charValue * 2;

                    //int colorForeground = videoMemory.paletteMemRam[dat >> 12];
                    //int colorBackground = videoMemory.paletteMemRam[dat >> 8 & 0xF];
                    ColorRGBA colorForeground = videoMemory.colors[dat >> 12];
                    ColorRGBA colorBackground = videoMemory.colors[dat >> 8 & 0xF];

                    if (ClientGlobals.gfxSettings.ENABLE_LEM_BLINKING && blink && ((dat & 0x80) > 0)) {
                        colorForeground = colorBackground;
                    }

                    int pixelOffs = col * 4 + row * 8 * 128;

                    for (int x = 0; x < 4; x++) {
                        /*
                        word0 = 11111111 /
                                00001001
                        word1 = 00001001 /
                                00000000
                        */
                        int bits = videoMemory.fontMemRam[charOffsetInFontMemory + (x >> 1)] >> (x + 1 & 0x1) * 8 & 0xFF;
                        for (int y = 0; y < 8; y++) {
                            ColorRGBA color = (bits >> y & 0x1) == 1 ? colorForeground: colorBackground;
                            //view.pixels[(pixelOffs + x + y * 128)] = bit;
                            //TODO optimize
                            //c.fromIntARGB(0x33555555);
                            rasterComponent.raster.setPixel(col*4+x, row*8+y, color);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}