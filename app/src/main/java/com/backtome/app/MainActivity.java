
package com.backtome.app;

import android.app.*;
import android.os.*;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import android.content.res.ColorStateList;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {

    int ink = Color.rgb(35, 61, 51);
    int muted = Color.rgb(112, 132, 121);
    int green = Color.rgb(69, 132, 105);
    int lightGreen = Color.rgb(235, 245, 238);
    int white = Color.WHITE;
    int line = Color.rgb(225, 237, 228);

    LinearLayout root;
    SharedPreferences prefs;

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

        getWindow().setStatusBarColor(white);
        getWindow().setNavigationBarColor(white);

        if (Build.VERSION.SDK_INT >= 23) {
            getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR |
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            );
        }

        if (Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission("android.permission.POST_NOTIFICATIONS")
            != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                new String[]{"android.permission.POST_NOTIFICATIONS"}, 7
            );
        }

        build();
    }

    int dp(float n) {
        return (int)(n * getResources().getDisplayMetrics().density + .5f);
    }

    GradientDrawable bg(int color, int radius) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(radius));
        return g;
    }

    GradientDrawable borderBg(int color, int radius) {
        GradientDrawable g = bg(color, radius);
        g.setStroke(dp(1), line);
        return g;
    }

    void pad(View v, int a, int b, int c, int d) {
        v.setPadding(dp(a), dp(b), dp(c), dp(d));
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
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }

    void add(LinearLayout p, View v, int top, int left, int right, int bottom) {
        LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(-1, -2);

        lp.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        p.addView(v, lp);
    }

    LinearLayout card() {
        LinearLayout l = column();
        l.setBackground(borderBg(white, 20));
        pad(l, 17, 16, 17, 16);
        return l;
    }

    TextView sectionTitle(String s) {
        TextView t = text(s, 19, ink, true);
        return t;
    }

    Button button(String s) {
        Button b = new Button(this);
        b.setText(s);
        b.setTextSize(14);
        b.setTextColor(white);
        b.setAllCaps(false);
        b.setTypeface(null, Typeface.BOLD);
        b.setBackground(bg(green, 15));
        b.setPadding(dp(12), dp(10), dp(12), dp(10));

        LinearLayout.LayoutParams lp =
            new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, dp(9), 0, 0);
        b.setLayoutParams(lp);

        return b;
    }

    void build() {

        ScrollView sc = new ScrollView(this);
        sc.setFillViewport(true);
        sc.setBackgroundColor(white);

        root = column();
        root.setBackgroundColor(white);
        pad(root, 19, 20, 19, 30);

        sc.addView(root);
        setContentView(sc);

        // Header and logo
        LinearLayout header = column();
        header.setGravity(Gravity.CENTER);
        header.setBackground(bg(lightGreen, 24));
        pad(header, 16, 19, 16, 19);
        root.addView(header);

        ImageView logo = new ImageView(this);

        int logoId = getResources().getIdentifier(
            "back_to_me_logo", "drawable", getPackageName()
        );

        if (logoId != 0) {
            logo.setImageResource(logoId);
        }

        logo.setAdjustViewBounds(true);
        logo.setScaleType(ImageView.ScaleType.FIT_CENTER);

        LinearLayout.LayoutParams logoParams =
            new LinearLayout.LayoutParams(dp(145), dp(145));
        logoParams.gravity = Gravity.CENTER;
        header.addView(logo, logoParams);

        TextView brand = text("BACK TO ME", 13, green, true);
        brand.setGravity(Gravity.CENTER);
        add(header, brand, 8, 0, 0, 0);

        TextView heading = text("ارجع لحياتك، خطوة خطوة", 24, ink, true);
        heading.setGravity(Gravity.CENTER);
        add(header, heading, 9, 0, 0, 0);

        TextView intro = text(
            "مساحتك الخاصة للتركيز على نفسك من جديد.",
            13, muted, false
        );
        intro.setGravity(Gravity.CENTER);
        add(header, intro, 7, 0, 0, 0);

        // Daily reminder
        LinearLayout reminder = card();
        add(root, reminder, 18, 0, 0, 0);

        TextView reminderTitle = text("تذكير اليوم", 16, green, true);
        reminder.addView(reminderTitle);

        add(reminder,
            text(
                "وجود الفكرة ما يعني إنك لازم تتبعها أو تحلّها الآن. تقدر تتركها تمر وترجع لشيء يهمك.",
                15, ink, false
            ),
            9, 0, 0, 0
        );

        // Daily tasks
        add(root, sectionTitle("مهمتك اليوم"), 24, 2, 0, 9);

        LinearLayout taskCard = card();
        root.addView(taskCard);

        for (int i = 0; i < tasks.length; i++) {

            final int x = i;

            CheckBox cb = new CheckBox(this);
            checks[i] = cb;

            cb.setText(tasks[i]);
            cb.setTextSize(14);
            cb.setTextColor(ink);
            cb.setButtonTintList(
                ColorStateList.valueOf(green)
            );
            cb.setTextDirection(View.TEXT_DIRECTION_RTL);
            cb.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            cb.setPadding(dp(4), dp(8), dp(4), dp(8));

            cb.setChecked(
                prefs.getBoolean(today() + "_" + i, false)
            );

            cb.setOnCheckedChangeListener((v, checked) ->
                prefs.edit()
                    .putBoolean(today() + "_" + x, checked)
                    .apply()
            );

            taskCard.addView(cb);
        }

        // Urge scale
        add(root, sectionTitle("شدة الرغبة في التفقد"), 23, 2, 0, 9);

        LinearLayout urgeCard = card();
        root.addView(urgeCard);

        TextView urgeInfo = text(
            "قيّم شعورك الآن من 0 إلى 10",
            13, muted, false
        );
        urgeCard.addView(urgeInfo);

        scoreText = text("0 من 10", 17, green, true);
        add(urgeCard, scoreText, 12, 0, 0, 4);

        urge = new SeekBar(this);
        urge.setMax(10);
        urge.setProgress(prefs.getInt(today() + "_urge", 0));
        score = urge.getProgress();

        scoreText.setText(score + " من 10");

        urge.setProgressTintList(ColorStateList.valueOf(green));
        urge.setThumbTintList(ColorStateList.valueOf(green));

        add(urgeCard, urge, 5, 0, 0, 0);

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

        // Notes
        add(root, sectionTitle("ملاحظتي اليوم"), 23, 2, 0, 9);

        LinearLayout noteCard = card();
        root.addView(noteCard);

        TextView noteInfo = text(
            "اكتب أي شيء تبي تتذكره عن يومك.",
            13, muted, false
        );
        noteCard.addView(noteInfo);

        note = new EditText(this);
        note.setTextSize(14);
        note.setTextColor(ink);
        note.setHintTextColor(muted);
        note.setHint("وش صار؟ وش سويت بدل التفقد؟");
        note.setGravity(Gravity.RIGHT | Gravity.TOP);
        note.setTextDirection(View.TEXT_DIRECTION_RTL);
        note.setMinLines(3);
        note.setBackground(bg(lightGreen, 14));
        pad(note, 12, 12, 12, 12);
        note.setText(prefs.getString(today() + "_note", ""));

        add(noteCard, note, 12, 0, 0, 0);

        Button save = button("حفظ يومي");
        noteCard.addView(save);

        save.setOnClickListener(v -> {
            prefs.edit()
                .putString(today() + "_note", note.getText().toString())
                .apply();

            Toast.makeText(this,
                "انحفظ يومك على جهازك",
                Toast.LENGTH_SHORT
            ).show();
        });

        // Exercise
        add(root, sectionTitle("إذا جتك الرغبة الآن"), 23, 2, 0, 9);

        LinearLayout exercise = card();
        exercise.setBackground(bg(lightGreen, 20));
        root.addView(exercise);

        add(exercise,
            text(
                "1. سمّها: هذه رغبة في التفقد، مو أمر لازم أنفذه.",
                15, ink, true
            ), 0, 0, 0, 12
        );

        add(exercise,
            text(
                "2. أجّل فتح الحساب 10 دقائق. لا تحاول تطرد الفكرة؛ خلّها موجودة.",
                14, ink, false
            ), 0, 0, 0, 12
        );

        add(exercise,
            text(
                "3. خلال الدقائق العشر، ارجع لشيء تختاره أنت: Roblox، شرب ماء، ترتيب شيء، أو نشاط تحبه.",
                14, ink, false
            ), 0, 0, 0, 12
        );

        add(exercise,
            text(
                "4. لاحظ هل الرغبة تغيرت، حتى لو ما اختفت. النجاح هو اختيارك، مو اختفاء الفكرة.",
                14, green, true
            ), 0, 0, 0, 0
        );

        // Reminders
        add(root, sectionTitle("تذكيراتك اليومية"), 23, 2, 0, 9);

        LinearLayout reminderCard = card();
        root.addView(reminderCard);

        add(reminderCard,
            text(
                "ثلاث رسائل قصيرة تساعدك تتذكر نفسك خلال اليوم.",
                13, muted, false
            ), 0, 0, 0, 4
        );

        Button reminders = button("تفعيل تذكيرات الصباح والظهر والليل");
        reminderCard.addView(reminders);

        reminders.setOnClickListener(v -> {
            schedule(this, 9, 0, 1);
            schedule(this, 15, 0, 2);
            schedule(this, 21, 0, 3);

            Toast.makeText(this,
                "تم ضبط التذكيرات اليومية",
                Toast.LENGTH_LONG
            ).show();
        });

        // History
        add(root, sectionTitle("تقدمك"), 23, 2, 0, 9);

        LinearLayout historyCard = card();
        root.addView(historyCard);

        add(historyCard,
            text(
                "راجع خطواتك خلال آخر سبعة أيام. لا تقارن نفسك بأحد.",
                13, muted, false
            ), 0, 0, 0, 4
        );

        Button history = button("عرض ملخص آخر 7 أيام");
        historyCard.addView(history);
        history.setOnClickListener(v -> showHistory());

        // Footer
        TextView footer = text(
            "تذكّر: التقدم مو خط مستقيم، واليوم الصعب ما يلغي اللي قبله.\n\nالتطبيق للمساندة الذاتية وليس تشخيصًا أو علاجًا. إذا الأفكار معطلة يومك، حاول تكلم شخص بالغ تثق فيه أو مختص.",
            12, muted, false
        );

        footer.setGravity(Gravity.CENTER);
        add(root, footer, 25, 5, 5, 10);
    }

    String today() {
        return new SimpleDateFormat("yyyyMMdd", Locale.US)
            .format(new Date());
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
