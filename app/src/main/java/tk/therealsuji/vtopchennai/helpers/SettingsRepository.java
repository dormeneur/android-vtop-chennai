package tk.therealsuji.vtopchennai.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.FragmentActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import tk.therealsuji.vtopchennai.R;
import tk.therealsuji.vtopchennai.activities.WebViewActivity;
import tk.therealsuji.vtopchennai.fragments.RecyclerViewFragment;
import tk.therealsuji.vtopchennai.fragments.ViewPagerFragment;
import tk.therealsuji.vtopchennai.models.Timetable;
import tk.therealsuji.vtopchennai.receivers.NotificationReceiver;

public class SettingsRepository {
    public static final String APP_BASE_URL = "https://vtopchennai.therealsuji.tk";
    public static final String APP_ABOUT_URL = APP_BASE_URL + "/about.json";
    public static final String APP_PRIVACY_URL = APP_BASE_URL + "/privacy-policy";
    public static final String APP_FAQ_URL = APP_BASE_URL + "/frequently-asked-questions";

    public static final String DEVELOPER_BASE_URL = "https://therealsuji.tk";

    public static final String GITHUB_BASE_URL = "https://github.com/therealsujitk/android-vtop-chennai";
    public static final String GITHUB_FEATURE_URL = GITHUB_BASE_URL + "/issues";
    public static final String GITHUB_ISSUE_URL = GITHUB_BASE_URL + "/issues";

    public static final String VTOP_BASE_URL = "https://vtopcc.vit.ac.in/vtop";

    public static final int THEME_DAY = 0;
    public static final int THEME_NIGHT = 1;
    public static final int THEME_SYSTEM_DAY = 2;
    public static final int THEME_SYSTEM_NIGHT = 3;

    public static final int NOTIFICATION_ID_TIMETABLE = 1;
    public static final int NOTIFICATION_ID_VTOP_DOWNLOAD = 1;

    public static int getTheme(Context context) {
        String appearance = getSharedPreferences(context).getString("appearance", "system");
        int currentNightMode = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (appearance.equals("dark")) {
            return THEME_NIGHT;
        } else if (appearance.equals("light")) {
            return THEME_DAY;
        } else if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            return THEME_SYSTEM_NIGHT;
        } else {
            return THEME_SYSTEM_DAY;
        }
    }

    public static boolean isSignedIn(Context context) {
        return getSharedPreferences(context).getBoolean("isSignedIn", false);
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("tk.therealsuji.vtopchennai", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getEncryptedSharedPreferences(Context context) {
        try {
            MasterKey masterKey = new MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            return EncryptedSharedPreferences.create(
                    context,
                    "credentials",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            return null;
        }
    }

    public static void openDownloadPage(Context context) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(APP_BASE_URL));
        context.startActivity(browserIntent);
    }

    public static void openRecyclerViewFragment(FragmentActivity fragmentActivity, int titleId, int contentType) {
        RecyclerViewFragment recyclerViewFragment = new RecyclerViewFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("title_id", titleId);
        bundle.putInt("content_type", contentType);

        recyclerViewFragment.setArguments(bundle);

        fragmentActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .add(R.id.frame_layout_fragment_container, recyclerViewFragment)
                .addToBackStack(null)
                .commit();
    }

    public static void openViewPagerFragment(FragmentActivity fragmentActivity, int titleId, int contentType) {
        ViewPagerFragment viewPagerFragment = new ViewPagerFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("title_id", titleId);
        bundle.putInt("content_type", contentType);

        viewPagerFragment.setArguments(bundle);

        fragmentActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, 0, 0, R.anim.slide_out_right)
                .add(R.id.frame_layout_fragment_container, viewPagerFragment)
                .addToBackStack(null)
                .commit();
    }

    public static void openWebViewActivity(Context context, String title, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    public static void openBrowser(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    public static String getSystemFormattedTime(Context context, String time) throws ParseException {
        if (DateFormat.is24HourFormat(context)) {
            return time;
        } else {
            SimpleDateFormat hour12 = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
            SimpleDateFormat hour24 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

            return hour12.format(Objects.requireNonNull(hour24.parse(time)));
        }
    }

    public static Bitmap getBitmapFromVectorDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888
        );

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static void clearTimetableNotifications(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);

        int alarmCount = sharedPreferences.getInt("alarmCount", 0);
        while (alarmCount >= 0) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, --alarmCount, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.cancel(pendingIntent);
        }

        sharedPreferences.edit().remove("alarmCount").apply();
    }

    public static void setTimetableNotifications(Context context, Timetable timetable) throws ParseException {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Intent notificationIntent = new Intent(context, NotificationReceiver.class);
        SharedPreferences sharedPreferences = getSharedPreferences(context);

        SimpleDateFormat hour24 = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        int alarmCount = sharedPreferences.getInt("alarmCount", 0);
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        Integer[] slots = {
                timetable.sunday,
                timetable.monday,
                timetable.tuesday,
                timetable.wednesday,
                timetable.thursday,
                timetable.friday,
                timetable.saturday
        };

        Date today = dateFormat.parse(dateFormat.format(calendar.getTime()));
        Date now = hour24.parse(hour24.format(calendar.getTime()));

        for (int i = 0; i < slots.length; ++i) {
            if (slots[i] == null) {
                continue;
            }

            assert today != null;
            Calendar alarm = Calendar.getInstance();
            alarm.setTime(today);

            if (i == day) {
                Date startTime = hour24.parse(timetable.startTime);
                assert startTime != null;

                if (startTime.before(now)) {
                    alarm.add(Calendar.DATE, 7);
                }
            } else if (i > day) {
                alarm.add(Calendar.DATE, i - day);
            } else {
                alarm.add(Calendar.DATE, 7 - day + i);
            }

            alarm.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timetable.startTime.split(":")[0]));
            alarm.set(Calendar.MINUTE, Integer.parseInt(timetable.startTime.split(":")[1]));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmCount++, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

            alarm.add(Calendar.MINUTE, -30);
            pendingIntent = PendingIntent.getBroadcast(context, alarmCount++, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }

        sharedPreferences.edit().putInt("alarmCount", alarmCount).apply();
    }
}
