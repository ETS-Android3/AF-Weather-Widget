package net.veierland.aix;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

public class AixServiceReceiver extends BroadcastReceiver {
	private static final String TAG = "AixServiceReceiver";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			NetworkInfo networkInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
			if (settings.getBoolean("needwifi", false)) {
				Editor editor = settings.edit();
				editor.putBoolean("needwifi", false);
				editor.commit();
				if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
					sendIntent(context);
				}
			}
		} else {
			sendIntent(context);
		}
	}
	
	private void sendIntent(Context context) {
		long updateTime = System.currentTimeMillis() + 10 * DateUtils.SECOND_IN_MILLIS;
		
		Intent updateIntent = new Intent(AixService.ACTION_UPDATE_ALL);
		updateIntent.setClass(context, AixService.class);
		
		PendingIntent pendingIntent = PendingIntent.getService(context, 0, updateIntent, 0);
		AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, updateTime, pendingIntent);
	}

}
