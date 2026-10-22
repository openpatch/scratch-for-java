uniform float rate;

float rand(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453) * 2.0 - 1.0;
}

float offset(float blocks, vec2 uv) {
    float shaderTime = time*rate;
    return rand(vec2(shaderTime, floor(uv.y * blocks)));
}
    
void main( void ) {

    color = texture(costume, uv);
    color.r = texture(costume, uv + vec2(offset(64.0, uv) * 0.03, 0.0)).r;
    color.g = texture(costume, uv + vec2(offset(64.0, uv) * 0.03 * 0.1666666, 0.0)).g;
    color.b = texture(costume, uv + vec2(offset(64.0, uv) * 0.03, 0.0)).b;
}