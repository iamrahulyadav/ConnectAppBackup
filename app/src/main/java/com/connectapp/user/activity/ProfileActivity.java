package com.connectapp.user.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.PostWithJsonWebTask;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.ServerResponseStringCallback;
import com.connectapp.user.volley.VolleyTaskManager;

import org.json.JSONObject;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity implements ServerResponseCallback {

    private Context mContext;
    private TextView tv_name, tv_phone, tv_email;
    private EditText et_phone, et_email;
    private VolleyTaskManager volleyTaskManager;
    private boolean isFetchProfileService = false, isUpdateProfileService = false;
    private String phoneNumber = "", emailID = "";

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mContext = ProfileActivity.this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("  Edit Profile");

        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_email = findViewById(R.id.tv_email);

        et_phone = (EditText) findViewById(R.id.et_phone);
        et_email = (EditText) findViewById(R.id.et_email);

        volleyTaskManager = new VolleyTaskManager(mContext);

        fetchUserProfile();

    }

    private void fetchUserProfile() {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
        volleyTaskManager.doFetchUserProfile(hashMap, true);
        isFetchProfileService = true;
    }

    public void onEditPhoneClick(View view) {
        //tv_phone.setVisibility(View.GONE);
        //et_phone.setVisibility(View.VISIBLE);
        showEditPhoneDialog();


    }

    public void onEditEmailClick(View view) {
        // tv_email.setVisibility(View.GONE);
        // et_email.setVisibility(View.VISIBLE);
        showEditEmailDialog();
    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {
        Log.e("Response", "Response: " + resultJsonObject);
        if (isFetchProfileService) {
            isFetchProfileService = false;
            if (resultJsonObject.optString("code").equalsIgnoreCase("200")) {
                JSONObject data = resultJsonObject.optJSONObject("data");
                String name = data.optString("userName");
                String phone = data.optString("Phone");
                String email = data.optString("email");
                emailID = email;
                phoneNumber = phone;
                updateView(name, phone, email);


            } else if (resultJsonObject.optString("code").equalsIgnoreCase("404")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else if (resultJsonObject.optString("code").equalsIgnoreCase("400")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            }

        } else if (isUpdateProfileService) {
            isUpdateProfileService = false;
            if (resultJsonObject.optString("code").equalsIgnoreCase("200")) {

            } else if (resultJsonObject.optString("code").equalsIgnoreCase("404")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else if (resultJsonObject.optString("code").equalsIgnoreCase("400")) {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            } else {
                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
            }
        }
    }

    private void updateView(String name, String phone, String email) {

        tv_name.setText("" + name);
        tv_phone.setText("+91 " + phone);
        et_phone.setText("+91 " + phone);
        tv_email.setText("" + email);
        et_email.setText("" + email);
    }

    @Override
    public void onError() {

    }

    private void showEditEmailDialog() {

        final Dialog customDialog = new Dialog(mContext);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_edit_email, null);

        final EditText et_newEmail = (EditText) view.findViewById(R.id.et_newEmail);
        final Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String newEmail = et_newEmail.getText().toString().trim();

                if (TextUtils.isEmpty(newEmail)) {
                    Toast.makeText(mContext, "Please enter an email id.", Toast.LENGTH_LONG).show();
                    return;
                } else if (!Util.isValidEmail(newEmail)) {

                    Toast.makeText(mContext, "Please enter a valid e-mail id.", Toast.LENGTH_LONG).show();
                    return;
                }

                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
                requestMap.put("email", "" + newEmail);
                requestMap.put("userPhone", "" + phoneNumber);

                // volleyTaskManager.doUpdateUserProfile(requestMap, true);

                PostWithJsonWebTask.callPostWithJsonWebtask(ProfileActivity.this, Consts.BASE_URL + "updateProfile", requestMap, new ServerResponseStringCallback() {
                    @Override
                    public void onSuccess(String resultJsonObject) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultJsonObject);
                            if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                                emailID = newEmail;
                                Toast.makeText(mContext, "Email ID Ipdated successfully", Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.optString("code").trim().equalsIgnoreCase("400")) {
                                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
                            } else {
                                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorMsg(VolleyError error) {

                    }
                }, true, Request.Method.POST);
            }
        });

        customDialog.setCancelable(true);
        customDialog.setContentView(view);
        customDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(customDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        customDialog.show();
        customDialog.getWindow().setAttributes(lp);

    }

    private void showEditPhoneDialog() {

        final Dialog customDialog = new Dialog(mContext);

        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_edit_phone, null);

        final EditText et_newPho = (EditText) view.findViewById(R.id.et_newPho);
        Button btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String newPhone = et_newPho.getText().toString().trim();

                if (TextUtils.isEmpty(newPhone)) {
                    Toast.makeText(mContext, "Please enter a phone number", Toast.LENGTH_LONG).show();
                    return;
                } else if (newPhone.length() < 10) {
                    Toast.makeText(mContext, "Please enter a correct phone number.", Toast.LENGTH_LONG).show();
                    return;
                }

                HashMap<String, String> requestMap = new HashMap<>();
                requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
                requestMap.put("email", "" + emailID);
                requestMap.put("userPhone", "" + newPhone.trim());
                //  requestMap.put("organization_id", "" + organizationId.trim());
                PostWithJsonWebTask.callPostWithJsonWebtask(ProfileActivity.this, Consts.BASE_URL + "updateProfile", requestMap, new ServerResponseStringCallback() {
                    @Override
                    public void onSuccess(String resultJsonObject) {
                        try {
                            JSONObject jsonObject = new JSONObject(resultJsonObject);
                            if (jsonObject.optString("code").equalsIgnoreCase("200")) {
                                phoneNumber = newPhone;
                                Toast.makeText(mContext, "Phone number updated successfully.", Toast.LENGTH_SHORT).show();
                            } else if (jsonObject.optString("code").trim().equalsIgnoreCase("400")) {
                                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
                            } else {
                                Util.showMessageWithOk(ProfileActivity.this, "Something went wrong! Please try again.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void ErrorMsg(VolleyError error) {

                    }
                }, true, Request.Method.POST);
            }
        });

        customDialog.setCancelable(true);
        customDialog.setContentView(view);
        customDialog.setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(customDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        customDialog.show();
        customDialog.getWindow().setAttributes(lp);

    }
}