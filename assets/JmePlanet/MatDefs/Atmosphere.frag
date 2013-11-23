uniform vec4 g_LightDirection;
uniform vec4 g_LightPosition;

uniform float m_Shininess;
uniform float m_PlanetRadius;
uniform float m_AtmosphereRadius;
uniform float m_AtmosphereDensity;

varying vec3 Normal;
varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;
varying vec3 ViewDir;
varying vec4 LightDir;
varying vec3 LightVec;

float tangDot(in vec3 v1, in vec3 v2){
    float d = dot(v1,v2);
    return d;
}

float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir){
        float NdotL = max(0.0, dot(norm, lightdir));
        float NdotV = max(0.0, dot(norm, viewdir));
        return NdotL * pow(max(NdotL * NdotV, 0.1), -1.0) * 0.5;
}

float lightComputeSpecular(in vec3 norm, in vec3 viewdir, in vec3 lightdir, in float shiny){
       // Standard Phong
       vec3 R = reflect(-lightdir, norm);
       return pow(max(tangDot(R, viewdir), 0.0), shiny);
}

vec2 computeLighting(in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir){
    float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);
    float specularFactor = lightComputeSpecular(wvNorm, wvViewDir, wvLightDir, m_Shininess);
    float att = clamp(1.0 - g_LightPosition.w * length(LightVec), 0.0, 1.0);
    specularFactor *= diffuseFactor;
    return vec2(diffuseFactor, specularFactor) * vec2(att);
}

void main() {
    vec4 lightDir = LightDir;
    lightDir.xyz = normalize(lightDir.xyz);
    vec3 viewDir = normalize(ViewDir);
    vec2 light = computeLighting(Normal, viewDir, lightDir.xyz);
    gl_FragColor.rgb =  AmbientSum  +
                        DiffuseSum.rgb  * vec3(light.x) +
                        SpecularSum.rgb * vec3(light.y);
    gl_FragColor.a = max(max(gl_FragColor.r, gl_FragColor.b), gl_FragColor.g);
}