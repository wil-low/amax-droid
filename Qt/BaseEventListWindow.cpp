#include "BaseEventListWindow.h"

#include "util/DataProvider.h"

QString BaseEventListWindow::PREMIUM_KEY = "999ec9eafd3eb789e70c2a2584d21391";

BaseEventListWindow::BaseEventListWindow (QWidget *parent)
{
//	Event.setContext(mContext);
//	ViewHolder.initialize(mContext);
	mDataProvider = DataProviderSingleton::instance();

//	setContentView(layoutResId);

//	mEventList = (ListView) findViewById(R.id.event_list_view);
//	mNoPeriodLayout = (RelativeLayout) findViewById(R.id.NoPeriodLayout);
//	mMissingDataMessage = (TextView) mNoPeriodLayout
//			.findViewById(R.id.txtMissingData);
//	mMissingDataButton = (Button) mNoPeriodLayout
//			.findViewById(R.id.btnMissingData);
//	getSupportActionBar().setDisplayShowHomeEnabled(false);
}

void BaseEventListWindow::previousDate()
{
	mDataProvider->changeDate(-1);
	updateDisplay();
}

void BaseEventListWindow::nextDate()
{
	mDataProvider->changeDate(1);
	updateDisplay();
}

void BaseEventListWindow::downloadPeriod(const QString& periodStr)
{
//	Downloader.getInstance(mContext).downloadPeriod(periodStr,
//			PREMIUM_KEY, new Downloader.Callback() {
//				public void callback(boolean isSuccess) {
//					if (isSuccess) {
//						onPause();
//						onRestart();
//					}
//				}
//			});
}

void BaseEventListWindow::buyPeriod(const QString& periodStr)
{
//	Intent intent = new Intent(mContext, PeriodBuyActivity.class);
//	intent.putExtra(PreferenceUtils.PERIOD_STRING_KEY, periodStr);
//	startActivity(intent);
}

void BaseEventListWindow::updateDisplay()
{
	if (mDataProvider->hasPeriod()) {
		if (mDataProvider->hasLocation()) {
//			mEventList.setVisibility(View.VISIBLE);
//			mNoPeriodLayout.setVisibility(View.INVISIBLE);
			updateEventList();
		} 
//		else {
//			// No location
//			mEventList.setVisibility(View.INVISIBLE);
//			mNoPeriodLayout.setVisibility(View.VISIBLE);
//			mMissingDataMessage.setText(R.string.no_location);

//			mMissingDataButton.setTag(String.format("%04d%02d%02d",
//					mDataProvider->getYear(), mDataProvider->getMonth(), 1));
//			mMissingDataButton.setText(String.format(mContext
//					.getResources().getString(R.string.download_location),
//					mDataProvider->getCityName()));

//			mMissingDataButton
//					.setOnClickListener(new View.OnClickListener() {
//						public void onClick(View v) {
//							String cityKey = PreferenceUtils
//									.getCityKey(mContext);
//							Downloader.getInstance(mContext)
//									.downloadLocation(mDataProvider,
//											cityKey,
//											mDataProvider->getCityName(),
//											new Downloader.Callback() {
//												public void callback(
//														boolean isSuccess) {
//													if (isSuccess) {
//														onPause();
//														onRestart();
//													}
//												}
//											});
//						}
//					});
//		}
	} 
//	else {
//		// No period
//		mEventList.setVisibility(View.INVISIBLE);
//		mNoPeriodLayout.setVisibility(View.VISIBLE);
//		mMissingDataMessage.setText(R.string.no_period);

//		final String periodStr = String.format("%04d%02d%02d",
//				mDataProvider->getYear(), mDataProvider->getMonth(), 1);
//		mMissingDataButton.setText(String.format(mContext.getResources()
//				.getString(R.string.buy_period), mDataProvider->getYear(),
//				mDataProvider->getMonth() + 1));

//		mMissingDataButton.setOnClickListener(new View.OnClickListener() {
//			public void onClick(View v) {
//				// buyPeriod(periodKey);
//				downloadPeriod(periodStr);
//			}
//		});
//	}
	updateTitle();
}
