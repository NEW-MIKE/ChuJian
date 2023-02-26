#version 300 es
precision mediump float;
uniform bool bDrawWindow;
uniform bool isNV12;
layout(location = 0) out vec4 outColor;
in vec2 TexCoordOut;
uniform sampler2D texture1;//rgb y
uniform sampler2D texture2;//uv
const mat3 convertMat = mat3(1.0, 1.0, 1.0,
0.0, -0.3455, 1.7790,
1.4075, -0.7169, 0.0);
void main() {
    if (bDrawWindow)
    outColor = vec4(1, 1, 1, 0);
    else {
        if (isNV12){
            float y, u, v;
            y = texture(texture1, TexCoordOut).r;
            u = texture(texture2, TexCoordOut).r-0.5;
            v = texture(texture2, TexCoordOut).a-0.5;
            vec3 rgb = convertMat * vec3(y, u, v);
            outColor = vec4(rgb, 1);
        } else {
            outColor = texture(texture1, TexCoordOut);
        }
    }
}