#ifdef GL_ES
precision mediump float;
#endif

#define PROCESSING_TEXTURE_SHADER

// The picture being drawn, and where on it this pixel is: (0, 0) is one
// corner, (1, 1) the opposite one.
uniform sampler2D texture;
varying vec4 vertTexCoord;

void main(void) {
  vec4 color = texture2D(texture, vertTexCoord.st);
  float grey = (color.r + color.g + color.b) / 3.0;
  gl_FragColor = vec4(grey, grey, grey, color.a);
}
