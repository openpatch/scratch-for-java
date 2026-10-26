uniform float blocks;

void main() {
  vec2 p = floor(uv * blocks) / blocks;
  color = texture(costume, p);
}
