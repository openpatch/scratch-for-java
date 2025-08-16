#ifdef GL_ES
precision mediump float;
#endif

#define PROCESSING_TEXTURE_SHADER

varying vec4 vertTexCoord;
uniform sampler2D texture;

uniform float time;
uniform float rate;

float rand(vec2 co) {
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453) * 2.0 - 1.0;
}

float offset(float blocks, vec2 uv) {
    float shaderTime = time*rate;
    return rand(vec2(shaderTime, floor(uv.y * blocks)));
}
    
void main( void ) {
	vec2 uv = vertTexCoord.st;

    gl_FragColor = texture2D(texture, uv);
    gl_FragColor.r = texture2D(texture, uv + vec2(offset(64.0, uv) * 0.03, 0.0)).r;
    gl_FragColor.g = texture2D(texture, uv + vec2(offset(64.0, uv) * 0.03 * 0.1666666, 0.0)).g;
    gl_FragColor.b = texture2D(texture, uv + vec2(offset(64.0, uv) * 0.03, 0.0)).b;
}