// `time` is game time in seconds, filled in for every shader

void main(void) {
  vec2 p = uv;
  p.x += sin(p.y * 30.0 + time * 4.0) * 0.01;
  color = texture(costume, p);
}
