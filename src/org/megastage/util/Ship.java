package org.megastage.util;

import com.cubes.Vector3Int;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import org.megastage.components.Position;
import org.megastage.components.Rotation;
import org.megastage.components.gfx.ShipGeometry;
import org.megastage.ecs.ECSException;
import org.megastage.server.Hit;
import org.megastage.server.NoHit;
import org.megastage.server.ShipStructureHit;
import org.megastage.server.Target;

public class Ship {

    public static Hit getHit(Target target) throws ECSException {
        Vector3f pos = Position.getWorldCoordinates(target.eid);
        Quaternion rot = Rotation.getWorldRotation(target.eid);
        
        Ship ship = ShipGeometry.getShip(target.eid);
        return ship.getHit(target.weaponPosition, target.attackVector, pos, rot);
    }

    private int version;
    
    private ShipSegment segments;
    private Vector3f sumOfMass;
    private Vector3f centerOfMass;
    private Vector3f midPoint;
    private float mass;
    
    private float[] xAxisMass;
    private float[] yAxisMass;
    private float[] zAxisMass;
    
    private Ship() {}
    
    public Ship(int size) {
        segments = ShipSegment.create(size);
        sumOfMass = new Vector3f();
        midPoint = new Vector3f(size / 2.0f, size / 2.0f, size / 2.0f);

        xAxisMass = new float[size];
        yAxisMass = new float[size];
        zAxisMass = new float[size];
    }

    public Vector3f getCenterOfMass() {
        return centerOfMass;
    }
    
    public float getMass() {
        return mass;
    }
    
    public float getCollisionRadius() {
        return segments.maybeHitRange;
    }
    
    public char getBlock(int x, int y, int z) {
        return segments.get(x, y, z);
    }
    
    public char setBlock(int x, int y, int z, char value) {
        char oldValue = segments.set(x, y, z, value);

        if(oldValue > 0) {
            mass -= 1.0f;
        }
        
        if(value > 0) {
            mass += 1.0f;
        }

        sumOfMass.addLocal(x + 0.5f, y + 0.5f, z + 0.5f);
        
        if(mass > 0.0f) {
            centerOfMass = sumOfMass.divide(mass);
        } else {
            centerOfMass = midPoint;
        }
        
        xAxisMass[x] += mass;
        yAxisMass[y] += mass;
        zAxisMass[z] += mass;

        version++;
        
        return oldValue;
    }
    
    public Hit getHit(Vector3f wpnPos, Vector3f attVec, Vector3f shipPos, Quaternion shipRot) {
        // rotate weapon's coordinate system so that target has its native orientation
        
        wpnPos.subtractLocal(shipPos);
        shipRot.inverseLocal();
        shipRot.multLocal(wpnPos);
        shipPos.subtractLocal(wpnPos);
        shipRot.multLocal(attVec);
        
        shipPos.subtractLocal(centerOfMass);

        return segments.getHit(attVec, shipPos);
    }

    public int getSize() {
        return segments.size;
    }

    public float getXAxisInertia() {
        return getInertia(centerOfMass.x, xAxisMass);
    }
    
    public float getYAxisInertia() {
        return getInertia(centerOfMass.y, yAxisMass);
    }
    
    public float getZAxisInertia() {
        return getInertia(centerOfMass.z, zAxisMass);
    }
    
    public float getInertia(float center, float[] distribution) {
        float inertia = 0.0f;
        for(int coord = 0; coord < distribution.length; coord++) {
            float r = FastMath.abs(coord + 0.5f - center);
            inertia += distribution[coord] * r * r;
        }
        return inertia;
    }

    public int getVersion() {
        return version;
    }
    
    public static abstract class ShipSegment implements Comparable<ShipSegment> {
        protected static final Vector3f[] xyzf = new Vector3f[] {
            new Vector3f(-1.0f, -1.0f, -1.0f),
            new Vector3f(-1.0f, -1.0f, +1.0f),
            new Vector3f(-1.0f, +1.0f, -1.0f),
            new Vector3f(-1.0f, +1.0f, +1.0f),
            new Vector3f(+1.0f, -1.0f, -1.0f),
            new Vector3f(+1.0f, -1.0f, +1.0f),
            new Vector3f(+1.0f, +1.0f, -1.0f),
            new Vector3f(+1.0f, +1.0f, +1.0f)
        };

        protected static final Vector3Int[] xyzi = new Vector3Int[] {
            new Vector3Int(0, 0, 0),
            new Vector3Int(0, 0, 1),
            new Vector3Int(0, 1, 0),
            new Vector3Int(0, 1, 1),
            new Vector3Int(1, 0, 0),
            new Vector3Int(1, 0, 1),
            new Vector3Int(1, 1, 0),
            new Vector3Int(1, 1, 1)
        };

        protected int size;
        protected Vector3f segmentOffset;
        protected Vector3Int base;
        protected float maybeHitRange;

        protected float tmpDistance;

        private ShipSegment() {}
        
        protected ShipSegment(int size) {
            // this constructor is called only for top level ship
            this.size = size;

            base = new Vector3Int(0, 0, 0);
            segmentOffset = xyzf[7].mult(size / 2.0f);
            maybeHitRange = segmentOffset.length();
        }

        protected ShipSegment(int size, Vector3Int parentBase, int segmentIndex) {
            this.size = size;

            segmentOffset = xyzf[segmentIndex].mult(size / 2.0f);
            base = parentBase.add(xyzi[segmentIndex].mult(size));
            maybeHitRange = segmentOffset.length();
        }

        public static ShipSegment create(int size) {
            if(size == 1) {
                return new ShipCell();
            } else {
                return new ShipChunk(size);
            }
        }

        public static ShipSegment create(int size, Vector3Int parentBase, int segmentIndex) {
            if(size == 1) {
                return new ShipCell(parentBase, segmentIndex);
            } else {
                return new ShipChunk(size, parentBase, segmentIndex);
            }
        }

        public abstract char get(int x, int y, int z);
        public abstract char set(int x, int y, int z, char value);

        public Hit getHit(Vector3f attVec, Vector3f center) {
            if(isEmpty()) {
                // nothing to hit
                return NoHit.INSTANCE;
            }

            center = center.add(segmentOffset);

            if(isRange(attVec, center) && isFront(attVec, center) && isHit(attVec, center)) {
                // maybe hit
                return getSubSegmentHit(attVec, center);
            }

            // not hit
            return NoHit.INSTANCE;
        }

        public boolean isHit(Vector3f attVec, Vector3f center) {
            return MathUtil.distanceFromPointToLine(center, attVec) < maybeHitRange;
        }

        private boolean isRange(Vector3f attVec, Vector3f center) {
            return center.length() - maybeHitRange < attVec.length();
        }

        private boolean isFront(Vector3f attVec, Vector3f center) {
            return attVec.angleBetween(center) < FastMath.HALF_PI || center.length() < maybeHitRange;
        }

        protected abstract Hit getSubSegmentHit(Vector3f attVec, Vector3f center);

        public abstract boolean isEmpty();

        protected final int getSubSegmentIndex(int x, int y, int z, int delimit) {
            int segmentIndex = 0;
            if(x >= delimit) segmentIndex |= 4;
            if(y >= delimit) segmentIndex |= 2;
            if(z >= delimit) segmentIndex |= 1;

            return segmentIndex;
        }

        void updateDistance(Vector3f center) {
            tmpDistance = center.add(segmentOffset).length();
        }

        @Override
        public int compareTo(ShipSegment o) {
            return Float.compare(tmpDistance, o.tmpDistance);
        }
    }


    public static class ShipChunk extends ShipSegment {
        int delimit;
        int mask;
        boolean empty = true;

        ShipSegment[] subs = new ShipSegment[8];
        
        private ShipChunk() {}

        public ShipChunk(int size) {
            // this constructor is called only for top level ship
            super(size);

            delimit = size / 2;
            mask = delimit - 1;
        }

        public ShipChunk(int size, Vector3Int parentBase, int segmentIndex) {
            super(size, parentBase, segmentIndex);

            delimit = size / 2;
            mask = delimit - 1;
        }

        @Override
        public char get(int x, int y, int z) {
            int subSegmentIndex = getSubSegmentIndex(x, y, z, delimit);
            if(subs[subSegmentIndex] == null) {
                return 0;
            }

            return subs[subSegmentIndex].get(x & mask, y & mask, z & mask);
        }

        @Override
        public char set(int x, int y, int z, char val) {
            int subSegmentIndex = getSubSegmentIndex(x, y, z, delimit);

            if(subs[subSegmentIndex] == null) {
                subs[subSegmentIndex] = create(delimit, base, subSegmentIndex);
                empty = false;
            }

            return subs[subSegmentIndex].set(x & mask, y & mask, z & mask, val);
        }

        @Override
        public boolean isEmpty() {
            return empty;
        }

        @Override
        protected Hit getSubSegmentHit(Vector3f attVec, Vector3f center) {
            Bag<ShipSegment> bag = new Bag<>(8);

            for(ShipSegment ss: subs) {
                if(ss != null) {
                    ss.updateDistance(center);
                    bag.add(ss);
                }
            }

            bag.sort();

            for(ShipSegment ss: bag) {
                Hit hit = ss.getHit(attVec, center);
                if(hit != NoHit.INSTANCE) {
                    return hit;
                }
            }

            return NoHit.INSTANCE;
        }

    }

    public static class ShipCell extends ShipSegment {
        private char value;

        ShipCell() {
            // this constructor is called only for top level ship
            super(1);
        }

        ShipCell(Vector3Int parentBase, int segmentIndex) {
            super(1, parentBase, segmentIndex);
        }

        @Override
        public char get(int x, int y, int z) {
            return value;
        }

        @Override
        public char set(int x, int y, int z, char val1) {
            char val0 = value;
            value = val1;
            return val0; 
        }

        @Override
        public boolean isEmpty() {
            return value == 0;
        }

        @Override
        protected Hit getSubSegmentHit(Vector3f attVec, Vector3f center) {
    //        Log.mark();
            if(iterate(attVec, center, 0.5f)) {
                return new ShipStructureHit(base, center.length());
            } 

            return NoHit.INSTANCE;
        }

        private boolean iterate(Vector3f attVec, Vector3f center, float radius) {
            float dist = MathUtil.distanceFromPointToLine(center, attVec);

            if(dist < radius) {
                return true;
            }

            float maybeHitRange = (float) Math.sqrt(3.0f * radius * radius);

            if(dist < maybeHitRange) {
                //Log.info("maybe hit for radius %f", maybeHitRange);
                float subRadius = radius / 2.0f;
                if(subRadius < 0.1f) {
                    return true;
                }

                for(Vector3f offset: xyzf) {
                    if(iterate(attVec, center.add(offset.mult(subRadius)), subRadius)) {
                        return true;
                    }
                }
            }

            return false;
        }
    }
    
}

