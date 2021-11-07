#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

attribute vec3 inPosition;

#if defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif


varying float distanceAdjustment;
uniform float m_zoneHasLight;			//float value of ambient level for zone while inside doors

// fog - jayfella
#ifdef USE_FOG
varying float fog_distance;
#endif
uniform vec3 g_CameraPosition;

out float visibility;
attribute vec2 inTexCoord;
attribute vec2 inTexCoord2;
attribute vec4 inColor;

varying vec2 texCoord1;
varying vec2 texCoord2;

varying vec4 vertColor;
#ifdef HAS_POINTSIZE
    uniform float m_PointSize;
#endif


struct Torch
{
	float strength;
	vec3 color;
	int torchActive;
};

Torch torch;


//Fog
struct Fog
{
    int activeFog;
    vec4 colour;
    float gradient;
    float density;
};


uniform Fog m_fog;
uniform vec3 m_worldTime;				//Holds x = hours, y = minutes
uniform int m_inDoors;				//Are we inside door


#ifdef SPRITE_SHEET
uniform int m_numCols;
uniform int m_numRows;
uniform vec2 m_texOffsetCoord;
#endif



#ifdef SPRITE_SHEET
vec2 getTextOffset(vec2 offsetCoords)
{
	float x = (texCoord1.x / m_numCols + offsetCoords.x);
	float y = (texCoord1.y / m_numRows + offsetCoords.y);

	return vec2(x,y);
}
#endif



float random (vec2 st) 
{
    return fract(sin(dot(st.xy, vec2(12.9898,78.233)))*  43758.5453123);
}


float getDepthFromCamera()
{
	float depth = 0;
    vec4 modelSpacePos = vec4(inPosition, 1.0);

	vec3 camera = vec3(g_CameraPosition.x, modelSpacePos.y, g_CameraPosition.z);
    depth = distance(camera, (g_WorldViewMatrix * modelSpacePos).xyz);
	if (depth > 40)
	{
		depth = int(depth / 20);
	} else {
		depth = 0;
	}
	
	
	return depth;
}




float getAmbientLighting()
{
	float ambient;
	float adjustment = 1;
	float depth = getDepthFromCamera();

	if (m_inDoors == 1)
	{
		adjustment = 0.10 - (0.08 * depth);

		if (torch.torchActive > 0) 
		{
			adjustment = max(adjustment, m_zoneHasLight);
			adjustment = clamp(adjustment, 0.01, 0.75);
		} else
			adjustment = m_zoneHasLight;
		ambient = adjustment;
	}  	else {
		if (m_worldTime.x >= 16 || m_worldTime.x < 10)
		{
			if (m_worldTime.x >= 16)
			{
				float 	time = ((m_worldTime.x - 16) * 60) + m_worldTime.y;
				adjustment = float(1 - (time / 480));
				adjustment = max(adjustment, 0.05);
				
			}
			else if (m_worldTime.x < 10) {
				float time = ((m_worldTime.x) * 60) + m_worldTime.y;
				adjustment = float(time / 600);
				adjustment = max(adjustment, 0.05);
			} 
		
			clamp(adjustment, 0.01, 0.75);
			
			adjustment -=   (0.08 * depth);
			ambient = clamp(adjustment, 0.01, 1);
		}
		else 
		{
			ambient = 0.75;
		}
   	}

	return ambient;
}



void main()
{

	distanceAdjustment = getAmbientLighting(); 
    #ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    #endif

    #ifdef SEPARATE_TEXCOORD
        texCoord2 = inTexCoord2;
    #endif

   #ifdef SPRITE_SHEET
    texCoord1 = getTextOffset(m_texOffsetCoord);
    texCoord2 = getTextOffset(m_texOffsetCoord);
   #endif


    #ifdef HAS_VERTEXCOLOR
        vertColor = inColor;
    #endif

    #ifdef HAS_POINTSIZE
        gl_PointSize = m_PointSize;
    #endif

    vec4 modelSpacePos = vec4(inPosition, 1.0);

    #ifdef NUM_MORPH_TARGETS
        Morph_Compute(modelSpacePos);
    #endif

    #ifdef NUM_BONES
        Skinning_Compute(modelSpacePos);
    #endif

    #ifdef USE_FOG
	    fog_distance = distance(g_CameraPosition, (g_WorldViewMatrix * modelSpacePos).xyz);
    #endif


    gl_Position = TransformWorldViewProjection(modelSpacePos);
}