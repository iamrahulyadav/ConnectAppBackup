package com.connectapp.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.connectapp.user.R;
import com.connectapp.user.adapter.ChatFirebaseAdapter;
import com.connectapp.user.callback.ClickListenerChatFirebase;
import com.connectapp.user.data.ChatModel;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.data.UserModel;
import com.connectapp.user.util.Util;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Calendar;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

/**
 * Created by ritwik on 3/11/17.
 */

public class ChatActivity extends AppCompatActivity implements View.OnClickListener, ClickListenerChatFirebase, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;

    //Firebase and GoogleApiClient
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private DatabaseReference mFirebaseDatabaseReference;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Views UI
    private RecyclerView rvListMessage;
    private LinearLayoutManager mLinearLayoutManager;
    private ImageView btSendMessage, btEmoji, buttonAttachFile;
    private EmojiconEditText edMessage;
    private View contentRoot;
    private EmojIconActions emojIcon;
    //  private LinearLayout llWelcomeMsg;
    //CLass Model
    private UserModel userModel;

    static final String CHAT_REFERENCE = "chatmodel";

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
        setContentView(R.layout.activity_chat_main);
        mContext = ChatActivity.this;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Chat");

        if (!Util.isInternetAvailable(this)) {
            Util.initToast(this, "You do not have an internet connection.");
            finish();
        } else {
            bindViews();
            verifyUserLogin();
            mGoogleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this).addApi(Auth.GOOGLE_SIGN_IN_API).build();
        }
    }

    /**
     * Link views with Java API
     */
    private void bindViews() {
        contentRoot = findViewById(R.id.contentRoot);
        edMessage = (EmojiconEditText) findViewById(R.id.editTextMessage);
        btSendMessage = (ImageView) findViewById(R.id.buttonMessage);
        buttonAttachFile = (ImageView) findViewById(R.id.buttonAttachFile);
        btSendMessage.setOnClickListener(this);
        buttonAttachFile.setOnClickListener(this);
        btEmoji = (ImageView) findViewById(R.id.buttonEmoji);
        //llWelcomeMsg = (LinearLayout) findViewById(R.id.llWelcomeMsg);
        emojIcon = new EmojIconActions(this, contentRoot, edMessage, btEmoji);
        emojIcon.ShowEmojIcon();
        rvListMessage = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonMessage:
                sendMessageFirebase();
                break;
            case R.id.buttonAttachFile:
                //showAttachOptions();
                break;
        }
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
            userModel = new UserModel(mFirebaseUser.getDisplayName(), mFirebaseUser.getPhotoUrl().toString(), mFirebaseUser.getUid());
            readMessagensFirebase();
        }
    }

    /**
     * Read collections chatmodel Firebase
     */
    private void readMessagensFirebase() {


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Log.e("Data", "Json: " + mFirebaseDatabaseReference.child("" + CHAT_REFERENCE).child("chatModel"));
        mFirebaseDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("Data", "Json: " + dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        firebaseAdapter = new ChatFirebaseAdapter(mFirebaseDatabaseReference.child(CHAT_REFERENCE), userModel.getName(), this, Util.fetchUserClass(mContext).getEmail(), true);

        firebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    rvListMessage.scrollToPosition(positionStart);
                }

               /* if (!(friendlyMessageCount > 0))
                    llWelcomeMsg.setVisibility(View.VISIBLE);

                else
                    llWelcomeMsg.setVisibility(View.GONE);*/
            }
        });
        rvListMessage.setLayoutManager(mLinearLayoutManager);
        rvListMessage.setAdapter(firebaseAdapter);
    }

    /**
     * Send simple text msg to chat
     */
    private void sendMessageFirebase() {
        UserClass userClass = Util.fetchUserClass(mContext);
        ChatModel model = new ChatModel(userModel, edMessage.getText().toString(), userClass.adminFirebaseId, userClass.firebaseId, userClass.firebaseInstanceId, userClass.getEmail(), Calendar.getInstance().getTime().getTime() + "", null);
        mFirebaseDatabaseReference.child(CHAT_REFERENCE).push().setValue(model);
        edMessage.setText(null);
    }

    ChatFirebaseAdapter firebaseAdapter;

    @Override
    public void clickImageChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

    @Override
    public void clickImageMapChat(View view, int position, String latitude, String longitude) {

    }

    @Override
    public void clickFileChat(View view, int position, String nameUser, String urlPhotoUser, String urlPhotoClick) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
