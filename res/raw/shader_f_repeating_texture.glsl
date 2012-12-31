precision highp float;
	
uniform sampler2D uTexture;
uniform vec4 uBlendColor;
		    
varying vec2 vTexCoord;

// Top-left of texture in texcoord units 
uniform vec2 uTopLeft;

// Size of subTexture in texcoord units
uniform vec2 uFrameSize;

// number of times to repeat in each direction
uniform vec2 uRepeat;

void main() {
	
	// subtract the TopLeft
	vec2 baseTexCoord = (vTexCoord - uTopLeft);
	
	// Multiply by the rate that the repeating has to do.
	vec2 adjustedTexCoord = (baseTexCoord * uRepeat);
	
	// mod the value to the frame size
	vec2 moddedAdjustedTexCoord = mod(adjustedTexCoord, uFrameSize);
	
	// add the TopLeft
	vec2 newTexCoord = (moddedAdjustedTexCoord + uTopLeft); 
	
	vec4 texColor = texture2D(uTexture, newTexCoord);

	gl_FragColor = texColor * uBlendColor;
}