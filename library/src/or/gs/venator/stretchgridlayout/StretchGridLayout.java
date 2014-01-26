package or.gs.venator.stretchgridlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class StretchGridLayout extends ViewGroup {

	public static final int AUTO = Integer.MIN_VALUE;

	private int columns = 3;

	private int columnSeparatorSize = AUTO; // AUTO means take the intrinsic width/height from the separator drawable
	private int rowSeparatorSize = AUTO;

	private Drawable columnSeparator;
	private Drawable rowSeparator;

	private boolean forceEqualRowsHeight = true;

	public StretchGridLayout(Context context) {
		super(context);
		init(null);
	}

	public StretchGridLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	public StretchGridLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	protected void init(AttributeSet attrs) {
		setWillNotDraw(false);

		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.StretchGridLayout);

			columns = a.getInt(R.styleable.StretchGridLayout_columns, columns);

			rowSeparator = a.getDrawable(R.styleable.StretchGridLayout_row_separator);
			columnSeparator = a.getDrawable(R.styleable.StretchGridLayout_column_separator);

			columnSeparatorSize = a.getDimensionPixelSize(R.styleable.StretchGridLayout_column_separator_size, columnSeparatorSize);
			rowSeparatorSize = a.getDimensionPixelSize(R.styleable.StretchGridLayout_row_separator_size, rowSeparatorSize);

			forceEqualRowsHeight = a.getBoolean(R.styleable.StretchGridLayout_force_equal_rows_height, forceEqualRowsHeight);

			a.recycle();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		final int childCount = getChildCount();

		if (childCount == 0) {
			setMeasuredDimension(getPaddingLeft() + getPaddingRight(), getPaddingTop() + getPaddingBottom());
			return;
		}

		if (widthMode == MeasureSpec.EXACTLY) {
			final int totalWidth = widthSize - getPaddingLeft() - getPaddingRight();

			final int columnSeparatorSize = calculateColumnSeparatorSize();
			final int rowSeparatorSize = calculateRowSeparatorSize();

			final int childTargetWidth = (totalWidth - columnSeparatorSize * (columns - 1)) / columns;

			final int childWidthSpec = MeasureSpec.makeMeasureSpec(childTargetWidth, MeasureSpec.EXACTLY);

			int totalHeight = 0;

			final int rows = (int) Math.ceil(childCount / (float) columns);

			if (forceEqualRowsHeight) {
				int maxHeight = 0;

				int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				for (int i = 0; i < childCount; i++) {
					final View child = getChildAt(i);
					child.measure(childWidthSpec, childHeightSpec);
					maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
				}

				childHeightSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.EXACTLY);
				for (int i = 0; i < childCount; i++) {
					final View child = getChildAt(i);
					child.measure(childWidthSpec, childHeightSpec);
				}

				totalHeight = rows * maxHeight + (rows - 1) * rowSeparatorSize;

			} else {
				final int rowHeights[] = new int[rows];
				int totalRowsHeight = 0;

				int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
				for (int i = 0; i < rows; i++) {
					int rowLen = calculateRowLength(i);

					int maxRowHeight = 0;

					for (int j = 0; j < rowLen; j++) {
						int childIndex = i * columns + j;
						View child = getChildAt(childIndex);
						child.measure(childWidthSpec, childHeightSpec);
						maxRowHeight = Math.max(maxRowHeight, child.getMeasuredHeight());
					}

					rowHeights[i] = maxRowHeight;
					totalRowsHeight += maxRowHeight;
				}

				for (int i = 0; i < rows; i++) {
					int rowLen = calculateRowLength(i);

					childHeightSpec = MeasureSpec.makeMeasureSpec(rowHeights[i], MeasureSpec.EXACTLY);
					for (int j = 0; j < rowLen; j++) {
						int childIndex = i * columns + j;
						View child = getChildAt(childIndex);
						child.measure(childWidthSpec, childHeightSpec);
					}
				}

				totalHeight = totalRowsHeight + (rows - 1) * rowSeparatorSize;
			}

			totalHeight += getPaddingTop() + getPaddingBottom();
			setMeasuredDimension(widthSize, totalHeight);

		} else {
			throw new IllegalArgumentException("Width mode must be EXACTLY");
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		final int childCount = getChildCount();

		if (childCount == 0) {
			return;
		}

		final int columnSeparatorSize = calculateColumnSeparatorSize();
		final int rowSeparatorSize = calculateRowSeparatorSize();

		if (columnSeparatorSize == 0 && rowSeparatorSize == 0) {
			return;
		}

		final int numRows = (int) Math.ceil(childCount / (float) columns);

		int topOffset = getPaddingTop();

		for (int i = 0; i < numRows; i++) {
			int rowHeight;
			if (columns > 1) {
				rowHeight = 0;
				int rowLen = calculateRowLength(i);
				for (int j = 0; j < rowLen - 1; j++) {
					int childIndex = i * columns + j;
					View child = getChildAt(childIndex);

					final int childW = child.getMeasuredWidth();
					final int childH = child.getMeasuredHeight();
					rowHeight = childH;

					int left = getPaddingLeft() + (j + 1) * childW + j * columnSeparatorSize;
					int top = topOffset;
					int right = left + columnSeparatorSize;
					int bottom = top + childH;

					if (columnSeparator != null && columnSeparatorSize > 0) {
						columnSeparator.setBounds(left, top, right, bottom);
						columnSeparator.draw(canvas);
					}
				}
			} else {
				int childIndex = i * columns;
				View child = getChildAt(childIndex);
				rowHeight = child.getMeasuredHeight();
			}

			if (rowSeparator != null && rowSeparatorSize > 0 && i < numRows - 1) {
				rowSeparator.setBounds(getPaddingLeft(), topOffset + rowHeight, getMeasuredWidth() - getPaddingRight(), topOffset + rowHeight + rowSeparatorSize);
				rowSeparator.draw(canvas);
			}

			topOffset += rowHeight + rowSeparatorSize;
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();

		if (childCount == 0) {
			return;
		}

		final int numRows = (int) Math.ceil(childCount / (float) columns);

		final int columnSeparatorSize = calculateColumnSeparatorSize();
		final int rowSeparatorSize = calculateRowSeparatorSize();

		int topOffset = getPaddingTop();

		for (int i = 0; i < numRows; i++) {
			int rowLen = calculateRowLength(i);

			int rowHeight = 0;
			for (int j = 0; j < rowLen; j++) {
				int childIndex = i * columns + j;
				View child = getChildAt(childIndex);

				final int childW = child.getMeasuredWidth();
				final int childH = child.getMeasuredHeight();
				rowHeight = childH;

				int left = getPaddingLeft() + j * (columnSeparatorSize + childW);
				int top = topOffset;
				int right = left + childW;
				int bottom = top + childH;

				child.layout(left, top, right, bottom);
			}

			topOffset += rowHeight + rowSeparatorSize;
		}
	}

	public int calculateRowLength(int rowIndex) {
		int rowStartIndex = rowIndex * columns;
		int rowEndIndex = Math.min(rowStartIndex + columns, getChildCount());
		int rowLen = rowEndIndex - rowStartIndex;
		return rowLen;
	}

	public int calculateRowSeparatorSize() {
		final int rowSeparatorSize;
		if (rowSeparator != null && rowSeparator.getIntrinsicHeight() >= 0) {
			rowSeparatorSize = rowSeparator.getIntrinsicHeight();
		} else if (this.rowSeparatorSize != AUTO) {
			rowSeparatorSize = this.rowSeparatorSize;
		} else {
			rowSeparatorSize = 0;
		}
		return rowSeparatorSize;
	}

	public int calculateColumnSeparatorSize() {
		final int columnSeparatorSize;
		if (columnSeparator != null && columnSeparator.getIntrinsicWidth() >= 0) {
			columnSeparatorSize = columnSeparator.getIntrinsicWidth();
		} else if (this.columnSeparatorSize != AUTO) {
			columnSeparatorSize = this.columnSeparatorSize;
		} else {
			columnSeparatorSize = 0;
		}
		return columnSeparatorSize;
	}

	public void setColumns(int columns) {
		this.columns = columns;
		requestLayout();
	}

	public int getColumns() {
		return columns;
	}

	public int getColumnSeparatorSize() {
		return columnSeparatorSize;
	}

	public void setColumnSeparatorSize(int columnSeparatorSize) {
		this.columnSeparatorSize = columnSeparatorSize;
		requestLayout();
	}

	public int getRowsSeparatorSize() {
		return rowSeparatorSize;
	}

	public void setRowsSeparatorSize(int rowsSeparatorSize) {
		rowSeparatorSize = rowsSeparatorSize;
		requestLayout();
	}

	public Drawable getColumnSeparator() {
		return columnSeparator;
	}

	public void setColumnSeparator(Drawable columnSeparator) {
		this.columnSeparator = columnSeparator;
		requestLayout();
	}

	public Drawable getRowSeparator() {
		return rowSeparator;
	}

	public void setRowSeparator(Drawable rowSeparator) {
		this.rowSeparator = rowSeparator;
		requestLayout();
	}

	public void setForceEqualRowsHeight(boolean forceEqualRowsHeight) {
		this.forceEqualRowsHeight = forceEqualRowsHeight;
		requestLayout();
	}

	public boolean isForceEqualRowsHeight() {
		return forceEqualRowsHeight;
	}

}
