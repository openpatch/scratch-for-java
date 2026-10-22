// The picture being drawn, and where on it this pixel is: (0, 0) is one
// corner, (1, 1) the opposite one.

void main(void) {
  vec4 color_ = texture(costume, uv);
  float grey = (color_.r + color_.g + color_.b) / 3.0;
  color = vec4(grey, grey, grey, color_.a);
}
