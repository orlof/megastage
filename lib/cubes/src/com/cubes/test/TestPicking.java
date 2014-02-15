/*     */ package com.cubes.test;
/*     */ 
/*     */ import com.cubes.BlockNavigator;
/*     */ import com.cubes.BlockTerrainControl;
/*     */ import com.cubes.Vector3Int;
/*     */ import com.cubes.test.blocks.Block_Grass;
/*     */ import com.cubes.test.blocks.Block_Stone;
/*     */ import com.cubes.test.blocks.Block_Wood;
/*     */ import com.jme3.app.SimpleApplication;
/*     */ import com.jme3.collision.CollisionResult;
/*     */ import com.jme3.collision.CollisionResults;
/*     */ import com.jme3.font.BitmapCharacterSet;
/*     */ import com.jme3.font.BitmapFont;
/*     */ import com.jme3.font.BitmapText;
/*     */ import com.jme3.input.FlyByCamera;
/*     */ import com.jme3.input.InputManager;
/*     */ import com.jme3.input.controls.ActionListener;
/*     */ import com.jme3.input.controls.MouseButtonTrigger;
/*     */ import com.jme3.input.controls.Trigger;
/*     */ import com.jme3.math.Ray;
/*     */ import com.jme3.math.Vector2f;
/*     */ import com.jme3.math.Vector3f;
/*     */ import com.jme3.renderer.Camera;
/*     */ import com.jme3.scene.Node;
/*     */ import com.jme3.system.AppSettings;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class TestPicking extends SimpleApplication
/*     */   implements ActionListener
/*     */ {
/*     */   private Node terrainNode;
/*     */   private BlockTerrainControl blockTerrain;
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  22 */     Logger.getLogger("").setLevel(Level.SEVERE);
/*  23 */     TestPicking app = new TestPicking();
/*  24 */     app.start();
/*     */   }
/*     */ 
/*     */   public TestPicking() {
/*  28 */     this.settings = new AppSettings(true);
/*  29 */     this.settings.setWidth(1280);
/*  30 */     this.settings.setHeight(720);
/*  31 */     this.settings.setTitle("Cubes Demo - Picking");
/*     */   }
/*     */ 
/*     */   public void simpleInitApp()
/*     */   {
/*  38 */     CubesTestAssets.registerBlocks();
/*  39 */     initControls();
/*  40 */     initBlockTerrain();
/*  41 */     initGUI();
/*  42 */     this.cam.setLocation(new Vector3f(-16.6F, 46.0F, 97.599998F));
/*  43 */     this.cam.lookAtDirection(new Vector3f(0.68F, -0.47F, -0.56F), Vector3f.UNIT_Y);
/*  44 */     this.flyCam.setMoveSpeed(250.0F);
/*     */   }
/*     */ 
/*     */   private void initControls() {
/*  48 */     this.inputManager.addMapping("set_block", new Trigger[] { new MouseButtonTrigger(0) });
/*  49 */     this.inputManager.addListener(this, new String[] { "set_block" });
/*  50 */     this.inputManager.addMapping("remove_block", new Trigger[] { new MouseButtonTrigger(1) });
/*  51 */     this.inputManager.addListener(this, new String[] { "remove_block" });
/*     */   }
/*     */ 
/*     */   private void initBlockTerrain() {
/*  55 */     this.blockTerrain = new BlockTerrainControl(CubesTestAssets.getSettings(this), new Vector3Int(2, 1, 2));
/*  56 */     this.blockTerrain.setBlockArea(new Vector3Int(0, 0, 0), new Vector3Int(32, 1, 32), Block_Stone.class);
/*  57 */     this.blockTerrain.setBlocksFromNoise(new Vector3Int(0, 1, 0), new Vector3Int(32, 5, 32), 0.5F, Block_Grass.class);
/*  58 */     this.terrainNode = new Node();
/*  59 */     this.terrainNode.addControl(this.blockTerrain);
/*  60 */     this.rootNode.attachChild(this.terrainNode);
/*     */   }
/*     */ 
/*     */   private void initGUI()
/*     */   {
/*  65 */     BitmapText crosshair = new BitmapText(this.guiFont);
/*  66 */     crosshair.setText("+");
/*  67 */     crosshair.setSize(this.guiFont.getCharSet().getRenderedSize() * 2);
/*  68 */     crosshair.setLocalTranslation(this.settings.getWidth() / 2 - this.guiFont.getCharSet().getRenderedSize() / 3 * 2, this.settings.getHeight() / 2 + crosshair.getLineHeight() / 2.0F, 0.0F);
/*     */ 
/*  71 */     this.guiNode.attachChild(crosshair);
/*     */ 
/*  73 */     BitmapText instructionsText1 = new BitmapText(this.guiFont);
/*  74 */     instructionsText1.setText("Left Click: Set");
/*  75 */     instructionsText1.setLocalTranslation(0.0F, this.settings.getHeight(), 0.0F);
/*  76 */     this.guiNode.attachChild(instructionsText1);
/*  77 */     BitmapText instructionsText2 = new BitmapText(this.guiFont);
/*  78 */     instructionsText2.setText("Right Click: Remove");
/*  79 */     instructionsText2.setLocalTranslation(0.0F, this.settings.getHeight() - instructionsText2.getLineHeight(), 0.0F);
/*  80 */     this.guiNode.attachChild(instructionsText2);
/*  81 */     BitmapText instructionsText3 = new BitmapText(this.guiFont);
/*  82 */     instructionsText3.setText("(Bottom layer is marked as indestructible)");
/*  83 */     instructionsText3.setLocalTranslation(0.0F, this.settings.getHeight() - 2.0F * instructionsText3.getLineHeight(), 0.0F);
/*  84 */     this.guiNode.attachChild(instructionsText3);
/*     */   }
/*     */ 
/*     */   public void onAction(String action, boolean value, float lastTimePerFrame)
/*     */   {
/*  89 */     if ((action.equals("set_block")) && (value)) {
/*  90 */       Vector3Int blockLocation = getCurrentPointedBlockLocation(true);
/*  91 */       if (blockLocation != null) {
/*  92 */         this.blockTerrain.setBlock(blockLocation, Block_Wood.class);
/*     */       }
/*     */     }
/*  95 */     else if ((action.equals("remove_block")) && (value)) {
/*  96 */       Vector3Int blockLocation = getCurrentPointedBlockLocation(false);
/*  97 */       if ((blockLocation != null) && (blockLocation.getY() > 0))
/*  98 */         this.blockTerrain.removeBlock(blockLocation);
/*     */     }
/*     */   }
/*     */ 
/*     */   private Vector3Int getCurrentPointedBlockLocation(boolean getNeighborLocation)
/*     */   {
/* 104 */     CollisionResults results = getRayCastingResults(this.terrainNode);
/* 105 */     if (results.size() > 0) {
/* 106 */       Vector3f collisionContactPoint = results.getClosestCollision().getContactPoint();
/* 107 */       return BlockNavigator.getPointedBlockLocation(this.blockTerrain, collisionContactPoint, getNeighborLocation);
/*     */     }
/* 109 */     return null;
/*     */   }
/*     */ 
/*     */   private CollisionResults getRayCastingResults(Node node) {
/* 113 */     Vector3f origin = this.cam.getWorldCoordinates(new Vector2f(this.settings.getWidth() / 2, this.settings.getHeight() / 2), 0.0F);
/* 114 */     Vector3f direction = this.cam.getWorldCoordinates(new Vector2f(this.settings.getWidth() / 2, this.settings.getHeight() / 2), 0.3F);
/* 115 */     direction.subtractLocal(origin).normalizeLocal();
/* 116 */     Ray ray = new Ray(origin, direction);
/* 117 */     CollisionResults results = new CollisionResults();
/* 118 */     node.collideWith(ray, results);
/* 119 */     return results;
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestPicking
 * JD-Core Version:    0.6.2
 */