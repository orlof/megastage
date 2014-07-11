package org.megastage;

public class Test1 {
    public static final float VIEW_DIST_LINEAR = 20000.0f;
    public static final float VIEW_DIST_LOG = 20000.0f;
    public static final float REAL_DIST_FAR = 1.0e12f;
    public static final float K = VIEW_DIST_LOG / ((float) Math.log(REAL_DIST_FAR / VIEW_DIST_LINEAR));
    
    public static void main(String[] args) throws Exception {
        /*  k = bits of depthbuffer (16/24/32)
         *  z = z coord in viewspace
         *  zn = near plane
         *  zf = far plane
         *  ( 2^k – 1 ) should be stored as constant, as pow k isnt a very cheap op
         *  log( zf / zn ) could also be constant, at optimisation stage once far + near is fix
         */
        
        // alternative with constants, K = ( 2^k – 1 ) / log( zf / zn )
        calculate(0.1f);
        calculate(1e0f);
        calculate(1e1f);
        calculate(1e2f);
        calculate(1e3f);
        calculate(1e4f);
        calculate(20000f);
        calculate(1e5f);
        calculate(1e6f);
        calculate(1e7f);
        calculate(1e8f);
        calculate(1e9f);
        calculate(1e10f);
        calculate(1e11f);
        calculate(1e12f);
    }
    
    private static float depth(float d) {
        if(d < VIEW_DIST_LINEAR) {
            return d;
        } else {
            return VIEW_DIST_LINEAR + K * ((float) Math.log( d / VIEW_DIST_LINEAR ));
        }
    }

    private static void calculate(float z) {
        System.out.println(z + " -> " + depth(z));
    }
}
