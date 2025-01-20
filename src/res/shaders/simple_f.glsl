#version 330

in vec2 passTex;
out vec4 outColor;

uniform sampler2D simpleTexture; // or rename to "myTexture"

void main() {
    outColor = texture(simpleTexture, passTex);
}
