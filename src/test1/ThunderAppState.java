package test1;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.BaseAppState;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.post.FilterPostProcessor;
import com.jme3.scene.Node;

import test1.filter.RainFilter;

public class ThunderAppState extends BaseAppState
{
	private float drawLightingWhite;
	private boolean drawWhiteLight;
	private float drawLighting;
	private float timeBetweenFlashes;
	private AudioNode thunderAudio;
	private RainFilter rainFilter;
	private int width;
	private int height;

	private BitmapText lightingText;
	private BitmapText rainDropText;
	private BitmapText rainAmountText;
	private BitmapFont guiFont;
	private Node guiNode;

	public ThunderAppState(int width,  int height, BitmapFont font,Node parentNode)
	{
		this.width = width;
		this.height = height;
		this.guiFont = font;
		this.guiNode = parentNode;
	}
	
	@Override
	protected void initialize(Application app)
	{
		
        rainFilter = new RainFilter( width, height);
        FilterPostProcessor fpp = new FilterPostProcessor(app.getAssetManager());
        fpp.addFilter(rainFilter);
        app.getViewPort().addProcessor(fpp);

        thunderAudio = new AudioNode(app.getAssetManager(),"sound/lightningLong.ogg",AudioData.DataType.Buffer);
        thunderAudio.setPositional(false);
		drawLighting = 4;
		timeBetweenFlashes = 2;
		
        lightingText = guiFont.createLabel("Lighting : On");
        lightingText.setLocalTranslation(0, height - lightingText.getLineHeight() * 4, 0);
        guiNode.attachChild(lightingText);
        

        rainDropText = guiFont.createLabel("Rain Drop Amount : "+getRainFilter().getRainDropAmount());
        rainDropText.setLocalTranslation(0, height - rainDropText.getLineHeight() * 5, 0);
        guiNode.attachChild(rainDropText);
        
        rainAmountText = guiFont.createLabel("Rain Drop Amount : "+getRainFilter().getRainAmount());
        rainAmountText.setLocalTranslation(0, height - rainAmountText.getLineHeight() * 6, 0);
        guiNode.attachChild(rainAmountText);

    }

    
    @Override
    public void update(float tpf) 
    {
		if (drawLighting > 0)
		{
			drawLighting -= tpf;
			if (drawLightingWhite > 0)
				drawLightingWhite -= tpf;
			else {
				drawWhiteLight = !drawWhiteLight;
				drawLightingWhite = timeBetweenFlashes;
			}

			if (drawWhiteLight)
			{
				rainFilter.setLightingValue(((getApplication().getTimer().getTimeInSeconds() * 200) + 1) * 0.5f);
				rainFilter.setFlashDarkness(0.1f);
			} else {
				rainFilter.setLightingValue( ((getApplication().getTimer().getTimeInSeconds() * 200) + 1) * 0.15f);
				rainFilter.setFlashDarkness(.9f);
			}
		} else {
			rainFilter.setLighting(0);
			lightingText.setText("Lighting :"+(rainFilter.getLighting() == 0 ? "Off" : "On"));
		}


		lightingText.setText("Lighting :"+(getRainFilter().getLighting() == 0 ? "Off" : "On"));
		rainAmountText.setText("Rain Amount :"+getRainFilter().getRainAmount() );
		rainAmountText.setText("Rain Amount :"+getRainFilter().getRainAmount() );
		rainDropText.setText("Rain Amount :"+getRainFilter().getRainDropAmount() );
		rainDropText.setText("Rain Amount :"+getRainFilter().getRainDropAmount() );

    }




	@Override
	protected void cleanup(Application app)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onEnable()
	{
		rainFilter.setEnabled(true);
	}


	@Override
	protected void onDisable()
	{
		rainFilter.setEnabled(false);
	}

	public RainFilter getRainFilter()
	{
		return rainFilter;
	}

	public void activeThunder()
	{
		rainFilter.setLighting((rainFilter.getLighting() == 1 ? 0 : 1));
		
		if (rainFilter.getLighting() == 1)
		{
			drawLighting = 3;
			timeBetweenFlashes = 0.15f;
			drawWhiteLight = true;
			thunderAudio.play();
		}
		
	}

}
