#version 330

in vec3 passNormal;
in vec3 passPos;
in vec2 passTex;

out vec4 outColor;

uniform sampler2D myTexture; // bound texture
// Light parameters
uniform vec3 lightPos = vec3(5.0, 5.0, 5.0);

void main() {
    vec3 normal = normalize(passNormal);
    vec3 toLight = normalize(lightPos - passPos);
    vec3 toView  = normalize(-passPos);  // camera at (0,0,0)

    // ambient
    vec3 ambient = vec3(0.2, 0.2, 0.2);

    // diffuse
    float diff = max(dot(normal, toLight), 0.0);
    vec3 diffuse = diff * vec3(1.0, 1.0, 1.0);

    // specular
    vec3 reflectDir = reflect(-toLight, normal);
    float spec = pow(max(dot(reflectDir, toView), 0.0), 32.0);
    vec3 specular = spec * vec3(1.0, 1.0, 1.0);

    vec3 lighting = ambient + diffuse + specular;

    vec3 texColor = texture(myTexture, passTex).rgb;
    outColor = vec4(texColor * lighting, 1.0);
}
