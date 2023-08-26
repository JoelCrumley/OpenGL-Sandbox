#version 400

in vec2 textureCoords;

uniform sampler2D ourTexture;
uniform vec4 textColor;

out vec4 out_Color;

void main() {
//    float f = texture(ourTexture, textureCoords).x;
//    out_Color = vec4(f, f, f, 1.0f);
    out_Color = vec4(textColor.rgb, texture(ourTexture, textureCoords).x * textColor.a);
}