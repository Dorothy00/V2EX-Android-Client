package com.dorothy.v2ex.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewTopicActivity extends BaseActivity {

    private EditText mEtTitle;
    private EditText mEtContet;
    private Toolbar mToolbar;
    private String mNodeName = "bike";

    public static Intent newIntent(Activity activity, String tab) {
        Intent intent = new Intent(activity, NewTopicActivity.class);
        intent.putExtra("tab", tab);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_topic);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        String tab = getIntent().getStringExtra("tab");
        if (!TextUtils.isEmpty(tab)) {
            mToolbar.setTitle(tab);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEtTitle = (EditText) findViewById(R.id.edit_title);
        mEtContet = (EditText) findViewById(R.id.edit_content);
    }

    private void fetchNewTopicPage() {

        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<String> call = apiService.getNewTopicPage();
        showProgressDialogs("正在发布新话题...       ");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response != null && response.isSuccessful()) {
                    String once = V2EXHtmlParser.parseNewTopicOnce(response.body());
                    postNewTopic(once);
                } else {
                    showToast("主题发布失败");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dismissProgressDialog();
                if (t != null) {
                    showToast(t.getMessage() + " ");
                } else {
                    showToast("主题发布失败");
                }
            }
        });
    }

    private void postNewTopic(String once) {
        Retrofit retrofit = V2EXHttpClient.retrofit(this);
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Map<String, String> params = new HashMap<>();
        params.put("once", once);
        params.put("title", mEtTitle.getText().toString());
        params.put("content", mEtContet.getText().toString());
        params.put("node_name", mNodeName);
        Call<String> call = apiService.postNewTopic(params);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                dismissProgressDialog();
                if (response != null && response.isSuccessful()) {

                } else {
                    showToast("主题发布失败");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                dismissProgressDialog();
                if (t != null) {
                    showToast(t.getMessage());
                } else {
                    showToast("主题发布失败");
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_topic, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.menu_send:
                String title = mEtTitle.getText().toString();
                if (!TextUtils.isEmpty(title)) {
                    fetchNewTopicPage();
                }
                break;
            case R.id.menu_category:
                startActivity(new Intent(this, CategoryActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mEtTitle.getText().toString())) {
            super.onBackPressed();
        } else {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("放弃编辑");
            builder.setMessage("放弃此次编辑新主题?");
            builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
    }
}
