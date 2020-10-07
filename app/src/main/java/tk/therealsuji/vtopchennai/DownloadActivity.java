package tk.therealsuji.vtopchennai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DownloadActivity extends AppCompatActivity {
    VTOP vtop = new VTOP();
    SharedPreferences sharedPreferences;

    public void signIn(View view) {
        String username = sharedPreferences.getString("username", null);
        String password = sharedPreferences.getString("password", null);

        EditText captchaView = findViewById(R.id.captcha);
        String captcha = captchaView.getText().toString();
        vtop.signIn(username, password, captcha);
    }

    public void downloadTimetable(View view) {
        vtop.downloadTimetable();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        sharedPreferences = this.getSharedPreferences("tk.therealsuji.vtopchennai", Context.MODE_PRIVATE);

        WebView vtopPortal = findViewById(R.id.vtopPortal);
        ImageView captcha = findViewById(R.id.captchaCode);

        LinearLayout captchaLayout = findViewById(R.id.captchaLayout);
        LinearLayout loadingLayout = findViewById(R.id.loadingLayout);
        LinearLayout semesterLayout = findViewById(R.id.semesterLayout);

        TextView loading = findViewById(R.id.loading);
        loading.setText(getString(R.string.loading));

        Spinner selectSemester = (Spinner) findViewById(R.id.selectSemester);

        vtop.setVtop(this, vtopPortal, captcha, captchaLayout, loadingLayout, loading, semesterLayout, selectSemester, sharedPreferences);

        final Button submit = findViewById(R.id.submit);
        submit.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        submit.animate().scaleX(0.93f).scaleY(0.93f).setDuration(50);
                        submit.setAlpha(0.85f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        submit.animate().scaleX(1f).scaleY(1f).setDuration(50);
                        submit.setAlpha(1f);
                        break;
                }
                return false;
            }
        });

        final Button select = findViewById(R.id.select);
        select.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        select.animate().scaleX(0.93f).scaleY(0.93f).setDuration(50);
                        select.setAlpha(0.85f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        select.animate().scaleX(1f).scaleY(1f).setDuration(50);
                        select.setAlpha(1f);
                        break;
                }
                return false;
            }
        });
    }
}