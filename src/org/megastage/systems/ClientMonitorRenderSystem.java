package org.megastage.systems;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import org.megastage.client.Game;
import org.megastage.components.client.VirtualMonitorView;

public class ClientMonitorRenderSystem extends EntityProcessingSystem {
    @Mapper ComponentMapper<VirtualMonitorView> viewMapper;

    public ClientMonitorRenderSystem() {
        super(Aspect.getAspectForAll(VirtualMonitorView.class));
    }

    @Override
    protected void process(Entity entity) {
        VirtualMonitorView view = viewMapper.get(entity);

        long time = System.currentTimeMillis() / 16L;
        boolean blink = time / 20L % 2L == 0L;
        
        if(view.isDirty || blink != view.blink) {
            view.isDirty = false;
            view.blink = blink;
            render(view, blink);
        }
    }

    public void render(VirtualMonitorView view, boolean blink) {
        try {
            for (int row = 0; row < 12; row++) {
                for (int col = 0; col < 32; col++) {
                    int dat = view.screenMemRam[col + row * 32];
                    int charValue = dat & 0x7F;
                    int charOffsetInFontMemory = charValue * 2;

                    int colorForeground = view.paletteMemRam[dat >> 12];
                    int colorBackground = view.paletteMemRam[dat >> 8 & 0xF];

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
                        int bits = view.fontMemRam[charOffsetInFontMemory + (x >> 1)] >> (x + 1 & 0x1) * 8 & 0xFF;
                        for (int y = 0; y < 8; y++) {
                            int bit = (bits >> y & 0x1) == 1 ? colorForeground: colorBackground;
                            //view.pixels[(pixelOffs + x + y * 128)] = bit;
                            view.img.setRGB(col*4+x, row*8+y, bit);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}