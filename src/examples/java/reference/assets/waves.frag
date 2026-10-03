#ifdef GL_ES
precision mediump float;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertTexCoord;

// set by the program with shader.set("time", ...)
uniform float time;

void main(void) {
  vec2 p = vertTexCoord.st;
  p.x += sin(p.y * 30.0 + time * 4.0) * 0.01;
  gl_FragColor = texture2D(texture, p);
}
