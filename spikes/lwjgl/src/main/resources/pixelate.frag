// demos/shader/pixelate.frag in the 7.0 format: only the body, no
// #version, no Processing defines. costume, uv, color, resolution and time
// come from the header.
void main() {
  vec2 pixels = vec2(16.0, 16.0);
  vec2 p = uv - mod(uv, 1.0 / pixels);
  color = texture(costume, p);
}
