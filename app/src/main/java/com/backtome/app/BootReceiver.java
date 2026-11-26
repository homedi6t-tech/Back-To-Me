package com.backtome.app;
import android.content.*;
public class BootReceiver extends BroadcastReceiver {
 public void onReceive(Context c,Intent i){if(Intent.ACTION_BOOT_COMPLETED.equals(i.getAction())){MainActivity.schedule(c,9,0,1);MainActivity.schedule(c,15,0,2);MainActivity.schedule(c,21,0,3);}}
}
