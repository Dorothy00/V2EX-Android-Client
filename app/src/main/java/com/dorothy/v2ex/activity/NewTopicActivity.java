package com.dorothy.v2ex.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.http.NetWorkUnavaliableException;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.http.V2EXSubscriberAdapter;
import com.dorothy.v2ex.utils.V2EXHtmlParser;

import java.util.HashMap;
import java.util.Map;

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
        showProgressDialog("正在发布新话题...       ");
        InputMethodManager imm = (InputMethodManager) getSystemService
                (Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtContet.getWindowToken(), 0);
        V2EXHttpClient.getNewTopicPage(this, new V2EXSubscriberAdapter<String>(this) {
            @Override
            public void onNext(String s) {
                dismissProgressDialog();
                String once = V2EXHtmlParser.parseNewTopicOnce(s);
                postNewTopic(once);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissProgressDialog();
            }
        });
    }

    private void postNewTopic(String once) {
        Map<String, String> params = new HashMap<>();
        params.put("once", once);
        params.put("title", mEtTitle.getText().toString());
        params.put("content", mEtContet.getText().toString());
        params.put("node_name", mNodeName);
        //params.put("node_name", "sandbox");

        V2EXHttpClient.postNewTopic(this, params, new V2EXSubscriberAdapter<String>(this) {
            @Override
            public void onNext(String s) {
                super.onNext(s);
                if (V2EXHtmlParser.isPostNewTopicSuccess(s)) {
                    showToast("发布成功");
                } else {
                    showLongToast(V2EXHtmlParser.parseNewTopicProblem(s));
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof NetWorkUnavaliableException) {
                    showToast("网络未连接");
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
