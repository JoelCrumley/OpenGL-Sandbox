#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in int vertIndex;
layout(location = 2) in mat4 modelToWorld;
layout(location = 6) in vec4[8] iColour;

uniform mat4 worldToClip;

out vec4 colour;

void main(void){
    colour = iColour[vertIndex]; //gl_VertexID
	gl_Position = worldToClip * modelToWorld * vec4(position, 1.0);
}