package me.pdthx;

public class GridCell {
	private int _x;
	private int _y;
	private int _width;
	private int _height;
	private boolean _selected;

	public GridCell(int x, int y, int width, int height) {
		_x = x;
		_y = y;
		_width = width;
		_height = height;
		_selected = false;
	}
	public void setSelected(boolean selected) {
		_selected = selected;
	}
	public boolean getSelected() {
		return _selected;
	}
	public boolean isEventInGridCell(float x, float y) {
		if ((x >= _x) && (y >= _y) && (x < _x + _width) && (y < _y + _width)) {
			_selected = true;
			return true;
		}
		
		return false;
	}
}
