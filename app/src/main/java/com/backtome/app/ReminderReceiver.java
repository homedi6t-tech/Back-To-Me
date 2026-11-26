package com.backtome.app;
import android.app.*; import android.content.*; import android.os.Build;
public class ReminderReceiver extends BroadcastReceiver {
 public void onReceive(Context c,Intent i){
  String[] msgs={"خذ دقيقة لنفسك. مو لازم تراجع أي حساب الآن.","ارجع لشيء يهمك أنت، حتى لو كانت الفكرة موجودة.","قيّم يومك بلطف: هل اخترت حياتك ولو مرة؟"};
  int id=i.getIntExtra("id",1); String msg=msgs[Math.max(0,Math.min(2,id-1))];
  NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26)nm.createNotificationChannel(new NotificationChannel("backtome","تذكيرات Back to Me",NotificationManager.IMPORTANCE_DEFAULT));
  Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(c,"backtome"):new Notification.Builder(c);
  b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("Back to Me").setContentText(msg).setAutoCancel(true);
  nm.notify(id,b.build());
 }
}
