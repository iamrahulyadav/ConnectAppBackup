package com.connectapp.user.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.data.District;
import com.connectapp.user.data.State;
import com.connectapp.user.dropDownActivity.DistrictListActivity;
import com.connectapp.user.dropDownActivity.StateListActivity;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RevenueVillageActivity extends AppCompatActivity implements View.OnClickListener, ServerResponseCallback {

    private Context mContext;
    private TextView dropDownActivity_state;
    private TextView dropDownActivity_district;
    private EditText et_village_name;
    private static final int STATE_REQ_CODE = 3424;
    private static final int DISTRICT_REQ_CODE = 3425;
    private VolleyTaskManager volleyTaskManager;
    private ArrayList<District> districts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_village);
        mContext = RevenueVillageActivity.this;

        volleyTaskManager = new VolleyTaskManager(mContext);
        dropDownActivity_state = (TextView) findViewById(R.id.dropDownActivity_state);
        dropDownActivity_district = (TextView) findViewById(R.id.dropDownActivity_district);
        et_village_name = (EditText) findViewById(R.id.et_village_name);

        dropDownActivity_state.setOnClickListener(this);
        dropDownActivity_district.setOnClickListener(this);
    }

    public void onPersonClick(View view) {

    }

    public void onNextClick(View view) {
        String stateName = dropDownActivity_state.getText().toString().trim();
        String districtName = dropDownActivity_district.getText().toString().trim();
        String villageName = et_village_name.getText().toString().trim();
        if (TextUtils.isEmpty(stateName)) {
            Toast.makeText(mContext, "Please select a state.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(districtName)) {
            Toast.makeText(mContext, "Please select the district.", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(villageName)) {
            Toast.makeText(mContext, "Please enter the village name", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(mContext, VillageFormActivity.class);
        intent.putExtra("state", stateName);
        intent.putExtra("district", districtName);
        intent.putExtra("village", villageName);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.dropDownActivity_state:
                startActivityForResult(new Intent(this.mContext, StateListActivity.class), STATE_REQ_CODE);
                break;

            case R.id.dropDownActivity_district:
                String selectedState = dropDownActivity_state.getText().toString().trim();
                if (TextUtils.isEmpty(selectedState)) {
                    Toast.makeText(mContext, "Please select a state.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!TextUtils.isEmpty(selectedState) && districts.size() < 1) {
                    Toast.makeText(mContext, "Districts not found.", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(this.mContext, DistrictListActivity.class);
                intent.putExtra("district", districts);
                startActivityForResult(intent, DISTRICT_REQ_CODE);
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STATE_REQ_CODE && resultCode == Activity.RESULT_OK) {
            State selectedState = (State) data.getSerializableExtra(StateListActivity.STATE);
            dropDownActivity_state.setText("" + selectedState.getName().trim());
            String stateCode = selectedState.getCode();
            dropDownActivity_district.setText("");
            getDistrictFromStateCode(stateCode);

        } else if (requestCode == DISTRICT_REQ_CODE && resultCode == Activity.RESULT_OK) {
            dropDownActivity_district.setText(data.getStringExtra(DistrictListActivity.DISTRICT_NAME));
        }
    }

    private void getDistrictFromStateCode(String stateCode) {
        volleyTaskManager.doGet("http://districts.gov.in/doi_service/rest.php/district/" + stateCode + "/json");
    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        Log.e("getDistrict", "District" + resultJsonObject);
        JSONArray categoriesArray = resultJsonObject.optJSONArray("categories");
        ArrayList<District> districts = new ArrayList<>();
        for (int i = 0; i < categoriesArray.length(); i++) {
            JSONObject districtJSON = categoriesArray.optJSONObject(i);
            JSONObject category = districtJSON.optJSONObject("category");
            Log.e("category", "category" + category);
            if (category.has("district_name")) {
                District district = new District();
                district.setDistrictID(category.optString("district_id"));
                district.setDistrictName(category.optString("district_name"));
                districts.add(district);
            }
        }
        this.districts = districts;
    }

    @Override
    public void onError() {

    }
}
