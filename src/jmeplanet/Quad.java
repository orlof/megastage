/*
Copyright (c) 2012 Aaron Perkins

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package jmeplanet;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Geometry;
import com.jme3.material.Material;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.shader.VarType;
import com.jme3.terrain.heightmap.AbstractHeightMap;

/**
 * Quad
 * 
 * Credits
 * This code has been adapted from OgrePlanet
 * Copyright (c) 2010 Anders Lingfors
 * https://bitbucket.org/lingfors/ogreplanet/
 */
public class Quad {
    
    protected String name;
    protected Material material;
    protected Vector3f min;
    protected Vector3f max;
    protected float texXMin;
    protected float texXMax;
    protected float texYMin;
    protected float texYMax;
    protected float baseRadius;
    protected HeightDataSource dataSource;
    protected int quads;
    protected int depth;
    protected int minDepth;
    protected int maxDepth;
    protected Quad parentQuad;
    protected Node parentNode;
    protected int position;
    protected Node quadNode;
    protected Geometry quadGeometry;
    protected Vector3f quadCenter;
    protected Patch patch;
    protected BoundingBox aabb;
    protected AbstractHeightMap heightMap;
    protected Quad[] subQuad = new Quad[4];
    protected Quad[] neighborQuad = new Quad[4];
    enum Neighbor {
        Top,
        Right,
        Bottom,
        Left
    }
    
    public Quad(
            String name,
            Material material,
            Node parentNode,
            Vector3f min,
            Vector3f max,
            float texXMin,
            float texXMax,
            float texYMin,
            float texYMax,
            float baseRadius,
            HeightDataSource dataSource,
            int quads,
            int depth,
            int minDepth,
            int maxDepth,
            Quad parentQuad,
            int position) {
        
        this.name = name;
        this.material = material.clone();
        this.min = min;
        this.max = max;
        this.texXMin = texXMin;
        this.texXMax = texXMax;
        this.texYMin = texYMin;
        this.texYMax = texYMax;
        this.baseRadius = baseRadius;
        this.dataSource = dataSource;
        this.quads = quads;
        this.depth = depth;
        this.minDepth = minDepth;
        this.maxDepth = maxDepth;
        this.parentQuad = parentQuad;
        this.position = position;
        this.parentNode = parentNode;
        
        this.aabb = new BoundingBox();
        this.quadCenter = new Vector3f();
    }
    
    public void setCameraPosition(Vector3f position) {
        // Update camera position for subquads
        for (int i = 0; i < 4; i++) {
            if (this.subQuad[i] != null) {
                this.subQuad[i].setCameraPosition(position);
            }
        }
        
        float distanceToEdge = this.aabb.distanceToEdge(position);
        float aabbLength = this.aabb.getExtent(null).length();
        
        if ((this.quadGeometry != null || 
                (this.subQuad[0] != null && 
                this.subQuad[1] != null && 
                this.subQuad[2] != null && 
                this.subQuad[3] != null)) &&
                (this.depth < this.minDepth || (this.depth < this.maxDepth && distanceToEdge < aabbLength)))
        {
            
            if ((this.subQuad[0] != null &&
                    this.subQuad[1] != null &&
                    this.subQuad[2] != null &&
                    this.subQuad[3] != null)) 
            {
                hide();              
            } else {
                prepareSubQuads();
            }  
            
        } else {
            
            if ((this.subQuad[0] == null || this.subQuad[0].isLeaf()) &&
                    (this.subQuad[1] == null || this.subQuad[1].isLeaf() ) &&
                    (this.subQuad[2] == null || this.subQuad[2].isLeaf() ) &&
                    (this.subQuad[3] == null || this.subQuad[3].isLeaf() ))
            {
                if (!isPrepared())
                    preparePatch();
                
                if (this.quadGeometry == null) {
                    show();
                }
                
                for (int i = 0; i < 4; i++) {
                    if (this.subQuad[i] != null) {
                        this.subQuad[i].hide();
                        this.subQuad[i] = null;
                    }
                } 
            }
            
        }   
    }

    public void show() { 
        if (this.quadGeometry == null) {
            this.quadGeometry = new Geometry(this.name + "Geometry", patch.getMesh());
            
            // Set custom material parameters, if present
            if (this.material.getMaterialDef().getMaterialParam("PatchCenter") != null)
                this.material.setVector3("PatchCenter", this.quadCenter);
            if (this.material.getMaterialDef().getMaterialParam("PlanetRadius") != null)
                this.material.setFloat("PlanetRadius", this.baseRadius);
            
            this.quadGeometry.setMaterial(this.material);
        }
        
        if (this.quadNode == null) {
            this.quadNode = new Node(this.name);
            this.parentNode.attachChild(this.quadNode);
            this.quadNode.setLocalTranslation(this.quadCenter);
        }
        
        if (this.quadGeometry.getParent() == null) {
           this.quadNode.attachChild(this.quadGeometry);
           this.aabb = (BoundingBox)this.quadNode.getWorldBound();
        }     
    }
    
    public void hide() {
        if (this.patch != null)
            this.patch = null;
        
        if (this.quadGeometry != null) {
            this.quadGeometry.removeFromParent();
            this.quadGeometry = null;
        }
        if (this.quadNode != null) {
            this.quadNode.removeFromParent();
            this.quadNode = null;
        }
    }
    
    public boolean isPrepared() {
        if (this.patch == null)
            return false;
        return patch.isPrepared();
    }
    
    public boolean isLeaf() {
        return (this.subQuad[0] == null && this.subQuad[1] == null && this.subQuad[2] == null && this.subQuad[3] == null);
    }
    
    public void setWireframe(boolean value) {
        this.material.getAdditionalRenderState().setWireframe(value);
        
        for (int i = 0; i < 4; i++) {
            if (this.subQuad[i] != null) {
                this.subQuad[i].setWireframe(value);
            }
        } 
    }
    
    public void setVisiblity(boolean value) {
        if (value)
            this.material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Back);
        else
            this.material.getAdditionalRenderState().setFaceCullMode(FaceCullMode.FrontAndBack);
        for (int i = 0; i < 4; i++) {
            if (this.subQuad[i] != null) {
                this.subQuad[i].setVisiblity(value);
            }
        } 
    }
    
    public void setMaterialParam(String name, VarType type, String value) {
        switch (type){
            case Boolean:
                this.material.setBoolean(name, Boolean.parseBoolean(value));
                break;
        }
        
        for (int i = 0; i < 4; i++) {
            if (this.subQuad[i] != null) {
                this.subQuad[i].setMaterialParam(name, type, value);
            }
        } 
    }

    public void setSkirting(boolean skirting) {
        if (this.patch != null)
            this.patch.setSkirting(skirting);
        
        for (int i = 0; i < 4; i++) {
            if (this.subQuad[i] != null)
                this.subQuad[i].setSkirting(skirting);
        } 
    }
      
    public int getDepth() {
        return this.depth;
    }
    
    public int getCurrentMaxDepth() {
        int cDepth = this.depth;
        for (int i = 0; i < 4; i++) {
            if (this.subQuad[i] != null) {
                cDepth = Math.max(cDepth, this.subQuad[i].getCurrentMaxDepth());
            }
        }
        return cDepth;
    }

    protected void preparePatch() {                
        this.patch = new Patch(
                this.quads,
                this.min,
                this.max,
                this.texXMin,
                this.texXMax,
                this.texYMin,
                this.texYMax,
                this.baseRadius,
                this.dataSource,
                this.position,
                false);
        
        this.patch.prepare();
        this.quadCenter = this.patch.getCenter();
        this.aabb = this.patch.getAABB();
    }
    
    protected void prepareSubQuads() {
        Vector3f center = new Vector3f(
                this.min.x + (this.max.x - this.min.x)/2,
                this.min.y + (this.max.y - this.min.y)/2,
                this.min.z + (this.max.z - this.min.z)/2);

        Vector3f topCenter = new Vector3f();
        Vector3f bottomCenter = new Vector3f();
        Vector3f leftCenter = new Vector3f();
        Vector3f rightCenter = new Vector3f();

        if (this.min.x == this.max.x)
        {
            // This quad is perpendicular to the x axis
            // (right/left patches)
            topCenter = new Vector3f(this.min.x, this.min.y, center.z);
            bottomCenter = new Vector3f(this.max.x, this.max.y, center.z);
            leftCenter = new Vector3f(this.min.x, center.y, this.min.z);
            rightCenter = new Vector3f(this.max.x, center.y, this.max.z);
        }
        else if (this.min.y == this.max.y)
        {
            // This quad is perpendicular to the y axis
            // (top/bottom patches)
            topCenter = new Vector3f(center.x, this.min.y, this.min.z);
            bottomCenter = new Vector3f(center.x, this.max.y, this.max.z);
            leftCenter = new Vector3f(this.min.x, this.min.y, center.z);
            rightCenter = new Vector3f(this.max.x, this.max.y, center.z);
        }
        else if (this.min.z == this.max.z)
        {
            // This quad is perpendicular to the z axis
            // (front/back patches)
            topCenter = new Vector3f(center.x, this.min.y, this.min.z);
            bottomCenter = new Vector3f(center.x, this.max.y, this.max.z);
            leftCenter = new Vector3f(this.min.x, center.y, this.min.z);
            rightCenter = new Vector3f(this.max.x, center.y, this.max.z);
        }
        
        if (this.subQuad[0] == null)
        {
            // "Upper left" quad
            this.subQuad[0] = new Quad(
                    this.name + "0",
                    this.material,
                    this.parentNode,
                    this.min,
                    center,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texXMin,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texXMin + (this.texXMax - this.texXMin) / 2.0f,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texYMin,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texYMin + (this.texYMax - this.texYMin) / 2.0f,
                    this.baseRadius,
                    this.dataSource,
                    this.quads,
                    this.depth+1,
                    this.minDepth,
                    this.maxDepth,
                    this,
                    0);
        }

        if (this.subQuad[1] == null)
        {
            // "Upper right" quad
            this.subQuad[1] = new Quad(
                    this.name + "1",
                    this.material,
                    this.parentNode,
                    topCenter,
                    rightCenter,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texXMin + (this.texXMax - this.texXMin) / 2.0f,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texXMax,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texYMin,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texYMin + (this.texYMax - this.texYMin) / 2.0f,
                    this.baseRadius,
                    this.dataSource,
                    this.quads,
                    this.depth+1,
                    this.minDepth,
                    this.maxDepth,
                    this,
                    1);
        }

        if (this.subQuad[2] == null)
        {
            // "Lower left" quad
            this.subQuad[2] = new Quad(
                    this.name + "2",
                    this.material,
                    this.parentNode,
                    leftCenter,
                    bottomCenter,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texXMin,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texXMin + (this.texXMax - this.texXMin) / 2.0f,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texYMin + (this.texYMax - this.texYMin) / 2.0f,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texYMax,
                    this.baseRadius,
                    this.dataSource,
                    this.quads,
                    this.depth+1,
                    this.minDepth,
                    this.maxDepth,
                    this,
                    2);
        }

        if (this.subQuad[3] == null)
        {
            // "Lower right" quad
            this.subQuad[3] = new Quad(
                    this.name + "3",
                    this.material,
                    this.parentNode,
                    center,
                    this.max,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texXMin + (this.texXMax - this.texXMin) / 2.0f,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texXMax,
                    (this.depth < this.maxDepth - 9) ? 0f : this.texYMin + (this.texYMax - this.texYMin) / 2.0f,
                    (this.depth < this.maxDepth - 9) ? FastMath.pow(2.0f, this.maxDepth - this.depth - 1.0f) : this.texYMax,
                    this.baseRadius,
                    this.dataSource,
                    this.quads,
                    this.depth+1,
                    this.minDepth,
                    this.maxDepth,
                    this,
                    3);
        }            
    }

}
