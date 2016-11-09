package com.dorothy.v2ex.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.FlowLayout;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.http.V2EXSubscriberAdapter;
import com.dorothy.v2ex.models.NodeDetail;
import com.dorothy.v2ex.utils.FileUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends BaseActivity {

    private FlowLayout mFlowLayout;
    private List<NodeDetail> mNodeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mFlowLayout = (FlowLayout) findViewById(R.id.flow_layout);
        fetchAllNode();
    }


    private void fetchAllNode() {
        String nodesStr = (String) FileUtil.readObject(this);
        if (nodesStr != null) {
            List<NodeDetail> nodeDetails = new Gson().fromJson(nodesStr, new
                    TypeToken<List<NodeDetail>>() {
                    }.getType());
            renderView(nodeDetails);
            return;
        }

        showProgressDialog();
        V2EXHttpClient.getAllNodes(this, new V2EXSubscriberAdapter<List<NodeDetail>>(this) {
            @Override
            public void onNext(List<NodeDetail> nodeDetails) {
                super.onNext(nodeDetails);
                dismissProgressDialog();
                renderView(nodeDetails);
                FileUtil.deleteObject(CategoryActivity.this);
                FileUtil.saveObject(CategoryActivity.this, new Gson().toJson(nodeDetails));
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissProgressDialog();
            }
        });
    }

    private void renderView(List<NodeDetail> nodeDetails) {
        mNodeList.clear();
        mNodeList.addAll(nodeDetails);
        addButton();
    }

    private void addButton() {
        for (NodeDetail nodeDetail : mNodeList) {
            View view = getLayoutInflater().inflate(R.layout.btn_category, null);
            Button button = (Button) view.findViewById(R.id.btn_category);
            button.setText(nodeDetail.getName());
            mFlowLayout.addView(view);
        }
    }
}
