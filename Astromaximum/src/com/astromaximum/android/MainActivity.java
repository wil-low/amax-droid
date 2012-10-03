package com.astromaximum.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import com.astromaximum.util.DataProvider;

public class MainActivity extends Activity {
	static final int DATE_DIALOG_ID = 0;
	static final int CONVERT_DB_DIALOG_ID = 1;
	static final int PROGRESS_DIALOG_ID = 2;

	private Context mContext = null;
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
        mEventProvider = DataProvider.getInstance(mContext);
       
        setContentView(R.layout.main);
        mDateButton = (Button) findViewById(R.id.Button01);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        mCurrentLocationButton = (Button) findViewById(R.id.CurrentLocationButton);

        mEventList  = (ListView)findViewById(R.id.ListViewEvents);
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
        mEventProvider.saveState();
	}
    
    @Override
	protected void onResume() {
		super.onResume();
        Log.d(TAG, "OnResume");
        mEventProvider.restoreState();
		updateDisplay();
	}

    @Override
	protected void onRestart() {
		super.onRestart();
        Log.d(TAG, "OnRestart");
	}
    
    private void updateDisplay() {
    	mEventProvider.setDate(2011, 5, 22);
    	mEventProvider.gatherEvents(DataProvider.RANGE_DAY);
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
			break;
		}
	}
	
}