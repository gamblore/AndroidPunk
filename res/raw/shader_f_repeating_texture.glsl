precision highp float;
	
uniform sampler2D uTexture;
uniform vec4 uBlendColor;
		    
varying vec2 vTexCoord;

// Pixel of top left of texture
uniform vec2 uTopLeft;

// Size of subTexture
uniform vec2 uFrameSize;

// Size to tile in
uniform vec2 uRepeat;

void main() {
	
	// subtract the actualTopLeft
	vec2 baseTexCoord = (vTexCoord - uTopLeft);
	
	// Multiply by the ratio that the repeating texture has to do.
	vec2 adjustedTexCoord = (baseTexCoord * uRepeat);
	
	// mod the value
	vec2 moddedAdjustedTexCoord = mod(adjustedTexCoord, uFrameSize);
	
	// add the actualTopLeft
	vec2 newTexCoord = (moddedAdjustedTexCoord + uTopLeft); 
	
	vec4 texColor = texture2D(uTexture, newTexCoord);
	//texColor = texture2D(uTexture, baseTexCoord);
	//if (baseTexCoord.x < 0.85) {
	//	texColor = vec4(vec3(uTopLeft, 0.0), 1.0);
	//}
	gl_FragColor = texColor * uBlendColor;
}