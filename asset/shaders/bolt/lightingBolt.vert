#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/Skinning.glsllib"
#import "Common/ShaderLib/Instancing.glsllib"
#import "Common/ShaderLib/MorphAnim.glsllib"

attribute vec3 inPosition;

#if defined(HAS_COLORMAP) || (defined(HAS_LIGHTMAP) && !defined(SEPARATE_TEXCOORD))
    #define NEED_TEXCOORD1
#endif



attribute vec2 inTexCoord;

varying vec2 texCoord1;


void main()
{

    #ifdef NEED_TEXCOORD1
        texCoord1 = inTexCoord;
    #endif



    vec4 modelSpacePos = vec4(inPosition, 1.0);

    gl_Position = TransformWorldViewProjection(modelSpacePos);
}