package com.connectapp.user.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.Spouse;
import com.connectapp.user.util.Util;
import com.connectapp.user.view.DropDownViewForXML;
import com.connectapp.user.volley.PostWithJsonWebTask;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.ServerStringResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class VillageFormActivity extends AppCompatActivity implements ServerResponseCallback, View.OnClickListener {
    private Context mContext;
    private EditText et_name, et_idNo, et_phone, et_email, et_familyMemCount, et_religion, et_occupation;
    private DropDownViewForXML dropDown_idType, dropDown_gender, dropDown_role, dropDown_qualification, dropDown_incomeGroup;
    private TextView tv_dob, tv_dom, tv_languages, tv_imageProgress, tv_spouse;
    private ViewPager vp_selectedImages;
    private LinearLayout ll_spouse;
    // Volley
    private VolleyTaskManager volleyTaskManager;
    private ArrayList<Spouse> spouses = new ArrayList<>();
    //Intent Data
    private String stateName, districtName, villageName;

    private static final int DATE_DIALOG_ID = 316;
    private int year = 2000, day = 01, tempMonth = 01;
    private View selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_form);
        mContext = VillageFormActivity.this;
        stateName = getIntent().getStringExtra("state");
        districtName = getIntent().getStringExtra("district");
        villageName = getIntent().getStringExtra("village");
        initView();
        volleyTaskManager = new VolleyTaskManager(mContext);
    }

    /**
     * initialize UI components
     */
    private void initView() {
        et_name = (EditText) findViewById(R.id.et_name);
        et_idNo = (EditText) findViewById(R.id.et_idNo);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);
        et_familyMemCount = (EditText) findViewById(R.id.et_familyMemCount);
        et_religion = (EditText) findViewById(R.id.et_religion);
        et_occupation = (EditText) findViewById(R.id.et_occupation);

        tv_dob = (TextView) findViewById(R.id.tv_dob);
        tv_dom = (TextView) findViewById(R.id.tv_dom);
        tv_languages = (TextView) findViewById(R.id.tv_languages);
        tv_imageProgress = (TextView) findViewById(R.id.tv_imageProgress);
        tv_spouse = (TextView) findViewById(R.id.tv_spouse);

        dropDown_idType = (DropDownViewForXML) findViewById(R.id.dropDown_idType);
        dropDown_gender = (DropDownViewForXML) findViewById(R.id.dropDown_gender);
        dropDown_role = (DropDownViewForXML) findViewById(R.id.dropDown_role);
        dropDown_qualification = (DropDownViewForXML) findViewById(R.id.dropDown_qualification);
        dropDown_incomeGroup = (DropDownViewForXML) findViewById(R.id.dropDown_incomeGroup);

        vp_selectedImages = (ViewPager) findViewById(R.id.vp_selectedImages);

        ll_spouse = (LinearLayout) findViewById(R.id.ll_spouse);

        tv_dob.setOnClickListener(this);
        tv_dom.setOnClickListener(this);

        populateIdTypeDropdown();
        populateGenderDropdown();
        populateRoleDropdown();
        populateQualificationDropdown();
        populateIncomeDropdown();
    }

    private void populateIdTypeDropdown() {
        dropDown_idType.setText("");
        String[] countrynames = getResources().getStringArray(R.array.ref_id_type_array);
        dropDown_idType.setItems(countrynames);
    }

    private void populateGenderDropdown() {
        dropDown_gender.setText("");
        String[] countrynames = getResources().getStringArray(R.array.gender_array);
        dropDown_gender.setItems(countrynames);
    }

    private void populateRoleDropdown() {
        dropDown_role.setText("");
        String[] countrynames = getResources().getStringArray(R.array.role_array);
        dropDown_role.setItems(countrynames);
    }

    private void populateQualificationDropdown() {
        dropDown_qualification.setText("");
        String[] countrynames = getResources().getStringArray(R.array.qualification_array);
        dropDown_qualification.setItems(countrynames);
    }

    private void populateIncomeDropdown() {
        dropDown_incomeGroup.setText("");
        String[] countrynames = getResources().getStringArray(R.array.income_group_array);
        dropDown_incomeGroup.setItems(countrynames);
    }

    public void onSpouseAddClick(View view) {

    }

    public void onAddLangClick(View view) {

    }

    public void onCancelClicked(View view) {
        finish();
    }

    public void onPostClicked(View view) {

        validateAndPostFormData();

    }

    public void onCameraClicked(View view) {

    }


    private void validateAndPostFormData() {
        if (et_name.getText().toString().length() == 0) {
            Util.showMessageWithOk(VillageFormActivity.this, "Please fill the Name field!");
            return;
        } /*else if (dropDown_idType.getText().toString().length() == 0) {
            Util.showMessageWithOk(VillageFormActivity.this, "Please fill the Id Reference type field!");
            return;
        } else if (et_idNo.getText().toString().length() == 0) {
            Util.showMessageWithOk(VillageFormActivity.this, "Please fill the Id Reference number field!");
            return;
        }*/

        postFormData();
    }

    private void postFormData() {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("name", "" + et_name.getText().toString().trim());
        requestMap.put("state", "" + stateName);
        requestMap.put("district", "" + districtName);
        requestMap.put("village", "" + villageName);
        requestMap.put("idType", "" + dropDown_idType.getText().toString().trim());
        requestMap.put("idNo", "" + et_idNo.getText().toString().trim());
        requestMap.put("phone", "" + et_phone.getText().toString().trim());
        requestMap.put("email", "" + et_email.getText().toString().trim());
        requestMap.put("dob", "" + tv_dob.getText().toString().trim());
        requestMap.put("gender", "" + dropDown_gender.getText().toString().trim());
        requestMap.put("familyMembers", "" + et_familyMemCount.getText().toString().trim());
        requestMap.put("roles", "" + dropDown_role.getText().toString().trim());
        requestMap.put("qualification", "" + dropDown_qualification.getText().toString().trim());
        requestMap.put("religion", "" + et_religion.getText().toString().trim());
        requestMap.put("incomeGroup", "" + dropDown_incomeGroup.getText().toString().trim());
        requestMap.put("language", "" + tv_languages.getText().toString().trim());
        requestMap.put("occupation", "" + et_occupation.getText().toString().trim());
        //TODO Send Picture
        requestMap.put("picture", "");

        /*if (tv_dom.getText().toString().trim().length() > 0) {
            if (spouses.size() > 0) {
                //TODO Create JSON ARRAY
                // "dom": [{ "spouse":"BBB", "dob":"12/04/1980", "age":"38" }]
            } else {

            }
        } else {
            requestMap.put("dom", "");
        }*/
        requestMap.put("dom", "" + tv_dom.getText().toString().trim());
        String formData = new JSONObject(requestMap).toString().trim();
        Log.e("JSON", "JSON" + formData);
        PostWithJsonWebTask.sendDataString(formData, VillageFormActivity.this, new ServerStringResponseCallback() {
            @Override
            public void onSuccess(String resultJsonObject) {

            }

            @Override
            public void ErrorMsg(VolleyError error) {

            }
        }, true, Consts.VILLAGE_SUBMISSION_URL);
    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        Log.e("Server", "Response: " + resultJsonObject);
    }

    @Override
    public void onError() {

    }

    @Override
    public void onClick(View mView) {
        switch (mView.getId()) {
            case R.id.tv_dob:
                showDialog(DATE_DIALOG_ID);
                selectedView = mView;
                break;
            case R.id.tv_dom:
                showDialog(DATE_DIALOG_ID);
                selectedView = mView;
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // set date picker as current date
                return new DatePickerDialog(this, datePickerListener, year, tempMonth, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            year = selectedYear;
            tempMonth = selectedMonth;
            day = selectedDay;

            String dayPading, monthPadding;
            if (selectedDay < 10) {
                dayPading = "0" + String.valueOf(day);
            } else {
                dayPading = String.valueOf(day);
            }

            if (selectedMonth < 9) {
                monthPadding = "0" + String.valueOf(selectedMonth + 1);
            } else {
                monthPadding = String.valueOf(selectedMonth + 1);
            }

            String pickedDate = selectedYear + "-" + monthPadding + "-" + dayPading;
//			sendingDate = monthPadding + "/" + dayPading + "/" + selectedYear;
            ((TextView) selectedView).setText(pickedDate);
            Log.v("DatePickerDialog", pickedDate);

            if (selectedView.getId() == R.id.tv_dom) {
                Toast.makeText(mContext, "Add Spouse", Toast.LENGTH_SHORT).show();
                ll_spouse.setVisibility(View.VISIBLE);

            }
        }
    };


}
