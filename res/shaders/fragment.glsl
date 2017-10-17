#version 430

in vec3 p_vertexPosition;
in vec3 p_vertexNormal;
in vec2 p_vertexTexture;

uniform sampler2D diffuseTexture;
uniform vec3 textureScale;

out vec4 outColour;

void main(void)
{
	outColour = texture2D(diffuseTexture, p_vertexTexture / textureScale.xy);
}