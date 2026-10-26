uniform float brt;
uniform int rad;

void main(void) {
  int i = 0;
  int j = 0;
  vec4 sum = vec4(0.0);
  
  for( i=-rad;i<rad;i++) {
    for( j=-rad;j<rad;j++) {
        sum += texture(costume, uv + vec2(j,i)*(1.0 / resolution))*brt;
    }
  }

  color = sum*sum+ vec4(texture(costume, uv).rgba);
}
