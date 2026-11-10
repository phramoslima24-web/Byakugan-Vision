#version 150

uniform sampler2D DiffuseSampler;
in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec4 source = texture(DiffuseSampler, texCoord);
    vec3 rgb = source.rgb;

    // Visão monocromática com contraste mais forte, preservando detalhes nas sombras.
    float luminance = dot(rgb, vec3(0.2126, 0.7152, 0.0722));
    luminance = clamp((luminance - 0.5) * 1.16 + 0.5, 0.0, 1.0);

    // Leve tom frio perolado, próximo à visão Byakugan, sem colorir a imagem.
    vec3 monochrome = vec3(luminance * 0.96, luminance * 0.985, luminance);
    float vignette = smoothstep(1.18, 0.28, distance(texCoord, vec2(0.5)));
    monochrome *= mix(0.88, 1.0, vignette);

    fragColor = vec4(monochrome, source.a);
}
