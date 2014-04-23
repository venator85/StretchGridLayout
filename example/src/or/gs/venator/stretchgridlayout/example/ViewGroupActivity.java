package or.gs.venator.stretchgridlayout.example;

import java.util.Random;

import or.gs.venator.stretchgridlayout.StretchGridLayout;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ViewGroupActivity extends Activity {

	private static final int MENU_REFRESH = 1;
	private static final int MENU_1_COLUMN = 2;
	private static final int MENU_2_COLUMNS = 3;
	private static final int MENU_3_COLUMNS = 4;
	private static final int MENU_TOGGLE_FORCE_ROWS_EQUAL_HEIGHT = 5;

	private StretchGridLayout grid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_viewgroup);
		grid = (StretchGridLayout) findViewById(R.id.grid);

		generateButtonsText();
	}

	private void generateButtonsText() {
		// assign random text to buttons
		Random r = new Random();
		for (int i = 0; i < grid.getChildCount(); i++) {
			TextView b = (TextView) grid.getChildAt(i);
			int len = r.nextInt(10) + 1;
			String s = "";
			for (int j = 0; j < len; j++) {
				int v = r.nextInt(50000);
				s += String.valueOf(v) + " ";
			}
			s = s.trim();
			b.setText(s);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		menu.add(0, MENU_REFRESH, 0, "Change buttons text").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(0, MENU_1_COLUMN, 0, "1 column").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(0, MENU_2_COLUMNS, 0, "2 column").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(0, MENU_3_COLUMNS, 0, "3 column").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(0, MENU_TOGGLE_FORCE_ROWS_EQUAL_HEIGHT, 0, "Toggle equal rows height").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == MENU_REFRESH) {
			generateButtonsText();
			return true;
		} else if (item.getItemId() == MENU_1_COLUMN) {
			grid.setColumns(1);
			return true;
		} else if (item.getItemId() == MENU_2_COLUMNS) {
			grid.setColumns(2);
			return true;
		} else if (item.getItemId() == MENU_3_COLUMNS) {
			grid.setColumns(3);
			return true;
		} else if (item.getItemId() == MENU_TOGGLE_FORCE_ROWS_EQUAL_HEIGHT) {
			grid.setForceEqualRowsHeight(!grid.isForceEqualRowsHeight());
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
