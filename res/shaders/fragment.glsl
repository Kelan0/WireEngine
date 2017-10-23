#version 430

in vec3 p_vertexPosition;
in vec3 p_vertexNormal;
in vec2 p_vertexTexture;
in vec4 p_vertexColour;

uniform bool useTexture;
uniform sampler2D diffuseTexture;
uniform vec3 textureScale;

out vec4 outColour;

void main(void)
{
	vec4 colour = p_vertexColour;
	
	if (useTexture)
	{
		colour *= texture2D(diffuseTexture, p_vertexTexture / textureScale.xy);
	}
	
	outColour = colour;
}