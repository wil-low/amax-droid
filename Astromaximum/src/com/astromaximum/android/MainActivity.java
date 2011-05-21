package com.astromaximum.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import com.astromaximum.util.DataFile;
import com.astromaximum.util.DataProvider;
import com.astromaximum.util.Location;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends Activity {
	static final int DATE_DIALOG_ID = 0;
	static final int CONVERT_DB_DIALOG_ID = 1;
	static final int PROGRESS_DIALOG_ID = 2;

	private Context mContext = null;
	private EphDataOpenHelper mDbHelper = null;
	private final String TAG = "MainActivity";
	private ListView mEventList = null;
	private Button mDateButton;

	private ProgressDialog progressDialog = null;
	private ProgressThread progressThread = null;
	
	private final String FILENAME_COMMON = "common.dat";
	private final String FILENAME_LOCATIONS = "locations.dat";
	private Button mCurrentLocationButton;
	private DataProvider mEventProvider;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        mContext = getApplicationContext();
        mDbHelper = EphDataOpenHelper.getInstance(mContext);
        mEventProvider = DataProvider.getInstance();
       
        setContentView(R.layout.main);
        mDateButton = (Button) findViewById(R.id.Button01);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        mCurrentLocationButton = (Button) findViewById(R.id.CurrentLocationButton);

        if (mDbHelper.isEmpty(true)) {
            //showDialog(CONVERT_DB_DIALOG_ID);
	        try {
	        	AssetManager manager = getAssets();
	        	InputStream is = manager.open(FILENAME_COMMON);
				mDbHelper.convertDataFile(is, true);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            //dismissDialog(CONVERT_DB_DIALOG_ID);
        }
        if (mDbHelper.isEmpty(false)) {
            //showDialog(CONVERT_DB_DIALOG_ID);
	        try {
	        	AssetManager manager = getAssets();
	        	InputStream is = manager.open(FILENAME_LOCATIONS);
				mDbHelper.convertDataFile(is, false);
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}
            //dismissDialog(CONVERT_DB_DIALOG_ID);
        }
        mEventList  = (ListView)findViewById(R.id.ListViewEvents);
		final String[] columns = {EphDataOpenHelper.KEY_EVENT_TYPE,
				EphDataOpenHelper.KEY_DATE0, EphDataOpenHelper.KEY_DATE1, EphDataOpenHelper.KEY_PLANET0, EphDataOpenHelper.KEY_PLANET1};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(mContext, R.layout.event_list_item, null, 
        		columns, new int[]{R.id.EventListItemType, R.id.EventListItemText, R.id.EventListItemText2, R.id.EventListItemPlanet0, R.id.EventListItemPlanet1});
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {
				switch (columnIndex) {
					case 4:
					case 5:
						int planet = cursor.getInt(columnIndex);
						if (planet >= 0) {
							ImageView image = (ImageView)view;
							Resources res = getResources();
							Drawable drawable = res.getDrawable(R.drawable.p00 + planet);
							image.setImageDrawable(drawable);
						}
						break;
					default:
						TextView text2 = (TextView)view;
						text2.setText(cursor.getString(columnIndex));
				}
				return true;
			}
        });
        mEventList.setAdapter(adapter);
    }

    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            int total = msg.getData().getInt("total");
            progressDialog.setProgress(total);
            if (total >= 100){
                dismissDialog(PROGRESS_DIALOG_ID);
                progressThread.setState(ProgressThread.STATE_DONE);
            }
        }
    };

    // the callback received when the user "sets" the date in the dialog
    private DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
    			public void onDateSet(DatePicker view, int year, 
                                      int monthOfYear, int dayOfMonth) {
    				DataProvider.getInstance().setDate (year, monthOfYear, dayOfMonth);
                    updateDisplay();
                }
            };
    

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main, menu);
		return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	super.onOptionsItemSelected(item);
    	switch (item.getItemId()) {
    	case R.id.menu_today: {
    		Intent intent = new Intent(this, SummaryActivity.class);
    		startActivity(intent);
    		break; }
    	case R.id.menu_options: {
    		Intent intent = new Intent(this, Preferences.class);
    		startActivityForResult(intent, PreferenceUtils.ID_PREFERENCE);
    		break; }
    	}
    	return true;
    }

    @Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mEventProvider.getYear(), mEventProvider.getMonth(), mEventProvider.getDay());
		case CONVERT_DB_DIALOG_ID:
	    	ProgressDialog convertDbDialog;
	    	convertDbDialog = new ProgressDialog(mContext);
	    	convertDbDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	    	convertDbDialog.setMessage("Loading...");
	    	convertDbDialog.setCancelable(false);
	    	return convertDbDialog;
        case PROGRESS_DIALOG_ID:
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Loading...");
            progressThread = new ProgressThread(handler);
            progressThread.start();
            return progressDialog;
        default:
			return null;
		}
	}

    @Override
	protected void onPause() {
		super.onPause();
        Log.d(TAG, "OnPause");
		mDbHelper.close();
	}
    
    @Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG, "OnResume");
        setCurrentLocation();
		updateDisplay();
	}

    @Override
	protected void onRestart() {
		super.onRestart();
        Log.d(TAG, "OnRestart");
	}
    
    @Override
    protected void onSaveInstanceState (Bundle outState) {
    	super.onSaveInstanceState(outState);
    	Log.d(TAG, "onSaveInstanceState");
    	mEventProvider.saveState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	Log.d(TAG, "onRestoreInstanceState");
    	mEventProvider.restoreState(savedInstanceState);
    }

    private void updateDisplay() {
/*    	
     	mDateButton.setText (new StringBuilder()
                    .append(mYear).append("-")
                    // Month is 0 based so add 1
                    .append(mMonth + 1).append("-")
                    .append(mDay).append(" "));
     	DataFile.calendar.set(mYear, mMonth, mDay, 0, 0, 0);
     	DataFile.calendar.set(Calendar.MILLISECOND, 0);
     	if (mCurrentLocation != null) { 
     		mCurrentLocationButton.setText(mCurrentLocation.mName);
	    	mEventCursor = mDbHelper.getEventsOnPeriod(
	    			DataFile.calendar.getTimeInMillis(),
	    			DataFile.calendar.getTimeInMillis() + DataFile.MSECINDAY,
	    			mCurrentLocation.mTimeZoneId);
	    	if (!mEventCursor.moveToFirst())
	    		Log.e(TAG, "No events");
	    	else {
		    	CursorAdapter cursorAdapter = (CursorAdapter) mEventList.getAdapter();
				cursorAdapter.changeCursor(mEventCursor);
	    	}
     	}
     	else {
     		Log.e(TAG, "No current location");
     	}
     	*/
    }
    
	private class ProgressThread extends Thread {
        Handler mHandler;
        final static int STATE_DONE = 0;
        final static int STATE_RUNNING = 1;
        int mState;
        int total;
       
        ProgressThread(Handler h) {
            mHandler = h;
        }
       
        public void run() {
            mState = STATE_RUNNING;   
            total = 0;
            while (mState == STATE_RUNNING) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread Interrupted");
                }
                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("total", total);
                msg.setData(b);
                mHandler.sendMessage(msg);
                total++;
            }
        }
        
        /* sets the current state for the thread,
         * used to stop the thread */
        public void setState(int state) {
            mState = state;
        }
    }
	
	@Override 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult " + requestCode + "=>" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PreferenceUtils.ID_PREFERENCE:
			setCurrentLocation();
			break;
		}
	}
	
	private void setCurrentLocation() {
        final long locationId = PreferenceUtils.getLocationId(this);
        //mCurrentLocation = mDbHelper.getLocation(mYear, locationId);
		Log.d(TAG, "Received locationId " + locationId);
	}
}