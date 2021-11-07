package test1.control;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import com.landbeyond.engine.GuiGlobals;
import com.landbeyond.utils.Utils;

public class LightingBoltControl extends AbstractControl
{
	protected final float LENGTH_OF_STRIKE = 0.3f;
	protected final float LENGTH_BETWEEN_STRIKES = 0;
	
	public static enum STATE
	{
		IDLE,
		LIGHTING
	}

	STATE state;
	float timer = 1;
	private AudioNode lightAudio;
	private Node node;
	
	public LightingBoltControl(AssetManager assetManager, Node node)
	{
		state = STATE.IDLE;
    	lightAudio = new AudioNode(assetManager,"sound/lightningShort.ogg",AudioData.DataType.Buffer);
    	lightAudio.setDirectional(true);
		this.node = node;
		this.node.attachChild(lightAudio);
	}


	@Override
	protected void controlUpdate(float tpf)
	{
		if (!isEnabled())
		{
			spatial.setCullHint(CullHint.Always);
			return;
		}
		
		if (state == STATE.IDLE)
		{
			timer -= tpf;
			if (timer <= 0)
			{
				if (randn(0, 255) > 220)
				{
					node.setLocalTranslation(new Vector3f( 10 - randn(0, 20), 0, 10 - randn(0,20)));
					state = STATE.LIGHTING;
					timer = LENGTH_OF_STRIKE;
					playSound();
				}
				
			}
		}
		else {
			timer -= tpf;
			if (timer <= 0)
			{
				spatial.setCullHint(CullHint.Always);
				state = STATE.IDLE;
				timer = 3;
			} else {
				spatial.setCullHint(CullHint.Never);
			}
		}
	}

	private void playSound()
	{
		lightAudio.play();		
	}


	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
		// TODO Auto-generated method stub
		
	}

	public void activateLightingBolt()
	{
		activate();
	}

	
	public void activateLightingBolt(Vector3f position)
	{
		node.setLocalTranslation(position);
		activate();
	}
	
	protected int randn(int low,int high)
	 {
		if (low ==  high)
			return low;
		return (int) ((Math.random() * (high - low )) + low);
	}


	public void activateLightingBoltAtRandomPlace()
	{
		node.setLocalTranslation(new Vector3f( 10 - randn(0, 20), 0, 10 - randn(0,20)));
		activate();
	}

	protected void activate()
	{
		state = STATE.IDLE;
		timer = LENGTH_OF_STRIKE;
		playSound();
		
	}
}
