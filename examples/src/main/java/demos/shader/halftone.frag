uniform int pixelsPerRow;

void main(void)
{
	vec2 p = uv;
	float pixelSize = 1.0 / float(max(pixelsPerRow, 1));
	
	float dx = mod(p.x, pixelSize) - pixelSize*0.5;
	float dy = mod(p.y, pixelSize) - pixelSize*0.5;
	
	p.x -= dx;
	p.y -= dy;
	vec4 col = texture(costume, p).rgba;
	float bright = 0.3333*(col.r+col.g+col.b);
	
	float dist = sqrt(dx*dx + dy*dy);
	float rad = bright * pixelSize * 0.8;
	float m = step(dist, rad);

	vec3 col2 = mix(vec3(0.0), vec3(1.0), m);
	color = vec4(col2, col.a);
}

