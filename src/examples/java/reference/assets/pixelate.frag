#ifdef GL_ES
precision mediump float;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertTexCoord;

// how many blocks across, set by the program with shader.set("blocks", ...)
uniform float blocks;

void main(void) {
  vec2 p = vertTexCoord.st;
  p = (floor(p * blocks) + 0.5) / blocks;
  gl_FragColor = texture2D(texture, p);
}
