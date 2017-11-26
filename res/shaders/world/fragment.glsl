#version 430

const vec3 lightDirection = vec3(0.40824829046, 0.81649658092, 0.40824829046); // Normalized vector (1, 2, 1)
in vec3 p_vertexPosition;
in vec3 p_vertexNormal;
in vec2 p_vertexTexture;
in vec4 p_vertexColour;

uniform bool useTexture;
uniform bool useLight;
uniform sampler2D diffuseTexture;
uniform mat4 modelMatrix;
uniform vec3 textureScale = vec3(1.0);
uniform vec4 colourMultiplier = vec4(1.0);

out vec4 outColour;

void main(void)
{
	vec4 colour = p_vertexColour * colourMultiplier;
	
	if (useTexture)
	{
		colour *= texture2D(diffuseTexture, p_vertexTexture / textureScale.xy);
	}
	
	if (useLight)
	{
		vec3 normal = (modelMatrix * vec4(normalize(cross(dFdx(p_vertexPosition), dFdy(p_vertexPosition))), 0.0)).xyz;
		vec3 dirToLight = lightDirection;
		
		float diffuseCoefficient = clamp(dot(normal, dirToLight), 0.025, 1.0);
		colour.rgb *= diffuseCoefficient;
	}
	
	outColour = colour;
}