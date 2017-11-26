#version 430

in vec3 vertexPosition;
in vec2 vertexTexture;
in vec2 charScreenPos; // Position of the character on the screen.
in vec2 charTexturePos; // Position of the character on the font atlas.
in vec2 charTextureSize; // Size of the character on the font atlas.

uniform vec2 fontTextureSize; // Size of the font atlas.

out vec2 p_vertexTexture;

void main(void)
{
    p_vertexTexture = (charTexturePos.xy + charTextureSize.xy * vec2(vertexTexture.xy)) / fontTextureSize.xy;
	
	gl_Position = vec4(vertexPosition.xy + charScreenPos.xy, 0.0, 1.0);
}