package com.doubleslash.playground.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.doubleslash.playground.R;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity3 extends AppCompatActivity {
    private RelativeLayout numberLayout;
    private EditText emailEdit;
    private EditText numberEdit;
    private TextView okBtn;
    private Button requestNumberBtn;
    private Button nextBtn;

    int time, min, sec;   // 타이머를 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register3);

        initUI();
    }

    private void initUI() {
        numberLayout = findViewById(R.id.number_layout);
        emailEdit = findViewById(R.id.email_edit);
        numberEdit = findViewById(R.id.number_edit);
        okBtn = findViewById(R.id.ok_btn);
        requestNumberBtn = findViewById(R.id.request_number_btn);
        nextBtn = findViewById(R.id.next_btn);

        requestNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerStart();
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                nextBtn.setBackgroundResource(R.drawable.ic_button);
                nextBtn.setTextColor(getResources().getColor(R.color.white));
                nextBtn.setEnabled(true);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity4.class);
                startActivity(intent);
            }
        });
    }

    // 인증 번호 요청 후 타이머 구현
    private void timerStart() {
        numberLayout.setVisibility(View.VISIBLE);
        requestNumberBtn.setBackgroundResource(R.drawable.ic_disabled_blue_lined_button);
        requestNumberBtn.setTextColor(getResources().getColor(R.color.sub_gray));
        requestNumberBtn.setEnabled(false);

        final Handler handler = new Handler();
        Runnable addRunnable = new Runnable() {
            @Override
            public void run() {
                time = 120;

                while (time > 0) {
                    try {
                        min = time / 60;
                        sec = time % 60;
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (sec < 10) {
                                    requestNumberBtn.setText(min + ":0" + sec);
                                } else {
                                    requestNumberBtn.setText(min + ":" + sec);
                                }
                            }
                        });
                        Thread.sleep(1000);
                        time--;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(addRunnable);
    }
}