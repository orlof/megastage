package jmeplanet;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.util.Converter;
import com.jme3.math.Vector3f;

public class PlanetCollisionShape extends CollisionShape {

    protected Vector3f center;
    protected float radius;
    protected HeightDataSource dataSource;
    
    public PlanetCollisionShape(Vector3f center, float radius, HeightDataSource dataSource) {
        this.center = center;
        this.radius = radius;
        this.dataSource = dataSource;
        createShape();
    }
    
    private void createShape() {
        cShape = new PlanetShape(center, radius, dataSource);
        cShape.setLocalScaling(Converter.convert(getScale()));
        cShape.setMargin(margin);
    }
    
}
