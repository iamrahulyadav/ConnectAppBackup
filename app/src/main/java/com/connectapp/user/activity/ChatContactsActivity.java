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

    //List<Friend> friendList;

    public static final String USERS_CHILD = "users";
    // ParseFirebaseData pfbd;

    private ChatContactsAdapter mAdapter;
    private RecyclerView recyclerView;
    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mFirebaseDatabaseReference;

    private VolleyTaskManager volleyTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_contacts);
        mContext = ChatContactsActivity.this;
        initView();
        //friendList = new ArrayList<>();

        verifyUserLogin();
        //pfbd = new ParseFirebaseData(this);

        volleyTaskManager = new VolleyTaskManager(mContext);
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
            // startActivity(new Intent(this, SigninActivity.class));
            finish();
        } else {
            //userModel = new UserModel(mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString(), mFirebaseUser.getUid());
            //readMessagensFirebase();

            fetchChatUserContacts();
        }
    }


    public void bindView() {
        try {
            mAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    /**
     * Read collections chatmodel Firebase
     *//*
    private void readMessagensFirebase() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String totalData = dataSnapshot.getValue().toString();
                Log.e("FIREBASE", "DATA: " + totalData);
                mAdapter = new ChatContactsAdapter(mContext, pfbd.getUserList(totalData));
                recyclerView.setAdapter(mAdapter);

                mAdapter.setOnItemClickListener(new ChatContactsAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, Friend obj, int position) {
                        // ActivityChatDetails.navigate((ActivitySelectFriend) ActivitySelectFriend.this, findViewById(R.id.lyt_parent), obj);
                    }
                });

                bindView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Snackbar.make(getWindow().getDecorView(), "Could not connect", Snackbar.LENGTH_LONG).show();
            }
        });
    }*/
    private void fetchChatUserContacts() {


    }

    @Override
    public void onSuccess(JSONObject resultJsonObject) {

    }

    @Override
    public void onError() {

    }



}
