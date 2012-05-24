package me.pdthx;

import android.graphics.BlurMaskFilter;
import java.util.ArrayList;
import java.util.List;

import com.zubhium.ZubhiumSDK;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;

public class CustomLockView extends View {

	private OnTouchListener listener = null;
	private List<GridCell> buttonGrid;
	private List<Integer> passcode;
	private int gridSize = 3;
	private int gridLength = 0;
	private int offsetX = 0;
	private int offsetY = 0;
	private Bitmap unselected = BitmapFactory.decodeResource(
			this.getResources(), R.drawable.nsel);

	private Bitmap selected = BitmapFactory.decodeResource(this.getResources(),
			R.drawable.sel);
	//private Bitmap line = BitmapFactory.decodeResource(this.getResources(), R.drawable.);

	ZubhiumSDK sdk ;

	public CustomLockView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);


	}
	protected void buildButtonGrid() {
		int width = unselected.getWidth();
		int height = unselected.getHeight();

		int posX = 0;
		int posY = 0;

		buttonGrid = new ArrayList<GridCell>();

		// row 1
		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength/gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength/gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));

		// row 2
		posX = 0;
		posY += height;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength/gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength/gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));

		// row 3
		posX = 0;
		posY += height;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength/gridSize;

		buttonGrid.add(new GridCell(posX, posY, width, height));
		posX += gridLength/gridSize;

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
			clearSelection();

			for (int i = 0; i < buttonGrid.size(); i++) {
				if(buttonGrid.get(i).isEventInGridCell(event.getX(), event.getY()))
				{
					if(!passcode.contains(i + 1))
						passcode.add(i+1);
				}
			}

			invalidate();

			return true;
		case MotionEvent.ACTION_MOVE:
			for (int i = 0; i < buttonGrid.size(); i++) {
				if(buttonGrid.get(i).isEventInGridCell(event.getX(), event.getY()))
				{
					if(!passcode.contains(i + 1))
						passcode.add(i+1);
				}
			}
			invalidate();

			return true;
		case MotionEvent.ACTION_UP:
			if(listener != null)
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

		int index = row*3 + 0;

		drawCell(index, canvas, paint);

		index += 1;
		drawCell(index, canvas, paint);

		index += 1;
		drawCell(index, canvas, paint);

		offsetX = 0;
		offsetY += unselected.getHeight();
	}

	protected void drawCell(int index,  Canvas canvas, Paint paint) {

		if (buttonGrid.get(index).getSelected() == true) {
			canvas.drawBitmap(selected, offsetX, offsetY, paint);
	        offsetX += gridLength/gridSize;
		} else {
			canvas.drawBitmap(unselected, offsetX, offsetY, paint);
			offsetX += gridLength/gridSize;

		}
	}
	public String getPasscode() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < passcode.size(); i++) {
			result.append(passcode.get(i));
		}

		return result.toString();
	}

}
