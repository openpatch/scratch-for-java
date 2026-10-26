// how many blocks across, set by the program with shader.set("blocks", ...)
uniform float blocks;

void main(void) {
  vec2 p = uv;
  p = (floor(p * blocks) + 0.5) / blocks;
  color = texture(costume, p);
}
