package com.dorothy.v2ex.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.FlowLayout;
import com.dorothy.v2ex.http.V2EXHttpClient;
import com.dorothy.v2ex.http.V2EXSubscriberAdapter;
import com.dorothy.v2ex.models.NodeDetail;

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
        showProgressDialog();

        V2EXHttpClient.getAllNodes(this, new V2EXSubscriberAdapter<List<NodeDetail>>(this) {
            @Override
            public void onNext(List<NodeDetail> nodeDetails) {
                super.onNext(nodeDetails);
                dismissProgressDialog();
                mNodeList.clear();
                mNodeList.addAll(nodeDetails);
                addButton();
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                dismissProgressDialog();
            }
        });
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
