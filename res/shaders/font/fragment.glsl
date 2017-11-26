#version 430

in vec2 p_vertexTexture;

uniform vec4 colour;
uniform sampler2D sampler;

out vec4 outColour;

void main(void)
{
	outColour = vec4(2.0 / 3.0);//colour * texture2D(sampler, p_vertexTexture.xy);
}