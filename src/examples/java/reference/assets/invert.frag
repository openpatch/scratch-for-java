#ifdef GL_ES
precision mediump float;
#endif

#define PROCESSING_TEXTURE_SHADER

uniform sampler2D texture;
varying vec4 vertTexCoord;

void main(void) {
  vec4 color = texture2D(texture, vertTexCoord.st);
  // color.a instead of 1.0: in the browser a colour comes multiplied by its
  // transparency, and 1.0 would turn the clear pixels around a costume white
  gl_FragColor = vec4(color.a - color.rgb, color.a);
}
