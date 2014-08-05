package org.megastage.components.dcpu;

import org.jdom2.Element;
import org.megastage.ecs.BaseComponent;
import org.megastage.components.transfer.VirtualFloppyDriveData;
import org.megastage.ecs.ToStringComponent;
import org.megastage.ecs.World;
import org.megastage.protocol.Message;
import org.megastage.server.FloppyManager;

/**
 * Experimental, untested implementation of the MF35D Floppy Drive
 *
 * @author Herobrine
 *
 * The Mackapar 3.5" Floppy Drive is compatible with all standard 3.5" 1440 KB
 * floppy disks. The floppies need to be formatted in 16 bit mode, for a total
 * of 737,280 words of storage. Data is saved on 80 tracks with 18 sectors per
 * track, for a total of 1440 sectors containing 512 words each. The M35FD works
 * is asynchronous, and has a raw read/write speed of 30.7kw/s. Track seeking
 * time is about 2.4 ms per track.
 *
 * TODO: Switch to coarser, 60hz time-keeping. Current time-keeping is a bit
 * flawed. TODO: Test this more.
 */
public class VirtualFloppyDrive extends DCPUHardware {

    public static final char STATE_NO_MEDIA = 0x0000; //There's no floppy in the drive.
    public static final char STATE_READY = 0x0001; //The drive is ready to accept commands.
    public static final char STATE_READY_WP = 0x0002; //Same as ready, except the floppy is write protected.
    public static final char STATE_BUSY = 0x0003; //The drive is busy either reading or writing a sector.
    public static final char ERROR_NONE = 0x0000; //There's been no error since the last poll.
    public static final char ERROR_BUSY = 0x0001; //Drive is busy performing an action
    public static final char ERROR_NO_MEDIA = 0x0002; //Attempted to read or write with no floppy inserted.
    public static final char ERROR_PROTECTED = 0x0003; //Attempted to write to write protected floppy.
    public static final char ERROR_EJECT = 0x0004; //The floppy was removed while reading or writing.
    public static final char ERROR_BAD_SECTOR = 0x0005; //The requested sector is broken, the data on it is lost.
    public static final char ERROR_BROKEN = 0xffff; //There's been some major software or hardware problem, try turning off and turning on the device again.
    public static final int TRACKS_PER_DISK = 80;
    public static final int SECTORS_PER_TRACK = 18;
    public static final int WORDS_PER_SECTOR = 512;
    public static final int MAX_SECTOR = 1439;
    private static final int SEEK_CYCLES_PER_TRACK = 240;
    private static final int READ_CYCLES_PER_SECTOR = 1668;
    private static final int WRITE_CYCLES_PER_SECTOR = 1668;
    
    private char state = STATE_NO_MEDIA;
    private char error = ERROR_NONE;
    private boolean interruptsEnabled;
    private char message;
    private int track;
    
    private FloppyDisk floppy;
    private FloppyOperation operation = new FloppyOperation(FloppyOperationType.NONE, 0, 0, Long.MAX_VALUE);

    @Override
    public BaseComponent[] init(World world, int parentEid, Element element) throws Exception {
        super.init(world, parentEid, element);
        setInfo(TYPE_FLOPPY, 0x000b, MANUFACTORER_MACKAPAR);
        return null;
    }

    @Override
    public Message synchronize(int eid) {
        return VirtualFloppyDriveData.create().synchronize(eid);
    }
    
    @Override
    public void interrupt(DCPU dcpu) {
        int a = dcpu.registers[0];
        if (a == 0) {
            dcpu.registers[1] = state;
            dcpu.registers[2] = error;
            //It is unclear whether this should qualify as a change (interrupt-triggering)
            error = ERROR_NONE;
        } else if (a == 1) {
            if (dcpu.registers[3] == 0) {
                interruptsEnabled = false;
            } else {
                interruptsEnabled = true;
                message = dcpu.registers[3];
            }
        } else if (a == 2) {
            int sector = dcpu.registers[3];
            if (sector <= MAX_SECTOR && (state == STATE_READY || state == STATE_READY_WP)) {
                operation = new FloppyOperation(FloppyOperationType.READ, sector, dcpu.registers[4],
                        dcpu.cycles + READ_CYCLES_PER_SECTOR + SEEK_CYCLES_PER_TRACK
                        * Math.abs(track - (sector / SECTORS_PER_TRACK)));
                dcpu.registers[1] = 1;
                setState(dcpu, STATE_BUSY);
            } else {
                dcpu.registers[1] = 0;
                if (state == STATE_BUSY) {
                    setError(dcpu, ERROR_BUSY);
                } else if (state == STATE_NO_MEDIA) {
                    setError(dcpu, ERROR_NO_MEDIA);
                } else if (sector > MAX_SECTOR) {
                    setError(dcpu, ERROR_BAD_SECTOR);
                }
            }
        } else if (a == 3) {
            int sector = dcpu.registers[3];
            if (sector <= MAX_SECTOR && state == STATE_READY) {
                operation = new FloppyOperation(FloppyOperationType.WRITE, sector, dcpu.registers[4],
                        dcpu.cycles + WRITE_CYCLES_PER_SECTOR + SEEK_CYCLES_PER_TRACK
                        * Math.abs(track - (sector / SECTORS_PER_TRACK)));
                dcpu.registers[1] = 1;
                setState(dcpu, STATE_BUSY);
            } else {
                dcpu.registers[1] = 0;
                if (state == STATE_BUSY) {
                    setError(dcpu, ERROR_BUSY);
                } else if (state == STATE_NO_MEDIA) {
                    setError(dcpu, ERROR_NO_MEDIA);
                } else if (state == STATE_READY_WP) {
                    setError(dcpu, ERROR_PROTECTED);
                } else if (sector > MAX_SECTOR) {
                    setError(dcpu, ERROR_BAD_SECTOR);
                }
            }
        }
    }

    private void setState(DCPU dcpu, char state) {
        setState(dcpu, state, error);
    }

    private void setError(DCPU dcpu, char error) {
        setState(dcpu, state, error);
    }

    private void setState(DCPU dcpu, char state, char error) {
        if (this.state != state || this.error != error) {
            this.state = state;
            this.error = error;
            if (interruptsEnabled) {
                dcpu.interrupt(message);
            }
        }
    }

    @Override
    public void tick60hz(DCPU dcpu) {
        //Log.info(operation.finish + " <= " + dcpu.cycles);
        if (operation.finish <= dcpu.cycles) {
            switch (operation.type) {
                case READ:
                    for (int i = 0; i < 512; i++) {
                        if (operation.memory + i <= 65535) { //TODO
                            dcpu.ram[operation.memory + i] = floppy.data[operation.sector * WORDS_PER_SECTOR + i];
                        }
                    }
                    track = operation.sector / SECTORS_PER_TRACK;
                    operation = new FloppyOperation(FloppyOperationType.NONE, 0, 0, Long.MAX_VALUE);
                    setState(dcpu, floppy.isWriteProtected() ? STATE_READY_WP : STATE_READY, ERROR_NONE);
                    break;
                case WRITE:
                    for (int i = 0; i < 512; i++) {
                        floppy.data[operation.sector * WORDS_PER_SECTOR + i] = dcpu.ram[operation.memory + i];
                    }
                    track = operation.sector / SECTORS_PER_TRACK;
                    operation = new FloppyOperation(FloppyOperationType.NONE, 0, 0, Long.MAX_VALUE);
                    setState(dcpu, STATE_READY, ERROR_NONE);
                    break;
                case NONE:
                    //new FloppyOperation(FloppyOperation.NONE, 0, 0, Long.MAX_VALUE);
                    break;
            }
        }
    }

    public void insert(DCPU dcpu, String title) {
        this.floppy = FloppyManager.floppies.get(title);
        if (floppy.isWriteProtected()) {
            setState(dcpu, STATE_READY_WP);
        } else {
            setState(dcpu, STATE_READY);
        }
    }

    public FloppyDisk eject(DCPU dcpu) {
        FloppyDisk ejected = floppy;
        floppy = null;
        
        if (state == STATE_BUSY) {
            operation = new FloppyOperation(FloppyOperationType.NONE, 0, 0, Long.MAX_VALUE);
            setState(dcpu, STATE_NO_MEDIA, ERROR_EJECT);
        } else {
            setState(dcpu, STATE_NO_MEDIA);
        }
        return ejected;
    }

    public FloppyDisk getDisk() {
        return floppy;
    }
}

enum FloppyOperationType {
    NONE, READ, WRITE;
}

class FloppyOperation extends ToStringComponent {
    FloppyOperationType type;
    int sector;
    int memory;
    long finish;

    public FloppyOperation() {}
    
    public FloppyOperation(FloppyOperationType type, int sector, int memory, long cycles) {
        this.type = type;
        this.sector = sector;
        this.memory = memory;
        this.finish = cycles;
    }
}
