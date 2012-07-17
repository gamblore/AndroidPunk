package net.androidpunk.graphics.opengl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import net.androidpunk.FP;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.util.Log;

/**
 * A texture atlas.
 * @author ametcalf
 *
 */
public class Atlas extends Texture {

	private static final String TAG = "Atlas";
	
	private final Map<String, SubTexture> mSubTextures = new HashMap<String, SubTexture>();
	
	/**
	 * Loads a texture atlas in the Sparrow format.
	 * 
	 * Use TexturePacker with no texture cropping and power of two dimensions.
	 * 
	 * @param xmlPath The absolute path to the xml file in the assets directory.
	 */
	public Atlas(String xmlPath) {
		File xml = new File(xmlPath);
		String assetPath = xml.getParent();
		xml = null;
		
		Document doc = FP.getXML(xmlPath);
		
		NodeList tal = doc.getElementsByTagName("TextureAtlas");
		Node ta = tal.item(0);
		
		String texturePath = assetPath + File.separator + ta.getAttributes().getNamedItem("imagePath").getNodeValue();
		Log.d(TAG, "Loading " + texturePath);
		
		// This can start loading the texture in the OpenGL thread.
		setTextureBitmap(texturePath);
		
		NodeList stl = ta.getChildNodes();
		int subTextureCount = stl.getLength();
		for (int i = 0; i < subTextureCount; i++) {
			Node st = stl.item(i);
			if (st.getNodeType() == Node.TEXT_NODE) {
				continue;
			}
			NamedNodeMap atts = st.getAttributes();
			
			String name;
			int x, y, width, height;
			name = atts.getNamedItem("name").getNodeValue();
			
			x = Integer.parseInt(atts.getNamedItem("x").getNodeValue());
			y = Integer.parseInt(atts.getNamedItem("y").getNodeValue());
			width = Integer.parseInt(atts.getNamedItem("width").getNodeValue());
			height = Integer.parseInt(atts.getNamedItem("height").getNodeValue());
			
			mSubTextures.put(name, new SubTexture(this, x, y, width, height));
		}
	}
	
	public SubTexture getSubTexture(String name) {
		SubTexture st = mSubTextures.get(name);
		if (st == null) {
			Log.e(TAG, "Subtexture '" + name + "' does not exist.");
		}
		return st;
	}
	
}
