package com.astromaximum.android;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.astromaximum.android.util.MyLog;
import com.astromaximum.android.util.StartPageItem;
import com.astromaximum.android.view.StartPageLayoutAdapter;
import com.astromaximum.android.view.ViewHolder;
import com.mobeta.android.dslv.DragSortListView;

public class StartPageLayoutActivity extends SherlockActivity {

	protected static final String TAG = "StartPageLayoutActivity";
	private StartPageLayoutAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_page_layout);

		getSupportActionBar().setTitle(R.string.start_page_layout);
		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		ViewHolder.initialize(this);

		ArrayList<StartPageItem> checkList = PreferenceUtils.getStartPageLayout(this);

		mAdapter = new StartPageLayoutAdapter(this, checkList);

		DragSortListView dragList = (DragSortListView) findViewById(android.R.id.list);
		dragList.setAdapter(mAdapter);
		dragList.setDropListener(new DragSortListView.DropListener() {

			public void drop(int from, int to) {
				StartPageItem item = mAdapter.getItem(from);
				mAdapter.remove(item);
				mAdapter.insert(item, to);
				mAdapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		}
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		MyLog.d(TAG, "OnResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyLog.d(TAG, "OnPause");
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(this).edit();
		String[] mCaptions = getResources().getStringArray(
				R.array.startpage_items);		
		for (int i = 0; i < mCaptions.length; ++i) {
			StartPageItem item = mAdapter.getItem(i);
			if (item != null) {
				editor.putInt(PreferenceUtils.KEY_STARTPAGE_ITEM_INDEX + item.mIndex, i);
				editor.putBoolean(PreferenceUtils.KEY_STARTPAGE_ITEM_ENABLED + item.mIndex,
						item.mIsEnabled);
			}
		}
		editor.commit();
	}

}
