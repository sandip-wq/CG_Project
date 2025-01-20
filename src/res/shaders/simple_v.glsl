#version 330

layout(location=0) in vec3 inPosition;
layout(location=3) in vec2 inTexCoord;

out vec2 passTex;

uniform mat4 mvMatrix;
uniform mat4 projectionMatrix;

void main() {
    gl_Position = projectionMatrix * mvMatrix * vec4(inPosition, 1.0);
    passTex = inTexCoord;
}
