#version 430

in vec3 vertexPosition;
in vec3 vertexNormal;
in vec2 vertexTexture;

uniform mat4 modelMatrix;
uniform mat4 viewMatrix;
uniform mat4 projectionMatrix;

out vec3 p_vertexPosition;
out vec3 p_vertexNormal;
out vec2 p_vertexTexture;

void main(void)
{
	p_vertexPosition = vertexPosition;
    p_vertexNormal = vertexNormal;
    p_vertexTexture = vertexTexture;
	
	gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(vertexPosition, 1.0);
}