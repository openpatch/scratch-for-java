uniform vec3[100] lights;

float intensity = 100.0;
vec3 ambient_light = vec3(0.1, 0.1, 0.1);
vec3 color_ = vec3(0.499, 0.499, 0.999);

void main(void) {
    vec4 fragColor = texture(costume, uv);

    float diffuse = 0.0;
    float combined_intensity = 1.0;
    for(int i = 0; i < lights.length(); i++) {
        if(lights[i].z == 1.0) {
            vec2 light = lights[i].xy;
            light = (light + resolution * 0.5) / resolution;

            vec2 diff = light - uv;
            diff = diff * resolution;
            float d = length(diff);

            if(d <= intensity) {
                combined_intensity *= abs(d / intensity);
                diffuse = min(1.0, 1.0 - combined_intensity);
            }
        }
    }
    color = vec4(min(fragColor.rgb * ((color_ * diffuse) + ambient_light), fragColor.rgb), fragColor.a);
}
