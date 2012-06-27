package me.pdthx.CustomViews;

import java.util.ArrayList;
import java.util.List;

import me.pdthx.R;
import com.zubhium.ZubhiumSDK;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CustomLockView extends View {

	private List<Point> points = new ArrayList<Point>();
	private Paint paint = new Paint();
	private OnTouchListener listener = null;
	private List<GridCell> buttonGrid;
	private List<Integer> passcode;
	private int gridSize = 3;
	private int gridLength = 0;
	private int offsetX = 0;
	private int offsetY = 0;
	private Bitmap unselected = BitmapFactory.decodeResource(
			this.getResources(), R.drawable.btn_pinswipe_inactive);

	private Bitmap selected = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.btn_pinswipe_selected);
	
	private Bitmap pressed = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.btn_pinswipe_pressed);
	ZubhiumSDK sdk;

	public CustomLockView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		paint.setColor(Color.parseColor("#59b2b6"));
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
	}

	protected void buildButtonGrid() {
		int width = unselected.getWidth();
		int height = unselected.getHeight();

		int posX = 0;
		int posY = 0;

		buttonGrid = new ArrayList<GridCell>();

		// row 1
		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength / gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength / gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));

		// row 2
		posX = 0;
		posY += height*2;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength / gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength / gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));

		// row 3
		posX = 0;
		posY += height*2;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength / gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength / gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);
		int value = Math.min(width, height);
		if (width == 0) {
			value = height;
		} else if (height == 0) {
			value = width;
		}
		gridLength = value;

		setMeasuredDimension(value, value);
		buildButtonGrid();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			points = new ArrayList<Point>();
			clearSelection();

			for (int i = 0; i < buttonGrid.size(); i++) {
				if (buttonGrid.get(i).isEventInGridCell(event.getX(),
						event.getY())) {
					if (!passcode.contains(i + 1))
						passcode.add(i + 1);
				}
			}

			invalidate();

			return true;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < buttonGrid.size(); i++) {
				if (buttonGrid.get(i).isEventInGridCell(event.getX(),
						event.getY())) {
					Point point = new Point();
					int x_min = buttonGrid.get(i)._x;
					int x_max = buttonGrid.get(i)._x + buttonGrid.get(i)._side;
					int y_min = buttonGrid.get(i)._y;
					int y_max = buttonGrid.get(i)._y + buttonGrid.get(i)._side;
					point.x = (x_min + x_max)/2;
					point.y = (y_min + y_max)/2;
					points.add(point);
					if (!passcode.contains(i + 1))
						passcode.add(i + 1);
				}
			}
			invalidate();

			return true;
		case MotionEvent.ACTION_UP:
			if (listener != null)
				listener.onTouch(this, event);
			return false;
		}

		return false;
	}

	public void setOnTouchListener(OnTouchListener onTouchListener) {
		listener = onTouchListener;
	}

	private void clearSelection() {
		passcode = new ArrayList<Integer>();
		for (int i = 0; i < buttonGrid.size(); i++) {
			buttonGrid.get(i).setSelected(false);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Path path = new Path();
		boolean first = true;
		for (Point point : points) {
			if (first) {
				first = false;
				path.moveTo(point.x, point.y);
			} else {
				path.lineTo(point.x, point.y);
			}
		}
		canvas.drawPath(path, paint);
		drawGrid(canvas);

		super.onDraw(canvas);
	}

	protected void drawGrid(Canvas canvas) {
		Paint paint = new Paint(Color.WHITE);

		drawGridRow(0, canvas, paint);
		drawGridRow(1, canvas, paint);
		drawGridRow(2, canvas, paint);

		offsetY = 0;

	}

	protected void drawGridRow(int row, Canvas canvas, Paint paint) {

		int index = row * 3 + 0;
		;

		drawCell(index, canvas, paint);

		index += 1;
		drawCell(index, canvas, paint);

		index += 1;
		drawCell(index, canvas, paint);

		offsetX = 0;
		offsetY += unselected.getHeight()*2;
	}

	protected void drawCell(int index, Canvas canvas, Paint paint) {

		if (buttonGrid.get(index).getSelected() == true) {
			canvas.drawBitmap(selected, offsetX, offsetY, paint);
			offsetX += gridLength / gridSize;
		} else {
			canvas.drawBitmap(unselected, offsetX, offsetY, paint);
			offsetX += gridLength / gridSize;
		}
	}

	public String getPasscode() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < passcode.size(); i++) {
			result.append(passcode.get(i));
		}

		return result.toString();
	}

	private class GridCell {
		private int _x;
		private int _y;
		private int _side;
		private boolean _selected;

		public GridCell(int x, int y, int side, int height) {
			_x = x;
			_y = y;
			_side = side;
			_selected = false;
		}

		public void setSelected(boolean selected) {
			_selected = selected;
		}

		public boolean getSelected() {
			return _selected;
		}

		public boolean isEventInGridCell(float x, float y) {
			if ((x >= _x) && (y >= _y) && (x < _x + _side) && (y < _y + _side)) {
				_selected = true;
				return true;
			}

			return false;
		}
	}

	private class Point {
		float x, y;

		@Override
		public String toString() {
			return x + ", " + y;
		}
	}
}
