package org.megastage;
 
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.cubes.*;
import com.cubes.test.CubesTestAssets;
import com.cubes.test.blocks.*;
import com.jme3.scene.Node;
 
public class Test extends SimpleApplication{
 
    public static void main(String[] args){
        Test app = new Test();
        app.start();
    }
 
    public Test(){
        settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        settings.setTitle("Cubes Demo - Tutorial");
    }
 
    @Override
    public void simpleInitApp(){
        CubesTestAssets.registerBlocks();
 
        //This is your terrain, it contains the whole
        //block world and offers methods to modify it
        BlockTerrainControl blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(1, 1, 1));
 
        //To set a block, just specify the location and the block object
        //(Existing blocks will be replaced)
        blockTerrain.setBlock(new Vector3Int(0, 0, 0), Block_Wood.class); 
        blockTerrain.setBlock(new Vector3Int(0, 0, 1), Block_Wood.class);
        blockTerrain.setBlock(new Vector3Int(1, 0, 0), Block_Wood.class);
        blockTerrain.setBlock(new Vector3Int(1, 0, 1), Block_Stone.class);
        blockTerrain.setBlock(0, 0, 0, Block_Grass.class); //For the lazy users :P
 
        //The terrain is a jME-Control, you can add it
        //to a node of the scenegraph to display it
        Node terrainNode = new Node();
        terrainNode.addControl(blockTerrain);
        rootNode.attachChild(terrainNode);
 
        cam.setLocation(new Vector3f(-10, 10, 16));
        cam.lookAtDirection(new Vector3f(1, -0.56f, -1), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(50);
    }
}
