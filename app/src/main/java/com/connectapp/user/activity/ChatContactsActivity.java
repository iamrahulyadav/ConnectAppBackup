package com.connectapp.user.activity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.adapter.ChatContactsAdapter;
import com.connectapp.user.data.ParseFirebaseData;
import com.connectapp.user.model.Friend;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatContactsActivity extends AppCompatActivity implements ServerResponseCallback {

    private Context mContext;

    private RecyclerView recyclerView;

    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private VolleyTaskManager volleyTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_contacts);
        // Assign context
        mContext = ChatContactsActivity.this;
        // Iniialize volley class
        volleyTaskManager = new VolleyTaskManager(mContext);
        // Initialize UI components
        initView();
        // Verify Firebase Login
        verifyUserLogin();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewContacts);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    /**
     * Verify user is logged in
     */
    private void verifyUserLogin() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // User could not be verified.
            // startActivity(new Intent(this, SigninActivity.class));
            Util.showCallBackMessageWithOkCallback(mContext, "Your gmail account cannot be verified at the moment.", new AlertDialogCallBack() {
                @Override
                public void onSubmit() {
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });

        } else {
            // User verified.
            fetchChatUserContacts();
        }
    }


    private void fetchChatUserContacts() {

        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
        volleyTaskManager.doFetchChatContacts(requestMap, true);

    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {

        if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {


        } else {
            Util.showCallBackMessageWithOkCallback(ChatContactsActivity.this, "Something went wrong. Please try again later.", new AlertDialogCallBack() {
                @Override
                public void onSubmit() {
                    // Exit
                    finish();
                }

                @Override
                public void onCancel() {

                }
            });
        }


    }

    @Override
    public void onError() {

    }


}
