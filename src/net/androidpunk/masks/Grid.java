package net.androidpunk.masks;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import net.androidpunk.FP;
import net.androidpunk.Mask;

public class Grid extends Hitbox {

    private static final String TAG = "Grid";
    
    public boolean usePositions = false;
    
    private Bitmap mData;
    private int mColumns;
    private int mRows;
    private Rect mTile;
    private Rect mRect = FP.rect;
    private Point mPoint = FP.point;
    private Canvas mCanvas = FP.canvas;
    private Paint mPaint = FP.paint;
    
    public Grid(int width, int height, int tileWidth, int tileHeight) {
        this(width, height, tileWidth, tileHeight, 0, 0);
    }
    
    public Grid(int width, int height, int tileWidth, int tileHeight, int x) {
        this(width, height, tileWidth, tileHeight, x, 0);
    }
    
    public Grid(int width, int height, int tileWidth, int tileHeight, int x, int y) {

        if (width == 0 || height == 0 || tileWidth == 0 || tileHeight == 0) { 
            Log.e(TAG, "Illegal Grid, sizes cannot be 0.");    
            return;
        }
        
        mColumns = width/tileWidth;
        mRows = height/tileHeight;
        mData = Bitmap.createBitmap(mColumns, mRows, Config.ARGB_4444);
        Canvas c = new Canvas(mData);
        c.drawColor(0);
        mTile = new Rect(0, 0, tileWidth, tileHeight);
        
        mWidth = width;
        mHeight = height;
        mX = x;
        mY = y;
        
        
        mCheck.put(Mask.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideMask(m);
            }
        });
        
        mCheck.put(Hitbox.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideHitbox((Hitbox)m);
            }
        });
        
        mCheck.put(PixelMask.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collidePixelMask((PixelMask)m);
            }
        });
        
        mCheck.put(Grid.class, new CollideCallback() {
            
            @Override
            public boolean collide(Mask m) {
                return collideGrid((Grid)m);
            }
        });
        
    }
    
    /**
	 * Sets the value of the tile to solid.
	 * @param	column		Tile column.
	 * @param	row			Tile row.
	 */
    public void setTile(int column, int row) {
    	setTile(column, row, true);
    }
    /**
	 * Sets the value of the tile.
	 * @param	column		Tile column.
	 * @param	row			Tile row.
	 * @param	solid		If the tile should be solid.
	 */
	public void setTile(int column, int row, boolean solid) {
		if (usePositions) {
			column /= mTile.width();
			row /= mTile.height();
		}
		mData.setPixel(column, row, solid ? 0xffffffff : 0);
	}

	/**
	 * Makes the tile non-solid.
	 * @param	column		Tile column.
	 * @param	row			Tile row.
	 */
	public void clearTile(int column, int row) {
		setTile(column, row, false);
	}

	/**
	 * Gets the value of a tile.
	 * @param	column		Tile column.
	 * @param	row			Tile row.
	 * @return	tile value.
	 */
	public boolean getTile(int column, int row) {
		if (usePositions) {
			column /= mTile.width();
			row /= mTile.height();
		}
		return mData.getPixel(column, row) != 0;
	}

	/**
	 * Sets the value of a rectangle region of tiles.
	 * @param	column		First column.
	 * @param	row			First row.
	 * @param	width		Columns to fill.
	 * @param	height		Rows to fill.
	 * @param	fill		Value to fill.
	 */
	public void setRect(int column , int row, int width, int height, boolean solid)
	{
		if (usePositions) {
			column /= mTile.width();
			row /= mTile.height();
			width /= mTile.width();
			height /= mTile.height();
		}
		mRect.set(column, row, column + width, row + height);
		mCanvas.setBitmap(mData);
		mPaint.reset();
		mPaint.setColor(solid ? 0xffffffff : 0);
		mCanvas.drawRect(mRect, mPaint);
	}

	/**
	 * Makes the rectangular region of tiles non-solid.
	 * @param	column		First column.
	 * @param	row			First row.
	 * @param	width		Columns to fill.
	 * @param	height		Rows to fill.
	 */
	public void clearRect(int column, int row, int width, int height) {
		setRect(column, row, width, height, false);
	}

	/**
	* Loads the grid data from a string. Using "," and "\n" as columnSep and rowSep.
	* @param str			The string data, which is a set of tile values (0 or 1) separated by the columnSep and rowSep strings.
	*/
	public void loadFromString(String str) {
		loadFromString(str, ",", "\n");
	}
	/**
	* Loads the grid data from a string.
	* @param str			The string data, which is a set of tile values (0 or 1) separated by the columnSep and rowSep strings.
	* @param columnSep		The string that separates each tile value on a row, default is ",".
	* @param rowSep			The string that separates each row of tiles, default is "\n".
	*/
	public void loadFromString(String str, String columnSep, String rowSep) {
		String row[] = str.split(rowSep);
		int rows = row.length;
		String col[];
		int cols;
		int x, y;
		// for empty string indexing
		int xp = 0;
		for (y = 0; y < rows; y ++) {
			xp = 0;
			if ("".equals(row[y]))
				continue;
			
			col = row[y].split(columnSep);
			
			cols = col.length;
			for (x = 0; x < cols; x ++) {
				if ("".equals(col[x])) {
					xp--;
					continue;
				}
				setTile(x+xp, y, Integer.parseInt(col[x]) > 0);
			}
		}
	}

	/**
	* Saves the grid data to a string. Using "," and "\n" as columnSep and rowSep
	*/
	public String saveToString() {
		return saveToString(",", "\n");
	}
	/**
	* Saves the grid data to a string.
	* @param columnSep		The string that separates each tile value on a row, default is ",".
	* @param rowSep			The string that separates each row of tiles, default is "\n".
	*/
	public String saveToString(String columnSep, String rowSep) {
		StringBuilder s = new StringBuilder();
		int x, y;
		for (y = 0; y < mRows; y ++) {
			for (x = 0; x < mColumns; x ++)
			{
				s.append(getTile(x, y) ? "1" : "0");
				if (x != mColumns - 1) s.append(columnSep);
			}
			if (y != mRows - 1)
				s.append(rowSep);
		}
		return s.toString();
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
	 * How many columns the grid has
	 */
	public int getColumns() { return mColumns; }

	/**
	 * How many rows the grid has.
	 */
	public int getRows() { return mRows; }

	/**
	 * The grid data.
	 */
	public Bitmap getData() { return mData; }
    
    /** @private Collide against an Entity. */
    private boolean collideMask(Mask other) {
        mRect.left = other.parent.x - other.parent.originX - parent.x + parent.originX;
        mRect.top = other.parent.y - other.parent.originY - parent.y + parent.originY;
        mPoint.x = (int)((mRect.left + other.parent.width - 1) / mTile.width()) + 1;
        mPoint.y = (int)((mRect.top + other.parent.height -1) / mTile.height()) + 1;
        
        mRect.left = (int)(mRect.left / mTile.width());
        mRect.top = (int)(mRect.top / mTile.height());
        mRect.right = mPoint.x;
        mRect.bottom = mPoint.y;
        
        return hitTest(mData, FP.zero, 1, mRect);
    }
    
    /** @private Collides against a Hitbox. */
    private boolean collideHitbox(Hitbox other)
    {
        mRect.left = other.parent.x + other.mX - parent.x - mX;
        mRect.top = other.parent.y + other.mY - parent.y - mY;
        mPoint.x = (int)((mRect.left + other.mWidth - 1) / mTile.width()) + 1;
        mPoint.y = (int)((mRect.top + other.mHeight-1) / mTile.height()) + 1;
        
        mRect.left = (int)(mRect.left / mTile.width());
        mRect.top = (int)(mRect.top / mTile.height());
        mRect.right = mPoint.x;
        mRect.bottom = mPoint.y;
        
        return hitTest(mData, FP.zero, 1, mRect);
    }
    
    /** @private Collides against a Pixelmask. */
    private boolean collidePixelMask(PixelMask other) {
    	int x1 = other.parent.x + other.mX - parent.x - mX;
		int y1 = other.parent.y + other.mY - parent.y - mY;
		int x2 = ((x1 + other.getWidth() - 1) / mTile.width());
		int y2 = ((y1 + other.getHeight() - 1) / mTile.height());
    	
		mPoint.x = x1;
		mPoint.y = y1;
		x1 /= mTile.width();
		y1 /= mTile.height();
		mRect.left = x1 * mTile.width();
		mRect.top = y1 * mTile.height();
		mRect.right = mRect.left + mTile.width();
		mRect.bottom = mRect.top + mTile.height();
		int xx = x1;
		while (y1 <= y2) {
			while (x1 <= x2) {
				if (mData.getPixel(x1, y1) > 0) {
					if (hitTest(other.mData, mPoint, 1, mTile)) 
						return true;
				}
				x1++;
				mRect.offset(mTile.width(), 0);
			}
			x1 = xx;
			y1++;
			mRect.left = x1 * mTile.width();
			mRect.right = mRect.left + mTile.width();
			mRect.offset(0, mTile.height());
		}
		return false;
    }
    
    private boolean collideGrid(Grid other) {
        // Find the X edges
        int ax1 = parent.x + mX;
        int ax2 = ax1 + mWidth;
        int bx1 = other.parent.x + other.mX;
        int bx2 = bx1 + other.mWidth;
        if (ax2 < bx1 || ax1 > bx2) 
            return false;
        
        // Find the Y edges
        int ay1 = parent.y + mY;
        int ay2 = ay1 + mHeight;
        int by1 = other.parent.y + other.mY;
        int by2 = by1 + other.mHeight;
        if (ay2 < by1 || ay1 > by2)
            return false;     
        
        // Find the overlapping area
        int ox1 = ax1 > bx1 ? ax1 : bx1;
        int oy1 = ay1 > by1 ? ay1 : by1;
        int ox2 = ax2 < bx2 ? ax2 : bx2;
        int oy2 = ay2 < by2 ? ay2 : by2;
        
        // Find the smallest tile size, and snap the top and left overlapping
        // edges to that tile size. This ensures that corner checking works
        // properly.
        int tw, th;
        
        if (mTile.width() < other.mTile.width()) {
            tw = mTile.width();
            ox1 -= parent.x + mX;
            ox1 = (int)(ox1 / tw) * tw;
            ox1 += parent.x + mX;
        } else {
            tw = other.mTile.width();
            ox1 -= other.parent.x + other.mX;
            ox1 = (int)(ox1 / tw) * tw;
            ox1 += other.parent.x + other.mX;
        }
        
        if (mTile.height() < other.mTile.height()){
            th = mTile.height();
            oy1 -= parent.y + mY;
            oy1 = (int)(oy1 / th) * th;
            oy1 += parent.y + mY;
        } else {
            th = other.mTile.height();
            oy1 -= other.parent.y + other.mY;
            oy1 = (int)(oy1 / th) * th;
            oy1 += other.parent.y + other.mY;
        }
        
        // Step through the overlapping rectangle
        for (int y = oy1; y < oy2; y += th) {
            // Get the row indices for the top and bottom edges of the tile
            int ar1 = (y - parent.y - mY) / mTile.height();
            int br1 = (y - other.parent.y - other.mY) / other.mTile.height();
            int ar2 = ((y - parent.y - mY) + (th - 1)) / mTile.height();
            int br2 = ((y - other.parent.y - other.mY) + (th - 1)) / other.mTile.height();
            
            for (int x = ox1; x < ox2; x += tw) {
                // Get the column indices for the left and right edges of the tile
                int ac1 = (x - parent.x - mX) / mTile.width();
                int bc1 = (x - other.parent.x - other.mX) / other.mTile.width();
                int ac2 = ((x - parent.x - mX) + (tw - 1)) / mTile.width();
                int bc2 = ((x - other.parent.x - other.mX) + (tw - 1)) / other.mTile.width();
                
                // Check all the corners for collisions
                if ((mData.getPixel(ac1, ar1) > 0 && other.mData.getPixel(bc1, br1) > 0)
                 || (mData.getPixel(ac2, ar1) > 0 && other.mData.getPixel(bc2, br1) > 0)
                 || (mData.getPixel(ac1, ar2) > 0 && other.mData.getPixel(bc1, br2) > 0)
                 || (mData.getPixel(ac2, ar2) > 0 && other.mData.getPixel(bc2, br2) > 0)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public void renderDebug(Canvas c) {
        float sx = FP.screen.getScaleX() * FP.screen.getScale();
        float sy = FP.screen.getScaleY() * FP.screen.getScale();
        
        int x, y;
        Paint p = new Paint();
        p.setStyle(Style.STROKE);
        p.setColor(Color.argb(255/4, 255, 255, 255));
        p.setStrokeWidth(1.0f);
        
        for (y = 0; y < mRows; y++ ) {
            for (x = 0; x < mColumns; x++) {
                if (mData.getPixel(x, y) > 0) {
                    c.drawRect((parent.x - parent.originX - FP.camera.x + x * mTile.width()) * sx,
                            (parent.y - parent.originY - FP.camera.y + y * mTile.height()) * sy,
                            mTile.width() * sx,
                            mTile.height() * sy,
                            p);
                   }
            }
        }
    }
}
