#ifdef GL_ES
precision mediump float;
#endif

#define PROCESSING_TEXTURE_SHADER

varying vec4 vertTexCoord;
uniform sampler2D texture;

uniform vec3[100] lights;
uniform vec2 resolution;

float intensity = 100;
vec3 ambient_light = vec3(0.1, 0.1, 0.1);
vec3 color = vec3(0.499, 0.499, 0.999);

void main(void) {
    vec2 uv = vertTexCoord.xy;
    vec4 fragColor = texture2D(texture, uv);

    float diffuse = 0.0;
    float combined_intensity = 1.0;
    for(int i = 0; i < lights.length(); i++) {
        if(lights[i].z == 1) {
            vec2 light = lights[i].xy;
            light = (light + resolution * 0.5) / resolution;

            vec2 diff = light - uv;
            diff = diff * resolution;
            float d = length(diff);

            if(d <= intensity) {
                combined_intensity *= abs(d / intensity);
                diffuse = min(1, 1-combined_intensity);
            }
        }
    }
    gl_FragColor = vec4(min(fragColor.rgb * ((color * diffuse) + ambient_light), fragColor.rgb), fragColor.a);
}
