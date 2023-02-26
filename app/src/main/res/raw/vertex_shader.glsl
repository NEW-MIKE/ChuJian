#version 300 es
layout(location = 0) in vec4 position;
layout(location = 1) in vec2 TexCoordIn;
out vec2 TexCoordOut;
void main() {
    gl_Position = position;
    TexCoordOut = TexCoordIn;
}