package com.dorothy.v2ex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.models.UserProfile;
import com.dorothy.v2ex.utils.UserCache;
import com.dorothy.v2ex.utils.V2EXCookieManager;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    //    V2EXCookieManager.clearCookie(this);
        if (2==2) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        mEtUsername = (EditText) findViewById(R.id.username);
        mEtPassword = (EditText) findViewById(R.id.password);
        mBtnLogin = (Button) findViewById(R.id.login);

        mEtUsername.addTextChangedListener(mTextWatcher);
        mEtPassword.addTextChangedListener(mTextWatcher);
        mBtnLogin.setOnClickListener(this);

    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            boolean isValid = !TextUtils.isEmpty(mEtUsername.getText()) && !TextUtils.isEmpty
                    (mEtPassword.getText());
            setLoginEnabled(isValid);
        }
    };

    private void setLoginEnabled(boolean isValid) {
        if (isValid) {
            mBtnLogin.setBackgroundResource(R.drawable.btn_primary_round_normal);
            mBtnLogin.setTextColor(getResources().getColor(R.color.login_text_normal));
            mBtnLogin.setEnabled(true);
        } else {
            mBtnLogin.setBackgroundResource(R.drawable.btn_primary_round_shadow);
            mBtnLogin.setTextColor(getResources().getColor(R.color.login_text_shadow));
            mBtnLogin.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        requestLoginPage();
    }

    /*
     * 首先获取登陆页面,从登陆页面中解析出 login 所需的field
     */
    private void requestLoginPage() {
        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.getLoginPage();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    String[] fields = V2EXHtmlParser.parseLoginField(response.body());
                    Map<String, String> paramsMap = new HashMap<>();
                    paramsMap.put(fields[0], mEtUsername.getText().toString().trim());
                    paramsMap.put(fields[1], mEtPassword.getText().toString().trim());
                    paramsMap.put("once", fields[2]);
                    paramsMap.put("next", "/");
                    doLogin(paramsMap);
                } else {

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void doLogin(Map<String, String> params) {
        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.login(params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    String htmlStr = response.body();
                    if (V2EXHtmlParser.isLoginSuccess(htmlStr)) {
                        List<String> cookies = response.headers().values("Set-Cookie");
                        StringBuilder sb = new StringBuilder();
                        for (String cookie : cookies) {
                            sb.append(cookie);
                        }
                        fetchUserProfile();

                    } else {
                        // Login Failure
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    private void fetchUserProfile() {
        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.getUserProfile();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    UserProfile userProfile = V2EXHtmlParser.parseUserProfile(response.body());
                    UserCache.userCache(LoginActivity.this, userProfile);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //TODO
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //TODO
            }
        });
    }
}
