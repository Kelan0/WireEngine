#version 430

in vec3 p_vertexPosition;
in vec3 p_vertexNormal;
in vec2 p_vertexTexture;
in vec4 p_vertexColour;

uniform vec2 screenSize = vec2(1600, 900);
uniform sampler2D sampler;

out vec4 outColour;

void main(void)
{
	//float depth = pow(texture2D(sampler, p_vertexTexture).r , 256);
	//vec4 colour = vec4(depth, depth, depth, 1.0);
	vec4 colour = texture2D(sampler, p_vertexTexture);
	
	float crosshairWidth = 3.0;
	
	if (length(gl_FragCoord.xy - screenSize * 0.5) < crosshairWidth)
	{
		colour = vec4(vec3(1.0) - colour.rgb, 1.0);
	}
	
	outColour = colour;
}