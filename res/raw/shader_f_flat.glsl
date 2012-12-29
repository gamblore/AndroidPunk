precision mediump float;

uniform vec4 uBlendColor;
		    
varying vec4 vColor;
		    
void main() {
	gl_FragColor = vColor * uBlendColor;
}