
package com.backtome.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {

    int ink = Color.rgb(37,49,47);
    int muted = Color.rgb(103,119,113);
    int green = Color.rgb(69,112,101);
    int cream = Color.rgb(244,245,241);

    LinearLayout root, content;
    android.content.SharedPreferences prefs;

    String[] tasks = {
        "لعبت أو سويت شيء لنفسي",
        "لاحظت الرغبة وما فتحت الحساب",
        "تركت الفكرة تمر بدون تحليل",
        "اهتميت بهدف أو بمظهري",
        "أخذت وقت بعيد عن مراقبة الناس",
        "رجعت لنشاطي حتى لو الفكرة موجودة"
    };

    CheckBox[] checks = new CheckBox[6];
    TextView scoreText;
    SeekBar urge;
    EditText note;
    int score = 0;

    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);

        prefs = getSharedPreferences("backtome", MODE_PRIVATE);

        getWindow().setStatusBarColor(cream);
        getWindow().setNavigationBarColor(cream);

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission("android.permission.POST_NOTIFICATIONS")
            != android.content.pm.PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{"android.permission.POST_NOTIFICATIONS"}, 7
            );
        }

        build();
    }

    int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(s);
        t.setTextSize(size);
        t.setTextColor(color);
        t.setGravity(Gravity.RIGHT);
        t.setTypeface(null, bold ? Typeface.BOLD : Typeface.NORMAL);
        t.setTextDirection(View.TEXT_DIRECTION_RTL);
        return t;
    }

    LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(1);
        return l;
    }

    void pad(View v, int a, int b, int c, int d) {
        v.setPadding(dp(a), dp(b), dp(c), dp(d));
    }

    GradientDrawable bg(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    void build() {

        ScrollView sc = new ScrollView(this);

        root = column();
        root.setBackgroundColor(cream);
        pad(root, 18, 18, 18, 28);

        sc.addView(root);
        setContentView(sc);

        TextView brand = text("BACK TO ME", 12, green, true);
        brand.setGravity(Gravity.LEFT);
        root.addView(brand);

        TextView heading = text("ارجع لحياتك، خطوة خطوة", 27, ink, true);
        heading.setGravity(Gravity.RIGHT);
        add(root, heading, 4, 0, 0, 3);

        add(root,
            text("مو مطلوب توقف الأفكار بالقوة. المطلوب تتعلم ما تمشي وراها.",
            14, muted, false),
            0, 0, 0, 16
        );

        LinearLayout card = card();
        root.addView(card);

        card.addView(text("تذكير اليوم", 16, green, true));

        add(card,
            text("وجود الفكرة ما يعني إنك لازم تتبعها أو تحلّها الآن.",
            17, ink, true),
            10, 0, 0, 0
        );

        add(root, text("مهمتك اليوم", 19, ink, true), 22, 0, 0, 8);

        for (int i = 0; i < tasks.length; i++) {

            final int x = i;

            CheckBox cb = new CheckBox(this);
            checks[i] = cb;

            cb.setText(tasks[i]);
            cb.setTextSize(15);
            cb.setTextColor(ink);

            cb.setButtonTintList(
                android.content.res.ColorStateList.valueOf(green)
            );

            cb.setTextDirection(View.TEXT_DIRECTION_RTL);
            cb.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            cb.setPadding(dp(4), dp(5), dp(4), dp(5));

            cb.setChecked(prefs.getBoolean(today() + "_" + i, false));

            cb.setOnCheckedChangeListener((v, c) ->
                prefs.edit().putBoolean(today() + "_" + x, c).apply()
            );

            root.addView(cb);
        }

        add(root, text("شدة الرغبة في التفقد", 18, ink, true),
            20, 0, 0, 5);

        scoreText = text("0 من 10", 14, green, true);
        root.addView(scoreText);

        urge = new SeekBar(this);
        urge.setMax(10);
        urge.setProgress(prefs.getInt(today() + "_urge", 0));

        score = urge.getProgress();
        scoreText.setText(score + " من 10");

        urge.setProgressTintList(
            android.content.res.ColorStateList.valueOf(green)
        );

        urge.setThumbTintList(
            android.content.res.ColorStateList.valueOf(green)
        );

        root.addView(urge);

        urge.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {

                public void onProgressChanged(SeekBar s, int p, boolean u) {
                    score = p;
                    scoreText.setText(p + " من 10");
                    prefs.edit().putInt(today() + "_urge", p).apply();
                }

                public void onStartTrackingTouch(SeekBar s) {}
                public void onStopTrackingTouch(SeekBar s) {}
            }
        );

        add(root, text("ملاحظة قصيرة (اختياري)", 17, ink, true),
            18, 0, 0, 6);

        note = new EditText(this);
        note.setTextSize(14);
        note.setTextColor(ink);
        note.setHint("وش صار؟ وش سويت بدل التفقد؟");
        note.setGravity(Gravity.RIGHT | Gravity.TOP);
        note.setMinLines(3);
        note.setBackground(bg(Color.WHITE, 16));
        pad(note, 12, 10, 12, 10);
        note.setText(prefs.getString(today() + "_note", ""));

        root.addView(note);

        Button save = button("حفظ يومي");
        root.addView(save);

        save.setOnClickListener(v -> {
            prefs.edit()
                .putString(today() + "_note", note.getText().toString())
                .apply();

            Toast.makeText(this,
                "انحفظ يومك على جهازك",
                Toast.LENGTH_SHORT
            ).show();
        });

        add(root, text("إذا جتك الرغبة الآن", 19, ink, true),
            22, 0, 0, 8);

        LinearLayout exercise = card();
        root.addView(exercise);

        add(exercise,
            text("1. سمّها: «هذه رغبة في التفقد، مو أمر لازم أنفذه»",
            15, ink, true), 0, 0, 0, 8);

        add(exercise,
            text("2. أجّل فتح الحساب 10 دقائق. لا تحاول تطرد الفكرة؛ خلّها موجودة.",
            15, ink, false), 0, 0, 0, 8);

        add(exercise,
            text("3. خلال الدقائق العشر، ارجع لشيء تختاره أنت: Roblox، شرب ماء، ترتيب شيء، أو مشي بسيط.",
            15, ink, false), 0, 0, 0, 8);

        add(exercise,
            text("4. لاحظ هل الرغبة تغيّرت، حتى لو ما اختفت. النجاح هو اختيارك، مو اختفاء الفكرة.",
            15, green, true), 0, 0, 0, 0);

        add(root, text("تذكيرات", 19, ink, true), 22, 0, 0, 8);

        Button reminders = button("فعّل / حدّث تذكيرات الصباح والظهر والليل");
        root.addView(reminders);

        reminders.setOnClickListener(v -> {
            schedule(this, 9, 0, 1);
            schedule(this, 15, 0, 2);
            schedule(this, 21, 0, 3);

            Toast.makeText(this,
                "تم ضبط 3 تذكيرات يومية تقريبًا: 9 ص، 3 م، 9 م",
                Toast.LENGTH_LONG
            ).show();
        });

        Button history = button("عرض ملخص آخر 7 أيام");
        root.addView(history);
        history.setOnClickListener(v -> showHistory());

        add(root,
            text("تذكّر: التقدم مو خط مستقيم، واليوم الصعب ما يلغي اللي قبله. التطبيق للمساندة الذاتية وليس تشخيصًا أو علاجًا. إذا الأفكار معطلة يومك، حاول تكلم شخص بالغ تثق فيه أو مختص.",
            12, muted, false),
            18, 0, 0, 0
        );
    }

    String today() {
        return new SimpleDateFormat("yyyyMMdd", Locale.US)
            .format(new Date());
    }

    LinearLayout card() {
        LinearLayout l = column();
        l.setBackground(bg(Color.WHITE, 18));
        pad(l, 16, 15, 16, 15);
        return l;
    }

    void add(LinearLayout p, View v, int t, int l, int r, int b) {
        LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(-1, -2);

        lp.setMargins(0, dp(t), 0, dp(b));
        p.addView(v, lp);
    }

    Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(14);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackground(bg(green, 14));
        b.setPadding(dp(8), dp(8), dp(8), dp(8));

        LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(-1, -2);

        lp.setMargins(0, dp(8), 0, 0);
        b.setLayoutParams(lp);

        return b;
    }

    void showHistory() {

        StringBuilder s = new StringBuilder();
        Calendar c = Calendar.getInstance();
        int total = 0;

        for (int d = 0; d < 7; d++) {

            String key = new SimpleDateFormat("yyyyMMdd", Locale.US)
                .format(c.getTime());

            int n = 0;

            for (int i = 0; i < 6; i++) {
                if (prefs.getBoolean(key + "_" + i, false)) n++;
            }

            total += n;

            s.append(new SimpleDateFormat("EEE dd/MM", Locale.getDefault())
                .format(c.getTime()))
                .append(" — ")
                .append(n)
                .append("/6 مهام")
                .append("\n");

            c.add(Calendar.DATE, -1);
        }

        new AlertDialog.Builder(this)
            .setTitle("ملخص آخر 7 أيام")
            .setMessage(s.toString() + "\nالمجموع: " + total + " من 42")
            .setPositiveButton("حسنًا", null)
            .show();
    }

    public static void schedule(Context c, int h, int m, int id) {

        AlarmManager am =
            (AlarmManager)c.getSystemService(ALARM_SERVICE);

        Intent i = new Intent(c, ReminderReceiver.class);
        i.putExtra("id", id);

        PendingIntent pi = PendingIntent.getBroadcast(
            c,
            id,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, h);
        cal.set(Calendar.MINUTE, m);
        cal.set(Calendar.SECOND, 0);

        if (cal.getTimeInMillis() <= System.currentTimeMillis()) {
            cal.add(Calendar.DATE, 1);
        }

        am.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            pi
        );
    }
}
