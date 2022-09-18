#version 400

in vec2 textureCoords;

uniform sampler2D ourTexture;

out vec4 out_Color;

void main() {
//    float f = texture(ourTexture, textureCoords).x;
//    out_Color = vec4(f, f, f, 1.0f);
    out_Color = vec4(1.0f, 1.0f, 1.0f, texture(ourTexture, textureCoords).x);
}