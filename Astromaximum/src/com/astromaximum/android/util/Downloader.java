package com.astromaximum.android.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.astromaximum.android.PreferenceUtils;
import com.astromaximum.android.R;
import com.astromaximum.util.CommonDataFile;
import com.astromaximum.util.LocationsDataFile;

public class Downloader {
	private final String TAG = "Downloader";
	
	public static class Callback {
		public void callback(boolean isSuccess) {

		}
	}

	private static Downloader mInstance;
	private AQuery mAQuery;
	private Context mContext;
	private AmaxDatabase mDB;

	private Downloader(Context context) {
		mContext = context;
		mAQuery = new AQuery(mContext);
		mDB = AmaxDatabase.getInstance(mContext);
	}

	public static Downloader getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new Downloader(context);
		}
		return mInstance;
	}

	public void downloadLocation(final DataProvider provider, final String cityKey,
			final String cityName, final Callback cb) {
		ProgressDialog dialog = makeProgressDialog(cityName);

		String url = "http://astromaximum.com/data/" + provider.mPeriodKey
				+ "/" + cityKey;
		MyLog.d(TAG, url);
		mAQuery.progress(dialog).ajax(url, InputStream.class,
				new AjaxCallback<InputStream>() {
					public void callback(String url, InputStream is,
							AjaxStatus status) {
						if (is != null) {
							GZIPInputStream zis = null;
							try {
								zis = new GZIPInputStream(is);
								FileOutputStream fos = mContext.openFileOutput(
										provider.mPeriodStr + cityKey,
										Context.MODE_PRIVATE);
								byte[] buffer = new byte[1024];
								int count;
								while ((count = zis.read(buffer)) > 0)
									fos.write(buffer, 0, count);
								fos.close();
								FileInputStream fis = mContext
										.openFileInput(provider.mPeriodStr
												+ cityKey);
								LocationsDataFile ldf = new LocationsDataFile(
										fis);
								fis.close();
								long cityId = mDB.addCity(ldf, cityKey);
								mDB.addLocation(provider.mPeriodId, cityId);
								PreferenceUtils
										.setCityKey(mContext, cityKey);
								showSuccessToast(cityName);
								cb.callback(true);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							showErrorToast(status);
							cb.callback(false);
						}
					}
				});
	}

	public void downloadPeriod(final String periodStr, final String periodKey,
			final Callback cb) {
		ProgressDialog dialog = makeProgressDialog(periodStr);

		String url = "http://astromaximum.com/data/?buy=" + periodKey;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("k", "44b62ab3e3165298849ac71428eca191");

		mAQuery.progress(dialog).ajax(url, params, InputStream.class,
				new AjaxCallback<InputStream>() {
					public void callback(String url, InputStream is,
							AjaxStatus status) {
						if (is != null) {
							byte[] keyBuffer = new byte[16];
							try {
								if (is.read(keyBuffer) == 16) {
									String receivedKey = new String(keyBuffer);
									GZIPInputStream zis = new GZIPInputStream(
											is);
									FileOutputStream fos = mContext
											.openFileOutput(periodStr,
													Context.MODE_PRIVATE);
									byte[] buffer = new byte[1024];
									int count;
									while ((count = zis.read(buffer)) > 0)
										fos.write(buffer, 0, count);
									fos.close();
									FileInputStream fis = mContext
											.openFileInput(periodStr);
									CommonDataFile cdf = new CommonDataFile(
											fis, false);
									fis.close();
									long periodId = mDB.addPeriod(cdf,
											receivedKey);
									PreferenceUtils.setPeriodId(mContext,
											periodId);
									showSuccessToast(periodStr);
									cb.callback(true);
								}
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							showErrorToast(status);
							cb.callback(true);
						}
					}
				});

	}

	private ProgressDialog makeProgressDialog(String s) {
		ProgressDialog dialog = new ProgressDialog(mContext);

		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(true);
		dialog.setInverseBackgroundForced(false);
		dialog.setCanceledOnTouchOutside(true);
		dialog.setTitle(String.format(
				mContext.getResources().getString(R.string.downloading), s));

		return dialog;
	}

	private void showSuccessToast(String s) {
		Toast.makeText(
				mAQuery.getContext(),
				String.format(
						mContext.getResources().getString(R.string.downloaded),
						s), Toast.LENGTH_LONG).show();

	}

	private void showErrorToast(AjaxStatus status) {
		Toast.makeText(mAQuery.getContext(), "Error:" + status.getCode(),
				Toast.LENGTH_LONG).show();
	}
}
