package com.michaelquiapos.blogreader;

//import android.R.anim;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
//import android.widget.Toast;

public class MainListActivity extends ListActivity {
	
	// We generally want to use protected for member variables unless we have specific reason to make them public
	// DATA
	protected String[] mAndroidNames = {
		"Android beta",
		"Android 1.0",
		"Android 1.1",
		"Cupcake",
		"Donut",
		"Eclair",
		"Froyo",
		"Gingerbread",
		"Honeycomb",
		"Ice Cream Sandwich",
		"Jelly Bean"
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_list);

		// Adaptor
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mAndroidNames);
		setListAdapter(adapter);
		
		// Use to display the string from the @string resource
			// String message = getString(R.string.no_items);
			// Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		// or
			// Toast.makeText(this, getString(R.string.no_items), Toast.LENGTH_LONG).show();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_list, menu);
		return true;
	}

}
