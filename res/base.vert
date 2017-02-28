#version 330 core
layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in int style;
out vec3 vertCol;
uniform mat4 projMat;
uniform mat4 viewMat;
uniform mat4 modelMat;
uniform vec3 primColor;
uniform vec3 altColor;

void main() { 
	gl_Position = projMat * viewMat * modelMat * vec4(position.x, position.y, position.z, 1.0f);
	if(style%2 == 0) vertCol = primColor;
	else vertCol = altColor;
}
