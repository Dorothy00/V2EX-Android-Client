package com.dorothy.v2ex.activity;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.dorothy.v2ex.R;
import com.dorothy.v2ex.View.FlowLayout;
import com.dorothy.v2ex.http.V2EXApiService;
import com.dorothy.v2ex.models.NodeDetail;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
        showProgressDialogs();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(V2EXApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build();
        V2EXApiService apiService = retrofit.create(V2EXApiService.class);
        Call<List<NodeDetail>> call = apiService.getAllNodes();
        call.enqueue(new Callback<List<NodeDetail>>() {
            @Override
            public void onResponse(Call<List<NodeDetail>> call, Response<List<NodeDetail>>
                    response) {
                dismissProgressDialog();
                if (response != null && response.isSuccessful()) {
                    mNodeList.clear();
                    mNodeList.addAll(response.body());
                    addButton();
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<NodeDetail>> call, Throwable t) {

            }
        });
    }

    private void addButton() {
        for (NodeDetail nodeDetail : mNodeList) {
            Button button = new Button(this);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup
                    .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(lp);
            button.setText(nodeDetail.getName());
            mFlowLayout.addView(button);
        }
    }
}
