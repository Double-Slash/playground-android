package com.doubleslash.playground;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.doubleslash.playground.databinding.ActivityLoginBinding;
import com.doubleslash.playground.register.RegisterActivity1;
import com.doubleslash.playground.retrofit.RetrofitClient;
import com.doubleslash.playground.retrofit.dto.response.Sign_in_responseDTO;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private RetrofitClient retrofitClient;
    private String user_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Check if we have read or write permission
        final int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE );
        }
        initUI();
    }

    private void initUI() {
        SharedPreferences auto = getSharedPreferences("playground", Activity.MODE_PRIVATE);
        user_token=auto.getString("user_token",null);
        retrofitClient = RetrofitClient.getInstance();

        binding.loginBtn.setOnClickListener(v -> {
            int result=0;
            if(user_token!=null){
                result= retrofitClient.post_autologin(user_token, FirebaseInstanceId.getInstance().getToken());
                if (result == 1) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    // 토큰 생성하기
                }
                else{
                    Log.e("error","result=0");
                }
            }
            if (binding.emailEdit.getText().toString() != null && binding.passwordEdit.getText().toString() != null) {
                Sign_in_responseDTO sign_in_responseDTO = retrofitClient.post_login(binding.emailEdit.getText().toString(), binding.passwordEdit.getText().toString());
                System.out.println(result);
                if (sign_in_responseDTO.getResult() == 1) {
                    SharedPreferences.Editor autoLogin = auto.edit();
                    autoLogin.putString("user_token",sign_in_responseDTO.getToken());
                    autoLogin.commit();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("email", binding.emailEdit.getText().toString());
                    startActivity(intent);
                    // 토큰 생성하기
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(task -> {
                                if (!task.isSuccessful()) {
                                    Log.w("bjyoo", "Fetching FCM registration token failed");
                                    return;
                                }

                                // Get new FCM registration token
                                String token = task.getResult();
                                Log.w("bjyoo", "Fetching FCM registration token success!");

                                MyFirebaseMessagingService service = new MyFirebaseMessagingService();
                                service.onNewToken(Objects.requireNonNull(token));
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "아이디와 패스워드를 다시 한 번 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.findIdPwBtn.setOnClickListener(v -> {
            // 미완성
            // 아이디/비밀번호 찾기
        });

        binding.registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity1.class);
            startActivity(intent);
        });
    }
}