precision mediump float;
	
uniform sampler2D uTexture;
uniform vec4 uBlendColor;
		    
varying vec4 vColor;
varying vec2 vTexCoord;
		    
void main() {
	vec4 texColor = texture2D(uTexture, vTexCoord);
	
	gl_FragColor = vColor * texColor * uBlendColor;
}