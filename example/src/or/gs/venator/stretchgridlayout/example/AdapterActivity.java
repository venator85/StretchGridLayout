package or.gs.venator.stretchgridlayout.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import or.gs.venator.stretchgridlayout.StretchGridLayout;
import or.gs.venator.stretchgridlayout.StretchGridLayout.OnItemClickListener;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AdapterActivity extends Activity {

	private static final int MENU_REFRESH = 1;
	private static final int MENU_1_COLUMN = 2;
	private static final int MENU_2_COLUMNS = 3;
	private static final int MENU_3_COLUMNS = 4;
	private static final int MENU_TOGGLE_FORCE_ROWS_EQUAL_HEIGHT = 5;

	private StretchGridLayout grid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adapter);
		grid = (StretchGridLayout) findViewById(R.id.grid);


		populate();
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(StretchGridLayout parent, View view, int position, long id) {
				Toast.makeText(AdapterActivity.this, "Clicked item at position " + position + " with text: " + ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void populate() {
		List<String> buttonsText = generateButtonsText();
		grid.setAdapter(new ArrayAdapter<String>(this, R.layout.cell_layout, buttonsText));
	}

	private List<String> generateButtonsText() {
		// assign random text to buttons
		Random r = new Random();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 49; i++) {
			int len = r.nextInt(10) + 1;
			String s = "";
			for (int j = 0; j < len; j++) {
				int v = r.nextInt(50000);
				s += String.valueOf(v) + " ";
			}
			s = s.trim();
			list.add(s);
		}
		return list;
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
			populate();
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
