package com.connectapp.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.dropDownActivity.PictureCategoryActivity;
import com.connectapp.user.dropDownActivity.StateCodeActivity;

public class RevenueVillageActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private TextView dropDownActivity_state;
    private TextView dropDownActivity_district;
    private EditText et_village_name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_village);
        mContext = RevenueVillageActivity.this;

        dropDownActivity_state = (TextView) findViewById(R.id.dropDownActivity_state);
        dropDownActivity_district = (TextView) findViewById(R.id.dropDownActivity_district);
        et_village_name = (EditText) findViewById(R.id.et_village_name);

        dropDownActivity_state.setOnClickListener(this);
    }

    public void onPersonClick(View view) {

    }

    public void onNextClick(View view) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.dropDownActivity_state:
                startActivityForResult(new Intent(this.mContext, PictureCategoryActivity.class), 12);
                break;

            case R.id.dropDownActivity_district:
                startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);
                break;

            default:
                break;
        }
    }
}
