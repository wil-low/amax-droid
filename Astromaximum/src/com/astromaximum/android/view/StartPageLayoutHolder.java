package com.astromaximum.android.view;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.astromaximum.android.R;
import com.astromaximum.util.MyLog;
import com.astromaximum.util.StartPageItem;

public class StartPageLayoutHolder {
	protected static final String TAG = "StartPageLayoutHolder";
	private CheckBox mChecked;
	private TextView mCaption;

	public void initLayout(View v) {
		mCaption = (TextView)v.findViewById(R.id.caption);
		mChecked = (CheckBox)v.findViewById(R.id.checked);
	}

	public void fillLayout(final StartPageItem item) {
		mCaption.setText(item.mCaption);
		mChecked.setChecked(item.mIsEnabled);
		mChecked.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				item.mIsEnabled = ((CheckBox)v).isChecked();
			}
		});
	}
}
