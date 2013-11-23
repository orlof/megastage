package jmeplanet;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.shapes.ConcaveShape;
import com.bulletphysics.collision.shapes.TriangleCallback;
import com.bulletphysics.linearmath.Transform;
import com.jme3.math.Vector3f;

public class PlanetShape extends ConcaveShape {
    
    protected Vector3f center;
    protected float radius;
    protected HeightDataSource dataSource;
    protected javax.vecmath.Vector3f scaling;

    public PlanetShape(Vector3f center, float radius, HeightDataSource dataSource) {
        this.center = center;
        this.radius = radius;
        this.dataSource = dataSource;
    }
    
    @Override
    public void getAabb(Transform t, javax.vecmath.Vector3f aabbMin, javax.vecmath.Vector3f aabbMax) {
        t = null;
	aabbMin.set(center.x-radius, center.y-radius, center.z-radius);
	aabbMax.set(center.x+radius, center.y+radius, center.z+radius);
    }

    @Override
    public void processAllTriangles(TriangleCallback callback, javax.vecmath.Vector3f aabbMin, javax.vecmath.Vector3f aabbMax) {
        
        // add local translation
        aabbMin.add(convert(this.center));
        aabbMax.add(convert(this.center));
        
        // calculate 8 corners of the AABB
        Vector3f bottomFrontLeft = convert(aabbMin);
        Vector3f bottomFrontRight = new Vector3f(aabbMax.x, aabbMin.y, aabbMin.z);
        Vector3f bottomBackLeft = new Vector3f(aabbMin.x, aabbMin.y, aabbMax.z);
        Vector3f bottomBackRight = new Vector3f(aabbMax.x, aabbMin.y, aabbMax.z);
        Vector3f topBackRight = convert(aabbMax);
        Vector3f topFrontRight = new Vector3f(aabbMax.x, aabbMax.y, aabbMin.z);
        Vector3f topBackLeft = new Vector3f(aabbMin.x, aabbMax.y, aabbMax.z);
        Vector3f topFrontLeft = new Vector3f(aabbMin.x, aabbMax.y, aabbMin.z);

        // calculate the midpoint of each of the 6 sides of the AABB
        Vector3f halfSize = topBackRight.subtract(bottomFrontLeft).mult(0.5f);
        Vector3f frontCenter = new Vector3f(aabbMin.x + halfSize.x, aabbMin.y + halfSize.y, aabbMin.z);
        Vector3f bottomCenter = new Vector3f(aabbMin.x + halfSize.x, aabbMin.y, aabbMin.z + halfSize.z);
        Vector3f leftCenter = new Vector3f(aabbMin.x, aabbMin.y + halfSize.y, aabbMin.z + halfSize.z);
        Vector3f backCenter = new Vector3f(aabbMin.x + halfSize.x, aabbMin.y + halfSize.y, aabbMax.z);
        Vector3f topCenter = new Vector3f(aabbMin.x + halfSize.x, aabbMax.y, aabbMin.z + halfSize.z);
        Vector3f rightCenter = new Vector3f(aabbMax.x, aabbMin.y + halfSize.y, aabbMin.z + halfSize.z);

        // calculate the position of the vertex on the terrain by "projecting" the corners of the AABB
        Vector3f bottomFrontLeftVertex = calculateTerrainVertex(bottomFrontLeft, this.center);
        Vector3f bottomFrontRightVertex = calculateTerrainVertex(bottomFrontRight, this.center);
        Vector3f bottomBackLeftVertex = calculateTerrainVertex(bottomBackLeft, this.center);
        Vector3f bottomBackRightVertex = calculateTerrainVertex(bottomBackRight, this.center);
        Vector3f topBackRightVertex = calculateTerrainVertex(topBackRight, this.center);
        Vector3f topFrontRightVertex = calculateTerrainVertex(topFrontRight, this.center);
        Vector3f topBackLeftVertex = calculateTerrainVertex(topBackLeft, this.center);
        Vector3f topFrontLeftVertex = calculateTerrainVertex(topFrontLeft, this.center);

        // determine which of the corners of the AABB are colliding with the terrain
        boolean bottomFrontLeftCollides = bottomFrontLeft.distance(this.center) <= bottomFrontLeftVertex.distance(this.center);
        boolean bottomFrontRightCollides = bottomFrontRight.distance(this.center) <= bottomFrontRightVertex.distance(this.center);
        boolean bottomBackLeftCollides = bottomBackLeft.distance(this.center) <= bottomBackLeftVertex.distance(this.center);
        boolean bottomBackRightCollides = bottomBackRight.distance(this.center) <= bottomBackRightVertex.distance(this.center);
        boolean topBackRightCollides = topBackRight.distance(this.center) <= topBackRightVertex.distance(this.center);
        boolean topFrontRightCollides = topFrontRight.distance(this.center) <= topFrontRightVertex.distance(this.center);
        boolean topBackLeftCollides = topBackLeft.distance(this.center) <= topBackLeftVertex.distance(this.center);
        boolean topFrontLeftCollides = topFrontLeft.distance(this.center) <= topFrontLeftVertex.distance(this.center);

        // find which 3 sides are closest in order to prevent extraneous triangle processing
        boolean frontCloser = frontCenter.distance(this.center) < backCenter.distance(this.center);
        boolean bottomCloser = bottomCenter.distance(this.center) < topCenter.distance(this.center);
        boolean leftCloser = leftCenter.distance(this.center) < rightCenter.distance(this.center);

        if (frontCloser)
        {
          //create triangles for front side
          if (bottomFrontLeftCollides || bottomFrontRightCollides || topFrontRightCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(bottomFrontLeftVertex), convert(bottomFrontRightVertex), convert(topFrontRightVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
          if (topFrontRightCollides || topFrontLeftCollides || bottomFrontLeftCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(topFrontRightVertex), convert(topFrontLeftVertex), convert(bottomFrontLeftVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
        }
        else
        {
          //create triangles for back side
          if (bottomBackLeftCollides || bottomBackRightCollides || topBackRightCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(bottomBackLeftVertex), convert(bottomBackRightVertex), convert(topBackRightVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
          if (topBackRightCollides || topBackLeftCollides || bottomBackLeftCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(topBackRightVertex), convert(topBackLeftVertex), convert(bottomBackLeftVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
        }

        if (bottomCloser)
        {
          //create triangles for bottom side
          if (bottomFrontLeftCollides || bottomFrontRightCollides || bottomBackRightCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(bottomFrontLeftVertex), convert(bottomFrontRightVertex), convert(bottomBackRightVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
          if (bottomBackRightCollides || bottomBackLeftCollides || bottomFrontLeftCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(bottomBackRightVertex), convert(bottomBackLeftVertex), convert(bottomFrontLeftVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
        }
        else
        {
          //create triangles for top side
          if (topFrontLeftCollides || topFrontRightCollides || topBackRightCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(topFrontLeftVertex), convert(topFrontRightVertex), convert(topBackRightVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
          if (topBackRightCollides || topBackLeftCollides || topFrontLeftCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(topBackRightVertex), convert(topBackLeftVertex), convert(topFrontLeftVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
        }

        if (leftCloser)
        {
          //create triangles for left side
          if (bottomBackLeftCollides || bottomFrontLeftCollides || topFrontLeftCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(bottomBackLeftVertex), convert(bottomFrontLeftVertex), convert(topFrontLeftVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
          if (topFrontLeftCollides || topBackLeftCollides || bottomBackLeftCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(topFrontLeftVertex), convert(topBackLeftVertex), convert(bottomBackLeftVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
        }
        else
        {
          //create triangles for right side
          if (bottomBackRightCollides || bottomFrontRightCollides || topFrontRightCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(bottomBackRightVertex), convert(bottomFrontRightVertex), convert(topFrontRightVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
          if (topFrontRightCollides || topBackRightCollides || bottomBackRightCollides)
              {
                javax.vecmath.Vector3f vertices[] = new javax.vecmath.Vector3f[]
                    {convert(topFrontRightVertex), convert(topBackRightVertex), convert(bottomBackRightVertex)};
                callback.processTriangle(vertices, 0, 0);
              }
        }        
    }
    
    @Override
    public String getName() {
        return "Planet";
    }

    @Override
    public void calculateLocalInertia(float mass, javax.vecmath.Vector3f inertia) {
        
    }

    @Override
    public void setLocalScaling(javax.vecmath.Vector3f scaling) {
        this.scaling = scaling;
    }

    @Override
    public BroadphaseNativeType getShapeType() {
       return BroadphaseNativeType.FAST_CONCAVE_MESH_PROXYTYPE;
    }

    @Override
    public javax.vecmath.Vector3f getLocalScaling(javax.vecmath.Vector3f scaling) {
        return this.scaling;
    }
    
    private Vector3f calculateTerrainVertex(Vector3f position, Vector3f center)
    {        
        Vector3f normalized = position.subtract(center).normalize();
        float result = this.dataSource.getValue(normalized);
        return normalized.mult(radius + result);
    }
    
    private com.jme3.math.Vector3f convert(javax.vecmath.Vector3f oldVec)
    {
        com.jme3.math.Vector3f newVec = new com.jme3.math.Vector3f();
        convert(oldVec,newVec);
        return newVec;
    }

    private void convert(javax.vecmath.Vector3f oldVec, com.jme3.math.Vector3f newVec)
    {
        newVec.x = oldVec.x;
        newVec.y = oldVec.y;
        newVec.z = oldVec.z;
    }

    private javax.vecmath.Vector3f convert(com.jme3.math.Vector3f oldVec)
    {
        javax.vecmath.Vector3f newVec = new javax.vecmath.Vector3f();
        convert(oldVec,newVec);
        return newVec;
    }

    private void convert(com.jme3.math.Vector3f oldVec, javax.vecmath.Vector3f newVec)
    {
        newVec.x = oldVec.x;
        newVec.y = oldVec.y;
        newVec.z = oldVec.z;
    }
 
}
