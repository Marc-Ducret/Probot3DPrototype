#version 330 core

in vec3 vertCol;
out vec4 color; 
void main() {
	color = vec4(vertCol, 1.0f);
}