#import "Common/ShaderLib/GLSLCompat.glsllib"
#import "Common/ShaderLib/MultiSample.glsllib"

uniform COLORTEXTURE m_Texture;
varying vec2 texCoord;

uniform float g_Time;
uniform vec4 m_Color;
uniform vec2 m_resolution;
uniform float m_rainAmount;
uniform float m_rainDropAmount;
uniform float m_lighting;
uniform float m_flashDarkness;
uniform float m_lightingValue;

#define S(a, b, t) smoothstep(a, b, t)


vec3 N13(float p) {
    //  from DAVE HOSKINS
   vec3 p3 = fract(vec3(p) * vec3(.1031,.11369,.13787));
   p3 += dot(p3, p3.yzx + 19.19);
   return fract(vec3((p3.x + p3.y)*p3.z, (p3.x+p3.z)*p3.y, (p3.y+p3.z)*p3.x));
}

vec4 N14(float t) {
	return fract(sin(t*vec4(123., 1024., 1456., 264.))*vec4(6547., 345., 8799., 1564.));
}
float N(float t) {
    return fract(sin(t*12345.564)*7658.76);
}

float Saw(float b, float t) {
	return S(0., b, t)*S(1., b, t);
}


vec2 DropLayer2(vec2 uv, float t) {
    vec2 UV = uv;
    
    uv.y += t*0.75;
    vec2 a = vec2(6., 1.);
    vec2 grid = a*2.;
    vec2 id = floor(uv*grid);
    
    float colShift = N(id.x); 
    uv.y += colShift;
    
    id = floor(uv*grid);
    vec3 n = N13(id.x*35.2+id.y*2376.1);
    vec2 st = fract(uv*grid)-vec2(.5, 0);
    
    float x = n.x-.5;
    
    float y = UV.y*20.;
    float wiggle = sin(y+sin(y));
    x += wiggle*(.5-abs(x))*(n.z-.5);
    x *= .7;
    float ti = fract(t+n.z);
    y = (Saw(.85, ti)-.5)*.9+.5;
    vec2 p = vec2(x, y);
    
    float d = length((st-p)*a.yx);
    
    float mainDrop = S(.4, .0, d);
    
    float r = sqrt(S(1., y, st.y));
    float cd = abs(st.x-x);
    float trail = S(.23*r, .15*r*r, cd);
    float trailFront = S(-.02, .02, st.y-y);
    trail *= trailFront*r*r;
    
    y = UV.y;
    float trail2 = S(.2*r, .0, cd);
    float droplets = max(0., (sin(y*(1.-y)*120.)-st.y))*trail2*trailFront*n.z;
    y = fract(y*10.)+(st.y-.5);
    float dd = length(st-vec2(x, y));
    droplets = S(.3, 0., dd);
    float m = mainDrop+droplets*r*trailFront;
    
    //m += st.x>a.y*.45 || st.y>a.x*.165 ? 1.2 : 0.;
    return vec2(m, trail);
}

float StaticDrops(vec2 uv, float t) {
	uv *= 40.;
    
    vec2 id = floor(uv);
    uv = fract(uv)-.5;
    vec3 n = N13(id.x*107.45+id.y*3543.654);
    vec2 p = (n.xy-.5)*.7;
    float d = length(uv-p);
    
    float fade = Saw(.025, fract(t+n.z));
    float c = S(.3, 0., d)*fract(n.z*10.)*fade;
    return c;
}

vec2 Drops(vec2 uv, float t, float l0, float l1, float l2) {
    float s = StaticDrops(uv, t)*l0; 
    vec2 m1 = DropLayer2(uv, t)*l1;
    vec2 m2 = DropLayer2(uv*1.85, t)*l2;
    
    float c = s+m1.x+m2.x;
    c = S(.3, 1., c);
    
    return vec2(c, max(m1.y*l0, m2.y*l1));
}

void main()
{
	vec2 uv = (texCoord.xy * m_resolution.xy) / m_resolution.y;
//	vec2 uv = texCoord.xy ; // m_resolution.xy;
	
	vec2 UV1 = texCoord.xy; // / m_resolution.xy;
	vec3 reso = vec3(m_resolution.xy, 1);
    vec3 M = 10.0 / reso;
    float T = g_Time + M.x * 200.1;
    
    
    float t = T*.2;
    
    float rainAmount =  sin(T*0.09)* .03 + m_rainAmount;
    
    float maxBlur = 0.0; //mix(3., 6., rainAmount);
    float minBlur = 0.;
    
    float heart = 0.;
    

    float staticDrops = S(-.5, 2.0, rainAmount) * m_rainDropAmount;
    float layer1 = S(.25, 0.95, rainAmount);
    float layer2 = S(.0, .5, rainAmount);
    
    
    vec2 c = Drops(uv, t, staticDrops, layer1, layer2);
   	vec2 e = vec2(0.001, 0.);
   	float cx = Drops(uv+e, t, staticDrops, layer1, layer2).x;
   	float cy = Drops(uv+e.yx, t, staticDrops, layer1, layer2).x;
   	vec2 n = vec2(cx-c.x, cy-c.x);		// expensive normals
    
    
    
    float focus = 0.0; //mix(maxBlur-c.y, minBlur, S(.1, .2, c.x));
    vec3 col = texture2D(m_Texture, UV1 +n).rgb; //, focus).rgb;
    
    
    t = ((m_lightingValue)) * m_lighting;						// make time sync with first lighting
//    t = ((T+1.)*.5) ;						// make time sync with first lighting
    float lightning = sin(t*sin(t*10.));				// lighting flicker
    lightning *= pow(max(0., sin(t+sin(t))), 10.);		// lightning flash
    col *= 1.+lightning*mix(1., .1, m_flashDarkness );	// composite lightning
    col *= 1.-dot(UV1 -= .5, UV1);							// vignette
    
    
    gl_FragColor = vec4(col, 1.);
    
//    gl_FragColor = texture2D(m_Texture, texCoord);
    
}