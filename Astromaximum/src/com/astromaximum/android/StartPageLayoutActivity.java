package com.astromaximum.android;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.astromaximum.android.view.StartPageLayoutAdapter;
import com.astromaximum.android.view.ViewHolder;
import com.astromaximum.util.MyLog;
import com.astromaximum.util.StartPageItem;
import com.mobeta.android.dslv.DragSortListView;

public class StartPageLayoutActivity extends Activity {

	protected static final String TAG = "StartPageLayoutActivity";
	private StartPageLayoutAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start_page_layout);
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
