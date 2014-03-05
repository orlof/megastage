/*     */ package com.cubes.test;
/*     */ 
/*     */ import com.cubes.BlockChunkControl;
/*     */ import com.cubes.BlockChunkListener;
/*     */ import com.cubes.BlockTerrainControl;
/*     */ import com.cubes.CubesSettings;
/*     */ import com.cubes.Vector3Int;
/*     */ import com.cubes.test.blocks.Block_Grass;
/*     */ import com.jme3.app.SimpleApplication;
/*     */ import com.jme3.app.state.AppStateManager;
/*     */ import com.jme3.bullet.BulletAppState;
/*     */ import com.jme3.bullet.PhysicsSpace;
/*     */ import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
/*     */ import com.jme3.bullet.collision.shapes.MeshCollisionShape;
/*     */ import com.jme3.bullet.control.CharacterControl;
/*     */ import com.jme3.bullet.control.RigidBodyControl;
/*     */ import com.jme3.input.InputManager;
/*     */ import com.jme3.input.controls.ActionListener;
/*     */ import com.jme3.input.controls.KeyTrigger;
/*     */ import com.jme3.input.controls.Trigger;
/*     */ import com.jme3.math.Vector3f;
/*     */ import com.jme3.renderer.Camera;
/*     */ import com.jme3.renderer.queue.RenderQueue.ShadowMode;
/*     */ import com.jme3.scene.Geometry;
/*     */ import com.jme3.scene.Node;
/*     */ import com.jme3.system.AppSettings;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class TestPhysics extends SimpleApplication
/*     */   implements ActionListener
/*     */ {
/*  37 */   private final Vector3Int terrainSize = new Vector3Int(100, 30, 100);
/*     */   private BulletAppState bulletAppState;
/*     */   private CharacterControl playerControl;
/*  40 */   private Vector3f walkDirection = new Vector3f();
/*  41 */   private boolean[] arrowKeys = new boolean[4];
/*     */   private CubesSettings cubesSettings;
/*     */   private BlockTerrainControl blockTerrain;
/*  44 */   private Node terrainNode = new Node();
/*     */ 
/*     */   public static void main(String[] args)
/*     */   {
/*  25 */     Logger.getLogger("").setLevel(Level.SEVERE);
/*  26 */     TestPhysics app = new TestPhysics();
/*  27 */     app.start();
/*     */   }
/*     */ 
/*     */   public TestPhysics() {
/*  31 */     this.settings = new AppSettings(true);
/*  32 */     this.settings.setWidth(1280);
/*  33 */     this.settings.setHeight(720);
/*  34 */     this.settings.setTitle("Cubes Demo - Physics");
/*  35 */     this.settings.setFrameRate(60);
/*     */   }
/*     */ 
/*     */   public void simpleInitApp()
/*     */   {
/*  48 */     this.bulletAppState = new BulletAppState();
/*  49 */     this.stateManager.attach(this.bulletAppState);
/*  50 */     initControls();
/*  51 */     initBlockTerrain();
/*  52 */     initPlayer();
/*  53 */     this.cam.lookAtDirection(new Vector3f(1.0F, 0.0F, 1.0F), Vector3f.UNIT_Y);
/*     */   }
/*     */ 
/*     */   private void initControls() {
/*  57 */     this.inputManager.addMapping("move_left", new Trigger[] { new KeyTrigger(30) });
/*  58 */     this.inputManager.addMapping("move_right", new Trigger[] { new KeyTrigger(32) });
/*  59 */     this.inputManager.addMapping("move_up", new Trigger[] { new KeyTrigger(17) });
/*  60 */     this.inputManager.addMapping("move_down", new Trigger[] { new KeyTrigger(31) });
/*  61 */     this.inputManager.addMapping("jump", new Trigger[] { new KeyTrigger(57) });
/*  62 */     this.inputManager.addListener(this, new String[] { "move_left" });
/*  63 */     this.inputManager.addListener(this, new String[] { "move_right" });
/*  64 */     this.inputManager.addListener(this, new String[] { "move_up" });
/*  65 */     this.inputManager.addListener(this, new String[] { "move_down" });
/*  66 */     this.inputManager.addListener(this, new String[] { "jump" });
/*     */   }
/*     */ 
/*     */   private void initBlockTerrain() {
/*  70 */     CubesTestAssets.registerBlocks();
/*  71 */     CubesTestAssets.initializeEnvironment(this);
/*     */ 
/*  73 */     this.cubesSettings = CubesTestAssets.getSettings(this);
/*  74 */     this.blockTerrain = new BlockTerrainControl(this.cubesSettings, new Vector3Int(7, 1, 7));
/*  75 */     this.blockTerrain.setBlocksFromNoise(new Vector3Int(), this.terrainSize, 0.8F, Block_Grass.class);
/*  76 */     this.blockTerrain.addChunkListener(new BlockChunkListener()
/*     */     {
/*     */       public void onSpatialUpdated(BlockChunkControl blockChunk)
/*     */       {
/*  80 */         Geometry optimizedGeometry = blockChunk.getOptimizedGeometry_Opaque();
/*  81 */         RigidBodyControl rigidBodyControl = (RigidBodyControl)optimizedGeometry.getControl(RigidBodyControl.class);
/*  82 */         if (rigidBodyControl == null) {
/*  83 */           rigidBodyControl = new RigidBodyControl(0.0F);
/*  84 */           optimizedGeometry.addControl(rigidBodyControl);
/*  85 */           TestPhysics.this.bulletAppState.getPhysicsSpace().add(rigidBodyControl);
/*     */         }
/*  87 */         rigidBodyControl.setCollisionShape(new MeshCollisionShape(optimizedGeometry.getMesh()));
/*     */       }
/*     */     });
/*  90 */     this.terrainNode.addControl(this.blockTerrain);
/*  91 */     this.terrainNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
/*  92 */     this.rootNode.attachChild(this.terrainNode);
/*     */   }
/*     */ 
/*     */   private void initPlayer() {
/*  96 */     this.playerControl = new CharacterControl(new CapsuleCollisionShape(this.cubesSettings.getBlockSize() / 2.0F, this.cubesSettings.getBlockSize() * 2.0F), 0.05F);
/*  97 */     this.playerControl.setJumpSpeed(25.0F);
/*  98 */     this.playerControl.setFallSpeed(20.0F);
/*  99 */     this.playerControl.setGravity(70.0F);
/* 100 */     this.playerControl.setPhysicsLocation(new Vector3f(5.0F, this.terrainSize.getY() + 5, 5.0F).mult(this.cubesSettings.getBlockSize()));
/* 101 */     this.bulletAppState.getPhysicsSpace().add(this.playerControl);
/*     */   }
/*     */ 
/*     */   public void simpleUpdate(float lastTimePerFrame)
/*     */   {
/* 106 */     float playerMoveSpeed = this.cubesSettings.getBlockSize() * 6.5F * lastTimePerFrame;
/* 107 */     Vector3f camDir = this.cam.getDirection().mult(playerMoveSpeed);
/* 108 */     Vector3f camLeft = this.cam.getLeft().mult(playerMoveSpeed);
/* 109 */     this.walkDirection.set(0.0F, 0.0F, 0.0F);
/* 110 */     if (this.arrowKeys[0] != 0) this.walkDirection.addLocal(camDir);
/* 111 */     if (this.arrowKeys[1] != 0) this.walkDirection.addLocal(camLeft.negate());
/* 112 */     if (this.arrowKeys[2] != 0) this.walkDirection.addLocal(camDir.negate());
/* 113 */     if (this.arrowKeys[3] != 0) this.walkDirection.addLocal(camLeft);
/* 114 */     this.walkDirection.setY(0.0F);
/* 115 */     this.playerControl.setWalkDirection(this.walkDirection);
/* 116 */     this.cam.setLocation(this.playerControl.getPhysicsLocation());
/*     */   }
/*     */ 
/*     */   public void onAction(String actionName, boolean value, float lastTimePerFrame)
/*     */   {
/* 121 */     if (actionName.equals("move_up")) {
/* 122 */       this.arrowKeys[0] = value;
/*     */     }
/* 124 */     else if (actionName.equals("move_right")) {
/* 125 */       this.arrowKeys[1] = value;
/*     */     }
/* 127 */     else if (actionName.equals("move_left")) {
/* 128 */       this.arrowKeys[3] = value;
/*     */     }
/* 130 */     else if (actionName.equals("move_down")) {
/* 131 */       this.arrowKeys[2] = value;
/*     */     }
/* 133 */     else if (actionName.equals("jump"))
/* 134 */       this.playerControl.jump();
/*     */   }
/*     */ }

/* Location:           /home/teppo/Dropbox/Computer/0x10c/megastage/lib/cubes/Cubes.jar
 * Qualified Name:     com.cubes.test.TestPhysics
 * JD-Core Version:    0.6.2
 */