package test1;

import com.jme3.app.DebugKeysAppState;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.BillboardControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.scene.shape.Sphere;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.shadow.SpotLightShadowFilter;
import com.jme3.shadow.SpotLightShadowRenderer;
import com.jme3.texture.Texture;
import com.jme3.util.TangentBinormalGenerator;
import com.landbeyond.engine.GuiGlobals;

import test1.control.LightingBoltControl;


public class ThunderBolt extends SimpleApplication implements ActionListener
{

    public ThunderBolt() {
        super(new StatsAppState(), new FlyCamAppState(), new DebugKeysAppState());
    }
    
    
    
    public static void main(String[] args) {
    	ThunderBolt app = new ThunderBolt();
        app.start();
    }

    protected SpotLight spotlight;
    protected DirectionalLight sun;
    
    // Renderers or Filters?
    protected boolean useRenderers = true;

    protected boolean moveTheSun = true;
	private AudioNode errornode;
	private AudioNode keynode;
	private Geometry lightingBoltGeom;
	private LightingBoltControl boltControl;
	private Node lightingBoltNode;
    
    @Override
    public void simpleInitApp() {
        // Create the sphere.
        Sphere sphereComp = new Sphere(32, 32, 1f);
        Spatial sphere = new Geometry("Sphere", sphereComp);
        TangentBinormalGenerator.generate(sphereComp);
        sphereComp.scaleTextureCoordinates(new Vector2f(4,4));

        Texture sphereTex = assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg");
        sphereTex.setWrap(Texture.WrapMode.Repeat);
        Texture sphereMap = assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png");
        sphereMap.setWrap(Texture.WrapMode.Repeat);

        Material sphereMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        sphereMat.setTexture("DiffuseMap", sphereTex);
        sphereMat.setTexture("NormalMap", sphereMap);
        sphereMat.setBoolean("UseMaterialColors",true);
        sphereMat.setColor("Diffuse",ColorRGBA.LightGray);
        sphereMat.setColor("Specular",ColorRGBA.Blue);
        sphereMat.setFloat("Shininess", 4f); // [0,128]

        sphere.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        sphere.setMaterial(sphereMat);
        sphere.setLocalTranslation(4f, 1.3f, 4f);
        rootNode.attachChild(sphere);

        // Create the floor.
        Box floorComp = new Box(10f, 0.1f, 10f);
        Spatial floor = new Geometry("Floor", floorComp);
        TangentBinormalGenerator.generate(floorComp);
        floorComp.scaleTextureCoordinates(new Vector2f(20,20));
        
        Texture floorTex = assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg");
        floorTex.setWrap(Texture.WrapMode.Repeat);
        Texture floorMap = assetManager.loadTexture("Textures/Terrain/Pond/Pond_normal.png");
        floorMap.setWrap(Texture.WrapMode.Repeat);

        Material floorMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMat.setTexture("DiffuseMap", floorTex);
        floorMat.setTexture("NormalMap", floorMap);
        floorMat.setBoolean("UseMaterialColors",true);
        floorMat.setColor("Diffuse",ColorRGBA.White);
        floorMat.setColor("Specular",ColorRGBA.White);
        floorMat.setFloat("Shininess", 1f);  // [0,128]
        
        floor.setShadowMode(RenderQueue.ShadowMode.Receive);
        floor.setMaterial(floorMat);
        floor.setLocalTranslation(0f, 0.2f, 0f);
        rootNode.attachChild(floor);


        
        lightingBoltNode = new Node();
        Quad quad = new Quad(1,2);
        
        lightingBoltGeom = new Geometry("", quad);
        Material geomMat = new Material(assetManager,  "shaders/bolt/lightingBolt.j3md");
        geomMat.setTexture("DiffuseMap", assetManager.loadTexture("textures/blank.png"));
        geomMat.setColor("Color", new ColorRGBA(0.0f, 1f, 1f, 1));
        geomMat.setFloat("intensity", 0.3f);
        lightingBoltGeom.setMaterial(geomMat);
        boltControl = new LightingBoltControl(assetManager, lightingBoltNode);
        lightingBoltGeom.addControl(boltControl);
        lightingBoltGeom.addControl(new BillboardControl());
        lightingBoltGeom.setCullHint(CullHint.Always);
        lightingBoltNode.attachChild(lightingBoltGeom);
        rootNode.attachChild(lightingBoltNode);
        
        
        
        // Create the sun.
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1,-1,-1).normalizeLocal());
        sun.setColor(ColorRGBA.White.mult(0.1f));
        rootNode.addLight(sun);

        // Create the spotlight.
        spotlight = new SpotLight(
            new Vector3f(4f, 10f, 8),
            new Vector3f(0f, 0f, -1f),
            100f,
            ColorRGBA.Green.mult(1.5f),
            2f * FastMath.DEG_TO_RAD,
            5f * FastMath.DEG_TO_RAD
        );
        rootNode.addLight(spotlight);

        if (useRenderers) {

            // Create the sun's shadowing.
            DirectionalLightShadowRenderer dlsr =
                new DirectionalLightShadowRenderer(assetManager, 1024, 3);
            dlsr.setLight(sun);
            viewPort.addProcessor(dlsr);

            // Create the spotlight's shadowing.
            SpotLightShadowRenderer slsr = new SpotLightShadowRenderer(assetManager, 1024);
            slsr.setLight(spotlight);
            slsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
            slsr.setShadowIntensity(1f);
            viewPort.addProcessor(slsr);
            
        } else { // use filters
            
            // Create the sun's shadowing.
            DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 3);
            dlsf.setLight(sun);
            dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
            dlsf.setShadowIntensity(0.6f);

            SpotLightShadowFilter slsf = new SpotLightShadowFilter(assetManager, 1024);
            slsf.setLight(spotlight);
            slsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
            slsf.setShadowIntensity(0.6f);

            FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
            fpp.addFilter(dlsf);
            fpp.addFilter(slsf);
            // fpp.setFlushShadowQueues(false); // deprecated and removed

            viewPort.addProcessor(fpp);
        }
        

        inputManager.addMapping("1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addMapping("2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addMapping("3", new KeyTrigger(KeyInput.KEY_3));
		inputManager.addListener(this, "1", "2", "3");

        cam.setLocation(new Vector3f(0.928f,6f,16f));
        cam.lookAtDirection(new Vector3f(19.97f, -44.999f, -190.04f), Vector3f.UNIT_Y);
    }

    protected float angle = 0;
    protected Vector3f baseDirection = new Vector3f(10f, -10f, 10f);
    
    @Override
    public void simpleUpdate(float tpf) {

    	
    	
        if (moveTheSun) {
            Matrix3f m = new Matrix3f();
            m.fromAngleAxis(angle, Vector3f.UNIT_Y);
            sun.setDirection(m.mult(baseDirection));
            angle += FastMath.PI/1024;
        }
    }

	@Override
	public void onAction(String name, boolean isPressed, float tpf)
	{
		if (isPressed)
		{
			if (name.equals("1"))
				boltControl.activateLightingBolt();
			else if (name.equals("2"))
				boltControl.activateLightingBoltAtRandomPlace();
			else if (name.equals("3"))
				boltControl.changeColor();
		}
	}
}