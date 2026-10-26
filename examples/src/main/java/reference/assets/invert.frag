void main(void) {
  vec4 color_ = texture(costume, uv);
  // color_.a instead of 1.0: in the browser a colour comes multiplied by its
  // transparency, and 1.0 would turn the clear pixels around a costume white
  color = vec4(color_.a - color_.rgb, color_.a);
}
