varying vec4 v_color;
varying vec2 v_texCoord0;

uniform sampler2D u_sampler2D;



//The resolution of the screen.
uniform vec2 u_resolution;

void main() {
	//Get the color of the current pixel.
	
	vec4 texColor = texture2D(u_sampler2D, v_texCoord0) * v_color;
	
	//get the distance 
	
	vec2 pos = vec2(u_resolution.x / 2.0, 0);
	float dist = u_resolution.y * 0.75;
	
	
	//invert the pixel color u_amount if u_amount is true
	if(distance(gl_FragCoord.xy, pos) > dist) {
		texColor.rgb = (1.0 - u_amount) + (1.0 - texColor.rgb);
	}
	
	//Add a vignette to the screen.(Note: This will not be affected by invert shader)
	vec2 relPos = gl_FragCoord.xy / u_resolution - 0.5;
	float len = length(relPos);
	float vignette = smoothstep(0.5, 0.1, len);
	texColor.rgb = mix(texColor.rgb, texColor.rgb * vignette, 1.0);
	
	
	//Send the final color to the screen.
	gl_FragColor = texColor;
}