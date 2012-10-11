package com.astromaximum.android.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.astromaximum.android.R;
import com.astromaximum.util.Event;

public class SummaryAdapter extends ArrayAdapter<SummaryItem> {

	class ViewHolder {
		TextView mType;
		TextView mText;
		ImageView mPlanet0;
		ImageView mPlanet1;
	}

	public SummaryAdapter(Context context, SummaryItem[] arr) {
		super(context, 0, arr);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.event_list_item, null);
			holder = new ViewHolder();
			holder.mType = (TextView) v.findViewById(R.id.EventListItemType);
			holder.mText = (TextView) v.findViewById(R.id.EventListItemText);
			holder.mPlanet0 = (ImageView) v
					.findViewById(R.id.EventListItemPlanet0);
			holder.mPlanet1 = (ImageView) v
					.findViewById(R.id.EventListItemPlanet1);
			v.setTag(holder);
		} else {
			holder = (ViewHolder) v.getTag();
		}
		SummaryItem si = getItem(position);
		holder.mType.setText(si.mKey);
		if (!si.mEvents.isEmpty()) {
			Event e = si.mEvents.get(0);
			holder.mText.setText(Event.long2String(e.getDate0(), 0, true));
			Resources res = getContext().getResources();
			if (e.getPlanet0() >= 0) {
				Drawable drawable = res.getDrawable(R.drawable.p00
						+ e.getPlanet0());
				holder.mPlanet0.setImageDrawable(drawable);
			}
			if (e.getPlanet1() >= 0) {
				Drawable drawable = res.getDrawable(R.drawable.p00
						+ e.getPlanet1());
				holder.mPlanet1.setImageDrawable(drawable);
			}
			v.setEnabled(true);
		} else {
			holder.mText.setText("");
			holder.mPlanet0.setImageDrawable(null);
			holder.mPlanet1.setImageDrawable(null);
			v.setEnabled(false);
		}
		return v;
	}
}
