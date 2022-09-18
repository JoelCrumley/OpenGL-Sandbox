#version 330 core

layout(location = 0) in vec3 iposition;
layout(location = 1) in vec2 itextureCoords;

uniform mat4 modelToWorld;
uniform mat4 worldToClip;

out vec2 textureCoords;

void main(void){
    gl_Position = worldToClip * modelToWorld * vec4(iposition.xyz, 1.0);
    textureCoords = itextureCoords;
}