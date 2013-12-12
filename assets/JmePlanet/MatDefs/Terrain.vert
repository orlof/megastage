uniform mat4 g_WorldViewProjectionMatrix;
uniform mat4 g_WorldViewMatrix;
uniform mat3 g_NormalMatrix;
uniform mat4 g_ViewMatrix;

uniform vec4 g_LightColor;
uniform vec4 g_LightPosition;
uniform vec4 g_AmbientLightColor;

varying vec2 texCoord;

varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 SpecularSum;

attribute vec4 inPosition;
attribute vec2 inTexCoord;
attribute vec3 inNormal;

varying vec3 lightVec;
varying vec4 positionObjectSpace;
varying vec3 vNormal;
varying vec3 wvNormal;
varying vec3 vViewDir;
varying vec4 vLightDir;

// JME3 lights in world space
void lightComputeDir(in vec3 worldPos, in vec4 color, in vec4 position, out vec4 lightDir){
    float posLight = step(0.5, color.w);
    vec3 tempVec = position.xyz * sign(posLight - 0.5) - (worldPos * posLight);
    lightVec = tempVec;  
    lightDir = vec4(normalize(tempVec), 1.0);
}

void main(){
   
    positionObjectSpace = inPosition;
    gl_Position = g_WorldViewProjectionMatrix * inPosition;
    
    texCoord = inTexCoord;

    vec3 wvPosition = (g_WorldViewMatrix * inPosition).xyz;

    vNormal = inNormal;
    wvNormal  = normalize(g_NormalMatrix * inNormal);
    vec3 viewDir = normalize(-wvPosition);

    vec4 wvLightPos = (g_ViewMatrix * vec4(g_LightPosition.xyz,clamp(g_LightColor.w,0.0,1.0)));
    wvLightPos.w = g_LightPosition.w;
    vec4 lightColor = g_LightColor;

    vViewDir = viewDir;
    lightComputeDir(wvPosition, lightColor, wvLightPos, vLightDir);

    lightColor.w = 1.0;
    AmbientSum  = vec3(0.2, 0.2, 0.2) * g_AmbientLightColor.rgb; // Default: ambient color is dark gray
    DiffuseSum  = lightColor;
    SpecularSum = vec3(0.0);
}


