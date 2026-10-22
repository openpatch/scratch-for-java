uniform vec2 pixels;

void main(void)
{
  	vec2 p = uv;

	p.x -= mod(p.x, 1.0 / pixels.x);
	p.y -= mod(p.y, 1.0 / pixels.y);
    
	vec4 col = texture(costume, p).rgba;
	color = vec4(col);
}
