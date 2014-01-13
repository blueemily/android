package org.sagemath.droid;

import org.sagemath.droid.CellGroupsFragment.OnGroupSelectedListener;

import sheetrock.panda.changelog.ChangeLog;
import sheetrock.panda.changelog.SimpleEula;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.actionbarcompat.ActionBarActivity;

public class CellActivity
		extends ActionBarActivity 
		implements OnGroupSelectedListener{
	private final static String TAG = "CellActivity";
	private static final String DIALOG_NEW_CELL = "newCell";

	private ChangeLog changeLog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CellCollection.initialize(getApplicationContext());
		setContentView(R.layout.cell_activity);
		try {
			new SimpleEula(this).new EulaTask().execute();
		} catch (Exception e) {
			Log.e(TAG, "Error showing EULA: " + e.toString());
			e.printStackTrace();
			//this.finish();
		}
		
		changeLog = new ChangeLog(this);
        if (changeLog.firstRun())
            changeLog.getLogDialog().show();
		
		CellGroupsFragment groupsFragment = (CellGroupsFragment) 
				getSupportFragmentManager().findFragmentById(R.id.cell_groups_fragment);
		groupsFragment.setOnGroupSelected(this);
		
		CellListFragment listFragment = (CellListFragment)
				getSupportFragmentManager().findFragmentById(R.id.cell_list_fragment);
		if (listFragment != null && listFragment.isInLayout()) 
			listFragment.switchToGroup(null);
	}
	
	public static final String INTENT_SWITCH_GROUP = "intent_switch_group";
	
	@Override
	public void onGroupSelected(String group) {
		CellListFragment listFragment = (CellListFragment)
				getSupportFragmentManager().findFragmentById(R.id.cell_list_fragment);
		if (listFragment == null || !listFragment.isInLayout()) {
			Intent i = new Intent(getApplicationContext(), CellListActivity.class);
			i.putExtra(INTENT_SWITCH_GROUP, group);
			startActivity(i);
		} else {
			listFragment.switchToGroup(group);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.sparse, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Uri uri;
		Intent intent;
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.menu_add: {
			FragmentManager fm = this.getSupportFragmentManager();
			NewCellDialog dialog = new NewCellDialog();
			dialog.show(fm, DIALOG_NEW_CELL);
			return true;
		}
		case R.id.menu_search:
			Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.menu_changelog:
			changeLog.getFullLogDialog().show();
			return true;
		case R.id.menu_about_sage:
			uri = Uri.parse("http://www.sagemath.org");
			intent = new Intent(Intent.ACTION_VIEW, uri); 
			startActivity(intent); 
			return true;
		case R.id.menu_manual_user:
			uri = Uri.parse("http://www.sagemath.org/doc/tutorial/");
			intent = new Intent(Intent.ACTION_VIEW, uri); 
			startActivity(intent); 
			return true;
		case R.id.menu_manual_dev:
			uri = Uri.parse("http://www.sagemath.org/doc/reference/");
			intent = new Intent(Intent.ACTION_VIEW, uri); 
			startActivity(intent); 
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
