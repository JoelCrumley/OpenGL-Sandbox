#version 330 core

layout(location = 0) in vec2 iposition;
layout(location = 1) in vec2 itextureCoords;

uniform mat3 modelToWorld;
uniform mat3 worldToClip;

out vec2 textureCoords;

void main(void){
    gl_Position = vec4((worldToClip * modelToWorld * vec3(iposition.xy, 1.0)).xy, 0.0, 1.0);
    textureCoords = itextureCoords;
}