package net.androidpunk.graphics.atlas;

import java.nio.CharBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.SubTexture;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

/**
 * A canvas to which Tiles can be drawn for fast multiple tile rendering.
 */
public class TileMap extends AtlasGraphic {

	private static final String TAG = "TileMap";
	/**
	 * If x/y positions should be used instead of columns/rows.
	 */
	public boolean usePositions = false;
	
	private FloatBuffer mVertexBuffer;
	private FloatBuffer mTextureBuffer;
	private CharBuffer mIndexBuffer;
	
	private int mIndexCount;
	private int mVerticesCount;
	private int mVerticiesAcross;
	private int mVerticiesDown;
	
	// Tilemap information.
	protected Bitmap mMap;
	private Bitmap mTemp;
	private int mWidth;
	private int mHeight;
	private int mColumns;
	private int mRows;

	// Tileset information.
	private SubTexture mSet;
	private int mSetColumns;
	private int mSetRows;
	private int mSetCount;
	private Rect mTile;

	// Global objects.
	private Rect mRect = FP.rect;
	private Canvas mCanvas = FP.canvas;
	
	/**
	 * Constructor.
	 * @param	tileset			The source tileset image.
	 * @param	width			Width of the tilemap, in pixels.
	 * @param	height			Height of the tilemap, in pixels.
	 * @param	tileWidth		Tile width.
	 * @param	tileHeight		Tile height.
	 */
	public TileMap(SubTexture tileset, int width, int height, int tileWidth, int tileHeight) {
		super(tileset);
		// set some tilemap information
		mWidth = width - (width % tileWidth);
		mHeight = height - (height % tileHeight);
		mColumns = mWidth / tileWidth;
		mRows = mHeight / tileHeight;
		mMap = Bitmap.createBitmap(mColumns, mRows, Config.ARGB_8888);
		//mTemp = mMap.copy(Config.ARGB_8888, true);
		mTile = new Rect(0, 0, tileWidth, tileHeight);
		
		/*
         * Initialize triangle list mesh.
         *
         *     [0]------[1]   [2]------[3] ...
         *      |    /   |     |    /   |
         *      |   /    |     |   /    |
         *      |  /     |     |  /     |
         *     [w]-----[w+1] [w+2]----[w+3]...
         *      |       |
         *
         */
		mVerticiesAcross = mColumns * 2;
		mVerticiesDown = mRows * 2;
		mVerticesCount = mVerticiesAcross * mVerticiesDown;
		mVertexBuffer = getDirectFloatBuffer(mVerticesCount * 2);
		mTextureBuffer = getDirectFloatBuffer(mVerticesCount * 2);
		
		Log.d(TAG, String.format("%d vertices", mVerticesCount));
		
		mIndexCount = mColumns * mRows * 6;
		mIndexBuffer = getDirectCharBuffer(mColumns * mRows * 6);
		
		setTileVerticesBuffer();
		setTileIndexBuffer();
		
		// load the tileset graphic
		mSet = tileset;
		if (mSet == null) {
			Log.e(TAG, "Invalid tileset graphic provided");
			return;
		}
		mSetColumns = (int)(mSet.getWidth() / tileWidth);
		mSetRows = (int)(mSet.getHeight() / tileHeight);
		mSetCount = mSetColumns * mSetRows;
	}
	
	private void setTileVerticesBuffer() {
		float f[] = new float[mVerticesCount * 2];
		int i = 0;
		mVertexBuffer.position(0);
		
		for(int y = 0; y < mRows; y++) {
			for(int x = 0; x < mColumns; x++) {
				//mVertexBuffer.put(x * mTile.width()).put(y * mTile.height());
				f[i++] = x * mTile.width();
				f[i++] = y * mTile.height();
				//index += 2;
				//mVertexBuffer.put((x+1) * mTile.width()).put(y * mTile.height());
				f[i++] = (x+1) * mTile.width();
				f[i++] = y * mTile.height();
				//index += 2;
				//Log.d(TAG, String.format("quad top %d,%d value %d,%d - %d,%d", x, y, 
				//		x * mTile.width(), y * mTile.height(), (x+1) * mTile.width(), y * mTile.height()));
			}
			for(int x = 0; x < mColumns; x++) {
				//mVertexBuffer.put(x * mTile.width()).put((y+1) * mTile.height());
				f[i++] = x * mTile.width();
				f[i++] = (y+1) * mTile.height();
				//index += 2;
				//mVertexBuffer.put((x+1) * mTile.width()).put((y+1) * mTile.height());
				f[i++] = (x+1) * mTile.width();
				f[i++] = (y+1) * mTile.height();
				//index += 2;
				//Log.d(TAG, String.format("quad bottom %d,%d value %d,%d - %d,%d", x, y,
				//		x * mTile.width(), (y+1) * mTile.height(), (x+1) * mTile.width(), (y+1) * mTile.height()));
			}
		}
		mVertexBuffer.put(f).position(0);
	}
	
	private void setTileTextureBuffer() {
		float f[] = new float[mVerticesCount * 2];
		int i = 0;
		Rect r = new Rect();
		int texWidth = mSubTexture.getTexture().getWidth();
		int texHeight = mSubTexture.getTexture().getHeight();
		
		mTextureBuffer.position(0);
		for(int y = 0; y < mRows; y++) {
			for(int x = 0; x < mColumns; x++) {
				int tile = getTile(x, y);
				mSubTexture.getFrame(r, tile, mTile.width(), mTile.height());
				//mTextureBuffer.put((float)r.left/texWidth).put((float)r.top/texHeight);
				f[i++] = (float)r.left/texWidth;
				f[i++] = (float)r.top/texHeight;
				//mTextureBuffer.put((float)(r.left + r.width())/texWidth).put((float)r.top/texHeight);
				f[i++] = (float)(r.left + r.width())/texWidth;
				f[i++] = (float)r.top/texHeight;
				//Log.d(TAG, String.format("texture quad top %d,%d value %d,%d - %d,%d", x, y, 
				//		r.left, r.top, r.left + r.width(), r.top));
			}
			for(int x = 0; x < mColumns; x++) {
				int tile = getTile(x, y);
				mSubTexture.getFrame(r, tile, mTile.width(), mTile.height());
				//mTextureBuffer.put((float)r.left/texWidth).put((float)(r.top + r.height())/ texHeight);
				f[i++] = (float)r.left/texWidth;
				f[i++] = (float)(r.top + r.height())/ texHeight;
				//mTextureBuffer.put((float)(r.left + r.width())/texWidth).put((float)(r.top + r.height())/ texHeight);
				f[i++] = (float)(r.left + r.width())/texWidth;
				f[i++] = (float)(r.top + r.height())/ texHeight;
				//Log.d(TAG, String.format("texture quad bottom %d,%d value %d,%d - %d,%d", x, y,
				//		r.left, r.top + r.height(), r.left + r.width(), r.top + r.height()));
			}
		}
		
		mTextureBuffer.put(f).position(0);
	}
	
	private void setTileIndexBuffer() {
		char indices[] = new char[mIndexCount];
		mIndexBuffer.position(0);
		int i = 0;
		for (int y = 0; y < mRows; y++) {
			final int indexY = y * 2;
			for (int x = 0; x < mColumns; x++) {
				final int indexX = x * 2;
				char a = (char) (indexY * mVerticiesAcross + indexX);
				char b = (char) (indexY * mVerticiesAcross + indexX + 1);
				char c = (char) ((indexY + 1) * mVerticiesAcross + indexX);
				char d = (char) ((indexY + 1) * mVerticiesAcross + indexX + 1);
				
				//Log.d(TAG, String.format("Quad %d, %d -> tl:%d tr:%d bl:%d br:%d", x, y, (int)a, (int)b, (int)c, (int)d));
				
				indices[i++] = a;
				indices[i++] = b;
				indices[i++] = c;
				//mIndexBuffer.put(a);
				//mIndexBuffer.put(b);
				//mIndexBuffer.put(c);
				
				indices[i++] = b;
				indices[i++] = d;
				indices[i++] = c;
				//mIndexBuffer.put(b);
				//mIndexBuffer.put(c);
				//mIndexBuffer.put(d);
			}
		}
		mIndexBuffer.put(indices).position(0);
	}
	
	@Override
	public void render(GL10 gl, Point point, Point camera) {
		super.render(gl, point, camera);
		if (!getAtlas().isLoaded()) {
			return;
		}
		
		mPoint.x = (int)(point.x + x - camera.x * scrollX);
		mPoint.y = (int)(point.y + y - camera.y * scrollY);
		
		gl.glPushMatrix(); 
		{
			setMatrix(gl);
			
			setBuffers(gl, mVertexBuffer, mTextureBuffer);
			gl.glDrawElements(GL10.GL_TRIANGLES, mIndexCount, GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
		}
		gl.glPopMatrix();

		/*
		for (int y = 0; y < mRows; y++) {
			gl.glPushMatrix(); 
			gl.glTranslatef(mPoint.x, mPoint.y + y * mTile.height(), 0);
			
			for (int x = 0; x < mColumns && mPoint.x + x * mTile.width() < FP.screen.getWidth(); x++) {
				int color = mMap.getPixel(x, y) & 0x00ffffff;
				if (color != -1) {
					
					setTextureBuffer(QUAD_FLOAT_BUFFER_2, mSet, color, mTile.width(), mTile.height());
				
					setBuffers(gl, mVertexBuffer, QUAD_FLOAT_BUFFER_2);
				
					gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
					gl.glTranslatef(mTile.width(), 0, 0);
					
				}
			}
			gl.glPopMatrix();
		}
		*/
	}


	public void setTile(int column, int row) {
		setTile(column, row, 0);
	}
	/**
	 * Sets the index of the tile at the position.
	 * @param	column		Tile column.
	 * @param	row			Tile row.
	 * @param	index		Tile index.
	 */
	public void setTile(int column, int row, int index) {
		if (usePositions) {
			column /= mTile.width();
			row /= mTile.height();
		}
		
		index %= mSetCount;
		column %= mColumns;
		row %= mRows;
		
		mMap.setPixel(column, row, 0xff << 24 | index);
		//Log.d(TAG, String.format("Setting %d %d to %d = %d", column, row, index, getTile(column, row)));
	}
	
	/**
	 * Clears the tile at the position.
	 * @param	column		Tile column.
	 * @param	row			Tile row.
	 */
	public void clearTile(int column, int row) {
		if (usePositions) {
			column /= mTile.width();
			row /= mTile.height();
		}
		column %= mColumns;
		row %= mRows;
		mMap.setPixel(column, row, -1);
	}
	
	/**
	 * Gets the tile index at the position.
	 * @param	column		Tile column.
	 * @param	row			Tile row.
	 * @return	The tile index.
	 */
	public int getTile(int column, int row) {
		if (usePositions) {
			column /= mTile.width();
			row /= mTile.height();
		}
		return mMap.getPixel(column % mColumns, row % mRows) & 0x00ffffff;
	}
	
	
	public void setRect(int column, int row) {
		setRect(column, row, 1, 1, 0);
	}
	
	public void setRect(int column, int row, int width, int height) {
		setRect(column, row, width, height, 0);
	}
	
	/**
	 * Sets a rectangular region of tiles to the index.
	 * @param	column		First tile column.
	 * @param	row			First tile row.
	 * @param	width		Width in tiles.
	 * @param	height		Height in tiles.
	 * @param	index		Tile index.
	 */
	public void setRect(int column, int row, int width, int height, int index) {
		if (usePositions)
		{
			column /= mTile.width();
			row /= mTile.height();
			width /= mTile.width();
			height /= mTile.height();
		}
		column %= mColumns;
		row %= mRows;
		int c = column;
		int r = column + width;
		int b = row + height;
		boolean u = usePositions;
		usePositions = false;
		while (row < b) {
			while (column < r) {
				setTile(column, row, index);
				column ++;
			}
			column = c;
			row ++;
		}
		usePositions = u;
	}
	
	
	/**
	 * Clears the rectangular region of tiles.
	 * @param	column		First tile column.
	 * @param	row			First tile row.
	 */
	public void clearRect(int column, int row) {
		clearRect(column, row, 1, 1);
	}
	
	
	/**
	 * Clears the rectangular region of tiles.
	 * @param	column		First tile column.
	 * @param	row			First tile row.
	 * @param	width		Width in tiles.
	 * @param	height		Height in tiles.
	 */
	public void clearRect(int column, int row, int width, int height) {
		if (usePositions)
		{
			column /= mTile.width();
			row /= mTile.height();
			width /= mTile.width();
			height /= mTile.height();
		}
		column %= mColumns;
		row %= mRows;
		int c = column;
		int r = column + width;
		int b = row + height;
		boolean u = usePositions;
		usePositions = false;
		while (row < b) {
			while (column < r) {
				clearTile(column, row);
				column ++;
			}
			column = c;
			row ++;
		}
		usePositions = u;
	}
	
	
	/**
	* Loads the Tilemap tile index data from a string. Column Seperator is "," and Row Seperator is "\n"
	* @param str			The string data, which is a set of tile values separated by the columnSep and rowSep strings.
	*/
	public void loadFromString(String str) {
		loadFromString(str, ",", "\n");
	}
	
	/**
	* Loads the Tilemap tile index data from a string.
	* @param str			The string data, which is a set of tile values separated by the columnSep and rowSep strings.
	* @param columnSep		The string that separates each tile value on a row, default is ",".
	* @param rowSep			The string that separates each row of tiles, default is "\n".
	*/
	public void loadFromString(String str, String columnSep, String rowSep) {
		String row[] = str.split(rowSep);
		int rows = row.length;
		String col[];
		int cols;
		int x, y;
		int xp;
		for (y = 0; y < rows; y ++) {
			xp = 0;
			if ("".equals(row[y])) 
				continue;
			col = row[y].split(columnSep);
			cols = col.length;
			for (x = 0; x < cols; x ++)
			{
				if ("".equals(col[x])) {
					xp--;
					continue;
				}
				setTile(x+xp, y, Integer.parseInt(col[x]));
			}
		}
		setTileTextureBuffer();
	}
	
	/**
	* Saves the Tilemap tile index data to a string. columnSep = "," rowSep = "\n"
	*/
	public String saveToString() {
		return saveToString(",", "\n");
	}
	
	/**
	* Saves the Tilemap tile index data to a string.
	* @param columnSep		The string that separates each tile value on a row, default is ",".
	* @param rowSep			The string that separates each row of tiles, default is "\n".
	*/
	public String saveToString(String columnSep, String rowSep) {
		StringBuilder s = new StringBuilder(mRows*mColumns);
		int x, y;
		for (y = 0; y < mRows; y ++) {
			for (x = 0; x < mColumns; x ++) {
				s.append(String.valueOf(getTile(x, y)));
				if (x != mColumns - 1) 
					s.append(columnSep);
			}
			if (y != mRows - 1) 
				s.append(rowSep);
		}
		return s.toString();
	}
	
	/**
	 * Gets the index of a tile, based on its column and row in the tileset.
	 * @param	tilesColumn		Tileset column.
	 * @param	tilesRow		Tileset row.
	 * @return	Index of the tile.
	 */
	public int getIndex(int tilesColumn, int tilesRow) {
		return (tilesRow % mSetRows) * mSetColumns + (tilesColumn % mSetColumns);
	}
	
	/**
	 * The tile width.
	 */
	public int getTileWidth() { return mTile.width(); }

	/**
	 * The tile height.
	 */
	public int getTileHeight() { return mTile.height(); }

	/**
	 * How many columns the tilemap has.
	 */
	public int getColumns() { return mColumns; }

	/**
	 * How many rows the tilemap has.
	 */
	public int getRows() { return mRows; }

	@Override
	protected void release() {
		super.release();
		
		mMap.recycle();
	}

}
