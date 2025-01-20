#version 330

layout(location = 0) in vec3 inPosition;
layout(location = 2) in vec3 inNormal;
layout(location = 3) in vec2 inTexCoord; // if you want texture coords in Phong

out vec3 passNormal;
out vec3 passPos;
out vec2 passTex;

uniform mat4 mvMatrix;         // model-view
uniform mat4 projectionMatrix;  // perspective

void main() {
    // transform vertex
    vec4 worldPos = mvMatrix * vec4(inPosition, 1.0);
    gl_Position = projectionMatrix * worldPos;

    passPos = worldPos.xyz;
    // normal transform: we want to apply the normal matrix
    // for simplicity, do approximate:
    passNormal = mat3(transpose(inverse(mvMatrix))) * inNormal;
    passTex    = inTexCoord;
}
