#import "Common/ShaderLib/GLSLCompat.glsllib"

varying vec2 texCoord1;
uniform float g_Time;
uniform vec4 m_Color;
uniform float m_intensity;


float hash(float x)
{
	return fract(21654.6512 * sin(385.51 * x));
}
float hash( in vec2 p ) 
{
    return fract(sin(p.x*15.32+p.y*35.78) * 43758.23);
}

vec2 hash2(vec2 p)
{
	return vec2(hash(p*.754),hash(1.5743*p.yx+4.5891))-.5;
}

vec2 hash2b(vec2 p)
{
	return vec2(hash(p*.754),hash(1.5743*p+4.5476351));
}
vec2 add = vec2(1.0, 0.0);

vec2 noise2(vec2 x)
{
    vec2 p = floor(x);
    vec2 f = fract(x);
    f = f*f*(3.0-2.0*f);
    
    vec2 res = mix(mix( hash2(p),          hash2(p + add.xy),f.x),
                    mix( hash2(p + add.yx), hash2(p + add.xx),f.x),f.y);
    return res;
}

vec2 fbm2(vec2 x)
{
    vec2 r = vec2(0.0);
    float a = 1.0;
    
    for (int i = 0; i < 8; i++)
    {
        r += abs(noise2(x)+.5 )* a;
        x *= 2.;
        a *= .5;
    }
     
    return r;
}

mat2 m2;

vec2 fbm3(vec2 x)
{
    vec2 r = vec2(0.0);
    float a = 1.;
    
    for (int i = 0; i < 6; i++)
    {
        r += m2*noise2((x+r)/a)*a; 
        r=-.8*abs(r);
        a*=1.7;
    }     
    return r;
}

vec3 storm(vec2 x)
{
    float t = .5* g_Time;
    float st = sin(t), ct = cos(t);
    m2 = mat2(ct,st,-st,ct);
    x=fbm3(x+0.5* g_Time)+2.;
    x*=.35;
        
    float c = length(x);
    c=c*c*c;
    vec3 col=vec3(0.6-.1*x.x,0.7,0.8-.1*x.y)*c*x.y;   
    return clamp(col,0.,1.);
}



float dseg( vec2 ba, vec2 pa )
{
	
	float h = clamp( dot(pa,ba)/dot(ba,ba), -0.2, 1. );	
	return length( pa - ba*h );
}

float arc(vec2 x,vec2 p, vec2 dir)
{
    vec2 r = p;
    float d=10.;
    for (int i = 0; i < 5; i++)
    {
        vec2 s= noise2(r+ g_Time)+dir;
        d=min(d,dseg(s,x-r));
        r +=s;      
    }
    return d*3.;
    
}



float thunderbolt(vec2 x,vec2 tgt)
{
    vec2 r = tgt;
    float d=1000.;
    float dist=length(tgt-x);
     
    for (int i = 0; i < 19; i++)
    {
        if(r.y>x.y+.5)break;
        vec2 s= (noise2(r+ g_Time)+vec2(0.,.7))*2.;
        dist = dseg(s,x-r);
        d=min(d,dist);
        
        r +=s;
        if(i-(i/5)*5==0){
            if(i-(i/10)*10==0)d=min(d,arc(x,r,vec2(.3,.5)));
            else d=min(d,arc(x,r,vec2(-.3,.5)));
        }
    }
    return exp(-5.*d)+.2*exp(-1.*dist);
   
}

void main( )
{
	
	
	vec2 p = 2.* texCoord1.xy / 1.0 - 1.0;
    vec2 d;
    vec2 tgt = vec2(1., -8.);
    float c=0.;
    vec3 col;
    
    
    float t = hash(floor(6. * g_Time));
    tgt+=8.*hash2b(tgt+t);
    if(hash(t+2.3)>.8)
	 {
		c= thunderbolt(p*10.+2.*fbm2(5.*p),tgt);	
		col+=clamp(1.7*vec3(0.8,.7,.9)*c,0.,1.);	
	}
    
	if ( col.r <= 0.1 || col.g <= 0.1 || col.b <= 0.1)
		discard;
	else
		gl_FragColor = vec4(mix(col, m_Color.rgb, m_intensity), 0.0);
}
