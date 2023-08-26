#version 330 core

layout(location = 0) in vec2 iposition;

uniform mat3 modelToWorld;
uniform mat3 worldToClip;

void main(void){
    gl_Position = vec4((worldToClip * modelToWorld * vec3(iposition.xy, 1.0)).xy, 0.0, 1.0);
}