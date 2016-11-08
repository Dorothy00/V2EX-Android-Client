package com.dorothy.v2ex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.http.V2EXSubscriberAdapter;
import com.dorothy.v2ex.models.UserProfile;
import com.dorothy.v2ex.utils.UserCache;
import com.dorothy.v2ex.utils.V2EXCookieManager;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (!V2EXCookieManager.isExpired(this)) {
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

        showProgressDialog("正在登录...");

        V2EXHttpClient.getLoginPage(this, new V2EXSubscriberAdapter<String>(this) {
            @Override
            public void onNext(String s) {
                super.onNext(s);
                String[] fields = V2EXHtmlParser.parseLoginField(s);
                Map<String, String> paramsMap = new HashMap<>();
                paramsMap.put(fields[0], mEtUsername.getText().toString().trim());
                paramsMap.put(fields[1], mEtPassword.getText().toString().trim());
                paramsMap.put("once", fields[2]);
                paramsMap.put("next", "/");
                doLogin(paramsMap);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                V2EXCookieManager.clearCookie(LoginActivity.this);
                dismissProgressDialog();
            }
        });
    }

    private void doLogin(Map<String, String> params) {

        V2EXHttpClient.login(this, params, new V2EXSubscriberAdapter<String>(this) {
            @Override
            public void onNext(String s) {
                super.onNext(s);
                if (V2EXHtmlParser.isLoginSuccess(s)) {
                    fetchUserProfile();

                } else {
                    String errMsg = V2EXHtmlParser.parseLoginErrorMsg(s);
                    showToast("登录失败: " + errMsg);
                    V2EXCookieManager.clearCookie(LoginActivity.this);
                    dismissProgressDialog();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissProgressDialog();
                V2EXCookieManager.clearCookie(LoginActivity.this);
            }
        });
    }

    private void fetchUserProfile() {

        V2EXHttpClient.getUserProfile(this, new V2EXSubscriberAdapter<String>(this) {
            @Override
            public void onNext(String s) {
                super.onNext(s);
                dismissProgressDialog();
                UserProfile userProfile = V2EXHtmlParser.parseUserProfile(s);
                UserCache.userCache(LoginActivity.this, userProfile);
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissProgressDialog();
                V2EXCookieManager.clearCookie(LoginActivity.this);
            }
        });
    }
}
