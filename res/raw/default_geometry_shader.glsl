precision mediump float;

attribute vec2 Position;
attribute vec4 Color;
attribute vec2 TexCoord;
		    		
varying vec4 vColor;
varying vec2 vTexCoord;
		    
uniform mat4 uProjectionView;
uniform mat4 uModelView;

//Shared between vertex and fragment shader
uniform mediump int uHasColorAttribute;
uniform mediump int uHasTextureAttribute;
		    
void main() {
	vColor = vec4(1.0);
	if (uHasColorAttribute != 0) {
		vColor = Color;
	}

	vTexCoord = vec2(0.0);
	if (uHasTextureAttribute != 0) {
		vTexCoord = TexCoord;
	}

	gl_Position = uProjectionView * uModelView * vec4(Position, 0.0, 1.0);
	//gl_Position = vec4(Position, 0.0, 1.0);
}