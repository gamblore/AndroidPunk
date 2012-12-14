precision mediump float;
	
uniform sampler2D uTexture;
uniform vec4 uBlendColor;

//Shared between vertex and fragment shader
uniform mediump int uHasColorAttribute;
uniform mediump int uHasTextureAttribute;
		    
varying vec4 vColor;
varying vec2 vTexCoord;
		    
void main() {
	vec4 texColor = texture2D(uTexture, vTexCoord);
	if (uHasTextureAttribute == 0) {
		texColor = vec4(1.0);
	}
	

	gl_FragColor = vColor * texColor * uBlendColor;
	//gl_FragColor = vec4(1.0);
}