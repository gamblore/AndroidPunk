precision mediump float;

attribute vec2 Position;
attribute vec2 TexCoord;
		    		
varying vec2 vTexCoord;
		    
uniform mat4 uProjectionView;
uniform mat4 uModelView;
		    
void main() {

	vTexCoord = TexCoord;

	gl_Position = uProjectionView * uModelView * vec4(Position, 0.0, 1.0);
}