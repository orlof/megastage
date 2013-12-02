uniform vec4 g_LightDirection;

uniform vec3 m_Region1;
uniform vec3 m_Region2;
uniform vec3 m_Region3;
uniform vec3 m_Region4;
uniform sampler2D m_Region1ColorMap;
uniform sampler2D m_Region2ColorMap;
uniform sampler2D m_Region3ColorMap;
uniform sampler2D m_Region4ColorMap;
uniform sampler2D m_SlopeColorMap;
uniform vec3 m_PatchCenter;
uniform float m_PlanetRadius;

varying vec3 wvNormal;
varying vec3 vNormal;
varying vec4 positionObjectSpace;
varying vec2 texCoord;
varying vec3 AmbientSum;
varying vec4 DiffuseSum;
varying vec3 vViewDir;
varying vec4 vLightDir;
varying vec3 lightVec;

float getWeight(float value, float vMin, float vMax) {
    float weight;
    float range = vMax - vMin;
    weight = (range - abs(value - vMax)) / range;
    weight = max(0.0, weight);

    return weight;
}

vec4 generateTerrainColor(float height) {
    
    vec4 region1Color = 0.25 * texture2D(m_Region1ColorMap, texCoord)
            + 0.25 * texture2D(m_Region1ColorMap, (1.0 / 8.0) * texCoord)
            + 0.25 * texture2D(m_Region1ColorMap, (1.0 / (8.0*8.0)) * texCoord)
            + 0.25 * texture2D(m_Region1ColorMap, (1.0 / (8.0*8.0*8.0)) * texCoord);
    vec4 region2Color = 0.5 * texture2D(m_Region2ColorMap, texCoord)
            + 0.25 * texture2D(m_Region2ColorMap, (1.0 / 8.0) * texCoord)
            + 0.25 * texture2D(m_Region2ColorMap, (1.0 / (8.0*8.0)) * texCoord)
            + 0.25 * texture2D(m_Region2ColorMap, (1.0 / (8.0*8.0*8.0)) * texCoord);
    vec4 region3Color = 1.0 * texture2D(m_Region3ColorMap, texCoord);
            + 0.25 * texture2D(m_Region3ColorMap, (1.0 / 8.0) * texCoord)
            + 0.25 * texture2D(m_Region3ColorMap, (1.0 / (8.0*8.0)) * texCoord)
            + 0.25 * texture2D(m_Region3ColorMap, (1.0 / (8.0*8.0*8.0)) * texCoord);
    vec4 region4Color = 1.0 * texture2D(m_Region4ColorMap, texCoord);
            + 0.25 * texture2D(m_Region4ColorMap, (1.0 / 8.0) * texCoord)
            + 0.25 * texture2D(m_Region4ColorMap, (1.0 / (8.0*8.0)) * texCoord)
            + 0.25 * texture2D(m_Region4ColorMap, (1.0 / (8.0*8.0*8.0)) * texCoord);
    vec4 slopeColor = 1.0 * texture2D(m_SlopeColorMap, texCoord);
            + 0.25 * texture2D(m_SlopeColorMap, (1.0 / 8.0) * texCoord)
            + 0.25 * texture2D(m_SlopeColorMap, (1.0 / (8.0*8.0)) * texCoord)
            + 0.25 * texture2D(m_SlopeColorMap, (1.0 / (8.0*8.0*8.0)) * texCoord);

    vec4 color;
    color = vec4(0.0,0.0,0.0,1.0);

    float slope = 1.0 - clamp(dot(normalize(vNormal), normalize(m_PatchCenter + positionObjectSpace.xyz)), 0.0, 1.0);

    float regionMin = 0.0;
    float regionMax = 0.0;
    float regionWeight = 0.0;

    // Terrain m_region 1.
    regionMin = m_Region1.x;
    regionMax = m_Region1.y;
    // Use region1's texture as the base
    if (height <= regionMax)
        color = region1Color;
    regionWeight = getWeight(height, regionMin, regionMax);
    color = mix(color, region1Color, regionWeight);

    // Terrain m_region 2.
    regionMin = m_Region2.x;
    regionMax = m_Region2.y;
    regionWeight = getWeight(height, regionMin, regionMax);
    color = mix(color, region2Color, regionWeight);

    // Terrain m_region 3.
    regionMin = m_Region3.x;
    regionMax = m_Region3.y;
    regionWeight = getWeight(height, regionMin, regionMax);
    color = mix(color, region3Color, regionWeight);

    // Terrain m_region 4.
    regionMin = m_Region4.x;
    regionMax = m_Region4.y;
    regionWeight = getWeight(height, regionMin, regionMax);
    color = mix(color, region4Color, regionWeight);

    color = mix(color, slopeColor, slope);

    return (color);
}

float lightComputeDiffuse(in vec3 norm, in vec3 lightdir, in vec3 viewdir){
    return max(0.0, dot(norm, lightdir));
}

vec2 computeLighting(in vec3 wvNorm, in vec3 wvViewDir, in vec3 wvLightDir){
    float diffuseFactor = lightComputeDiffuse(wvNorm, wvLightDir, wvViewDir);
    float att = vLightDir.w;
    return vec2(diffuseFactor) * vec2(att);
}

void main() {
    // Compute height of position from surface of planet
    float height = length(m_PatchCenter + positionObjectSpace.xyz) - m_PlanetRadius;

    vec4 color = generateTerrainColor(height);

    vec4 lightDir = vLightDir;
    lightDir.xyz = normalize(lightDir.xyz);
    vec3 viewDir = normalize(vViewDir);
    vec2 light = computeLighting(wvNormal, viewDir, lightDir.xyz);

    gl_FragColor.rgb =  AmbientSum * color.rgb + DiffuseSum.rgb * color.rgb * vec3(light.x);
}
