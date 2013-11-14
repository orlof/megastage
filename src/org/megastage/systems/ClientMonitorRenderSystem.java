package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.esotericsoftware.minlog.Log;
import com.jme3.math.ColorRGBA;
import org.megastage.components.client.ClientRaster;
import org.megastage.components.client.ClientVideoMemory;

public class ClientMonitorRenderSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<ClientVideoMemory> videoMemoryMapper;
    @Mapper ComponentMapper<ClientRaster> rasterComponentMapper;

    public ClientMonitorRenderSystem() {
        super(Aspect.getAspectForAll(ClientVideoMemory.class, ClientRaster.class));
    }

    @Override
    protected void process(Entity entity) {
        ClientVideoMemory videoMemory = videoMemoryMapper.get(entity);

        long time = System.currentTimeMillis() / 16L;
        boolean blink = time / 20L % 2L == 0L;
        
        if(videoMemory.isDirty || blink != videoMemory.blink) {
            videoMemory.isDirty = false;
            videoMemory.blink = blink;
            
            ClientRaster rasterComponent = rasterComponentMapper.get(entity);
            render(videoMemory, blink, rasterComponent);
        }
    }

    public void render(ClientVideoMemory videoMemory, boolean blink, ClientRaster rasterComponent) {
        try {
            for (int row = 0; row < 12; row++) {
                for (int col = 0; col < 32; col++) {
                    int dat = videoMemory.screenMemRam[col + row * 32];
                    int charValue = dat & 0x7F;
                    int charOffsetInFontMemory = charValue * 2;

                    int colorForeground = videoMemory.paletteMemRam[dat >> 12];
                    int colorBackground = videoMemory.paletteMemRam[dat >> 8 & 0xF];

                    if (blink && ((dat & 0x80) > 0)) {
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
                            int bit = (bits >> y & 0x1) == 1 ? colorForeground: colorBackground;
                            //view.pixels[(pixelOffs + x + y * 128)] = bit;
                            //TODO optimize
                            ColorRGBA c = new ColorRGBA();
                            c.fromIntARGB(bit);
                            rasterComponent.raster.setPixel(col*4+x, row*8+y, c);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}