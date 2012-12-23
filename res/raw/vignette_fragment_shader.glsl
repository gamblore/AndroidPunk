precision mediump float;

uniform vec2 resolution = vec2(800.0, 600.0);

// User specified color of the blend.
uniform vec4 uBlendColor = vec4(1.0);

//RADIUS of our vignette, where 0.5 results in a circle fitting the screen
const float RADIUS = 0.50;

//softness of our vignette, between 0.0 and 1.0
const float SOFTNESS = 0.50;

void main( void ) {

	// Turn it into a [0, 1] point
	vec2 position = ( gl_FragCoord.xy / resolution.xy );

	// get position relative to center
	float len = length(position.xy - vec2(0.5));
	
	// get dampening
	float vignette = smoothstep(RADIUS, RADIUS-SOFTNESS, len);
	
	// apply to buffer
	//gl_FragColor = vec4(mix(gl_FragColor.rgb, gl_FragColor.rgb * vignette, .5), 1.0) * uBlendColor;
	gl_FragColor = vec4(vignette * uBlendColor.rgb, 1.0 - vignette);

}