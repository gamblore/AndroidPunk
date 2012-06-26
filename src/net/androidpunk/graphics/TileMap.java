package net.androidpunk.graphics;

import net.androidpunk.FP;
import net.androidpunk.utils.Draw;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.Rect;
import android.util.Log;

/**
 * A canvas to which Tiles can be drawn for fast multiple tile rendering.
 */
public class TileMap extends CanvasGraphic {

	private static final String TAG = "TileMap";
	/**
	 * If x/y positions should be used instead of columns/rows.
	 */
	public boolean usePositions = false;
	
	// Tilemap information.
	protected Bitmap mMap;
	private Bitmap mTemp;
	private int mColumns;
	private int mRows;

	// Tileset information.
	private Bitmap mSet;
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
	public TileMap(Bitmap tileset, int width, int height, int tileWidth, int tileHeight) {
		super(width - (width % tileWidth), height - (height % tileHeight));
		// set some tilemap information
		mWidth = width - (width % tileWidth);
		mHeight = height - (height % tileHeight);
		mColumns = mWidth / tileWidth;
		mRows = mHeight / tileHeight;
		mMap = Bitmap.createBitmap(mColumns, mRows, Config.ARGB_8888);
		mTemp = mMap.copy(Config.ARGB_8888, true);
		mTile = new Rect(0, 0, tileWidth, tileHeight);

		// create the canvas
		mMaxWidth -= mMaxWidth % tileWidth;
		mMaxHeight -= mMaxHeight % tileHeight;
		

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
		
		int newX = (index % mSetColumns) * mTile.width();
		int newY = (int)(index / mSetColumns) * mTile.height();
		mTile.offsetTo(newX, newY);
		
		mMap.setPixel(column, row, index);
		if (index < 0) {
			clearTile(column, row);
		} else {
			draw(column * mTile.width(), row * mTile.height(), mSet, mTile);
		}
		
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
		int newX = column * mTile.width();
		int newY = row * mTile.height();
		mTile.offsetTo(newX, newY);
		fill(mTile, 0);
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
		return mMap.getPixel(column % mColumns, row % mRows);
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
	 * Shifts all the tiles in the tilemap. Without wrapping.
	 * @param	columns		Horizontal shift.
	 * @param	rows		Vertical shift.
	 */
	public void shiftTiles(int columns, int rows) {
		shiftTiles(columns, rows, false);
	}
	/**
	 * Shifts all the tiles in the tilemap.
	 * @param	columns		Horizontal shift.
	 * @param	rows		Vertical shift.
	 * @param	wrap		If tiles shifted off the canvas should wrap around to the other side.
	 */
	public void shiftTiles(int columns, int rows, boolean wrap) {
		if (usePositions) {
			columns /= mTile.width();
			rows /= mTile.height();
		}
		mCanvas.setBitmap(mTemp);
		if (!wrap) {
			Draw.rect(0, 0, mTemp.getWidth(), mTemp.getHeight(), 0);
		}

		if (columns != 0) {
			shift(columns * mTile.width(), 0);
			if (wrap) {
				mCanvas.setBitmap(mTemp);
				mCanvas.drawBitmap(mMap, 0, 0, null);
			}
			mCanvas.setBitmap(mMap);
			mCanvas.drawBitmap(mTemp, columns, 0, null);

			mRect.offsetTo(columns > 0 ? 0 : mColumns + columns, 0);
			mRect.right = mRect.left + Math.abs(columns);
			mRect.bottom = mRows;
			updateRect(mRect, !wrap);
		}

		if (rows != 0) {
			shift(0, rows * mTile.height());
			if (wrap) {
				mCanvas.setBitmap(mTemp);
				mCanvas.drawBitmap(mMap, 0, 0, null);
			}
			
			mCanvas.setBitmap(mMap);
			mCanvas.drawBitmap(mTemp, 0, rows, null);
			
			mRect.offsetTo(0, rows > 0 ? 0 : mRows + rows);
			mRect.right = mRect.left + mColumns;
			mRect.bottom = mRows + Math.abs(rows);
			updateRect(mRect, !wrap);
		}
	}
	
	/** @private Used by shiftTiles to update a rectangle of tiles from the tilemap. */
	private void updateRect(Rect rect, boolean clear) {
		int x = rect.left;
		int y = rect.top;
		int w = rect.right;
		int h = rect.bottom;
		boolean u = usePositions;
		usePositions = false;
		if (clear) {
			while (y < h) {
				while (x < w) clearTile(x ++, y);
				x = rect.left;
				y++;
			}
		} else {
			while (y < h) {
				while (x < w)
					updateTile(x++, y);
				x = rect.left;
				y++;
			}
		}
		usePositions = u;
	}
	
	/** @private Used by shiftTiles to update a tile from the tilemap. */
	private void updateTile(int column, int row) {
		setTile(column, row, mMap.getPixel(column % mColumns, row % mRows));
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

}
