package test.control;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.control.AbstractControl;
import com.landbeyond.utils.Utils;

public class LightingBoltControl extends AbstractControl
{
	public static enum STATE
	{
		IDLE,
		LIGHTING
	}

	STATE state;
	float timer = 1;
	
	public LightingBoltControl()
	{
		state = STATE.IDLE;
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
					state = STATE.LIGHTING;
					timer = 0.2f;
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

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp)
	{
		// TODO Auto-generated method stub
		
	}

	
	public int randn(int low,int high)
	 {
		if (low ==  high)
			return low;
		return (int) ((Math.random() * (high - low )) + low);
	}

}
