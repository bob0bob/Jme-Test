package test1.filter;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.system.AppSettings;

public class RainFilter extends Filter
{
	public Vector2f screenResolution;
	public Vector2f resolution;

	protected float rainAmount = 0.7f;
	protected float rainDropAmount = 2.0f;
	protected float lighting = 1;
	protected float flashDarkness = 0.5f;
	private float lightingValue;
	
	public RainFilter( int width, int height)
	{
		resolution = new Vector2f(width, height);
	}
	
	@Override
	protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h)
	{
        material = new Material(manager, "shaders/rain/rain.j3md");
        screenResolution = new Vector2f(w, h);
        if (resolution.x == -1)
        	resolution = screenResolution.clone();
	}

	@Override
	protected Material getMaterial()
	{
        material.setVector2("resolution", resolution);
        material.setFloat("rainAmount",  rainAmount);
        material.setFloat("rainDropAmount",  rainDropAmount);
        material.setFloat("lighting", lighting);

        material.setFloat("flashDarkness", flashDarkness);
        material.setFloat("lightingValue", lightingValue);
        return material;
	}

	public float getRainAmount()
	{
		return rainAmount;
	}

	public float getRainDropAmount()
	{
		return rainDropAmount;
	}

	public float getLighting()
	{
		return lighting;
	}

	public float getFlashDarkness()
	{
		return flashDarkness;
	}

	public void setRainAmount(float rainAmount)
	{
		this.rainAmount = rainAmount;
	}

	public void setRainDropAmount(float rainDropAmount)
	{
		this.rainDropAmount = rainDropAmount;
	}

	public void setLighting(float lighting)
	{
		this.lighting = lighting;
	}

	public void setFlashDarkness(float flashDarkness)
	{
		this.flashDarkness = flashDarkness;
	}

	public void setLightingValue(float lightingValue)
	{
		this.lightingValue = lightingValue;
		
	}

}
