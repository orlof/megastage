uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;

uniform vec4 m_Ambient;
uniform vec4 m_Diffuse;
uniform vec4 m_Specular;
uniform float m_Shininess;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;
varying vec3 LightVec;
varying vec3 Normal;
varying vec3 ViewDir;
varying vec4 LightDir;

attribute vec4 inPosition;
attribute vec3 inNormal;

// JME3 lights in world space
void lightComputeDir(in vec3 worldPos, in vec4 color, in vec4 position, out vec4 lightDir){
    float posLight = step(0.5, color.w);
    vec3 tempVec = position.xyz * sign(posLight - 0.5) - (worldPos * posLight);
    LightVec = tempVec;  

     float dist = length(tempVec);
     lightDir.w = clamp(1.0 - position.w * dist * posLight, 0.0, 1.0);
     lightDir.xyz = tempVec / vec3(dist);
}

void main(){
    gl_Position = g_WorldViewProjectionMatrix * inPosition;

    vec3 wvPosition = (g_WorldViewMatrix * inPosition).xyz;
    vec3 wvNormal  = normalize(g_NormalMatrix * inNormal);
    vec3 viewDir = normalize(-wvPosition);

    vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
    wvLightPos.w = g_LightPosition.w;
    vec4 lightColor = g_LightColor;
    Normal = wvNormal;
    ViewDir = viewDir;
    lightComputeDir(wvPosition, lightColor, wvLightPos, LightDir);
    lightColor.w = 1.0;

    AmbientSum  = (m_Ambient  * g_AmbientLightColor).rgb;
    DiffuseSum  =  m_Diffuse  * lightColor;
    SpecularSum = (m_Specular * lightColor).rgb;
}