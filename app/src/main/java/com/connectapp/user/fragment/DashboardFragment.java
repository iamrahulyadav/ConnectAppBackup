package com.connectapp.user.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.connectapp.user.R;
import com.connectapp.user.activity.ChatContactsActivity;
import com.connectapp.user.activity.ComingSoonActivity;
import com.connectapp.user.activity.EkalChaptersActivity;
import com.connectapp.user.activity.KeyWordActivity;
import com.connectapp.user.activity.RathFormActivity;
import com.connectapp.user.activity.ResourcesActivity;
import com.connectapp.user.activity.RevenueVillageActivity;
import com.connectapp.user.activity.SchoolFormActivity;
import com.connectapp.user.adapter.ThreadListAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.Keyword;
import com.connectapp.user.data.Thread;
import com.connectapp.user.data.Threads;
import com.connectapp.user.data.User;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.db.MembersDB;
import com.connectapp.user.members.MembersDirectory;
import com.connectapp.user.model.UserChatClass;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.util.AlertDialogCallBack;
import com.connectapp.user.util.Util;
import com.connectapp.user.volley.PostWithJsonWebTask;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.ServerResponseStringCallback;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DashboardFragment extends Fragment implements DBConstants, GoogleApiClient.OnConnectionFailedListener {

    private ListView lv_menu;
    private Context mContext;

    private String TAG = "";
    private ArrayList<Thread> threadList = new ArrayList<Thread>();

    private boolean isFetchThreads = false;

    //Firebase and GoogleApiClient
    public static GoogleApiClient mGoogleApiClient;
    public static FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    public static final int RC_SIGN_IN = 9001;

    private ProgressDialog mProgressDialog;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInAccount acct;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mContext = this.getActivity();
        initView(view);
        return view;
    }

    private void initView(View view) {

        lv_menu = (ListView) view.findViewById(R.id.lv_menu);

        TAG = mContext.getClass().getSimpleName();

        if (Util.isInternetAvailable(mContext) && !(Util.fetchUserClass(mContext).isOfflineLogin)) {

            HashMap<String, String> requestMap = new HashMap<String, String>();
            isFetchThreads = true;

            //volleyTaskManager.doPostFetchThreads(requestMap, true);

            PostWithJsonWebTask.callPostWithStringReqWebtask(getActivity(), Consts.FETCH_THREADS_URL, requestMap,
                    new ServerResponseStringCallback() {

                        @Override
                        public void onSuccess(String resultJsonObject) {
                            getThreads(resultJsonObject);
                        }

                        @Override
                        public void ErrorMsg(VolleyError error) {

                        }
                    }, true, Request.Method.POST);

        } else {
            if (Util.fetchOfflineKeywordsThreads(mContext) != null) {
                threadList = Util.fetchOfflineKeywordsThreads(mContext).getThreads();
                updateListUI(threadList);
            }
        }
        //if (!Util.fetchUserClass(mContext).isOfflineLogin)

        // TODO -- Uncomment initializeFirebaseComponents() to enable Firebase initialization
        // initializeFirebaseComponents();

    }

    private void initializeFirebaseComponents() {

        //GOOGLE LOGIN

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage((FragmentActivity) getActivity(), 1, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuthListener = new FirebaseAuth.AuthStateListener() {


            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    String name = user.getDisplayName();
                    Toast.makeText(mContext, "" + user.getDisplayName(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "something went wrong", Toast.LENGTH_LONG).show();
                }


            }
        };
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Loading...");
    }

    private void getThreads(String resultJsonString) {

        Log.d(TAG, "" + resultJsonString);
        try {
            JSONObject resultJsonObject = new JSONObject(resultJsonString);
            if (isFetchThreads) {
                isFetchThreads = false;

                if (resultJsonObject.toString() != null && !resultJsonObject.toString().trim().isEmpty()) {
                    threadList.clear();
                    JSONArray resultArray = resultJsonObject.optJSONArray("threads");

                    for (int i = 0; i < resultArray.length(); i++) {
                        Thread item = new Thread();
                        item.setThreadID(resultArray.optJSONObject(i).optString("tid"));
                        item.setThreadName(resultArray.optJSONObject(i).optString("tname"));
                        if (item.getThreadName().equalsIgnoreCase("Rath")) {
                            item.setThreadImage(R.drawable.ic_rath);
                        }
                        Log.v("Keywords", "Keywords: " + resultArray.optJSONObject(i).optString("keywords"));
                        JSONArray keywordsArray = resultArray.optJSONObject(i).optJSONArray("keywords");
                        ArrayList<Keyword> keywordList = new ArrayList<Keyword>();

                        if (keywordsArray != null) {
                            for (int j = 0; j < keywordsArray.length(); j++) {
                                Keyword keyword = new Keyword();
                                keyword.setId(keywordsArray.optJSONObject(j).optString("id"));
                                keyword.setValue(keywordsArray.optJSONObject(j).optString("value"));
                                keywordList.add(keyword);

                            }
                        }
                        item.setKeywords(keywordList);
                        threadList.add(item);
                    }

                    Thread threadResources = new Thread();
                    threadResources.setThreadID("resources");
                    threadResources.setThreadName("Resources");
                    threadResources.setThreadImage(R.drawable.ic_resources_bg);
                    Thread threadMembersDirectory = new Thread();
                    threadMembersDirectory.setThreadID("membersDirectory");
                    threadMembersDirectory.setThreadName("Members Directory");
                    threadMembersDirectory.setThreadImage(R.drawable.ic_members);
                    Thread threadRevenueVillage = new Thread();
                    threadRevenueVillage.setThreadID("revenueVillage");
                    threadRevenueVillage.setThreadName("Revenue Village");
                    threadRevenueVillage.setThreadImage(R.drawable.bg_village);
                    Thread threadEkalPrayash = new Thread();
                    threadEkalPrayash.setThreadID("ekalPrayash");
                    threadEkalPrayash.setThreadName("Ekal Prayash");
                    threadEkalPrayash.setThreadImage(R.drawable.ic_ekal_prayash_bg);
                   /* Thread threadChat = new Thread();
                    threadChat.setThreadID("chat");
                    threadChat.setThreadName("ConnectApp Chat");
                    threadChat.setThreadImage(R.drawable.ic_chat_bg);*/
                    threadList.add(threadResources);
                    threadList.add(threadMembersDirectory);
                    threadList.add(threadRevenueVillage);
                    threadList.add(threadEkalPrayash);
                    // threadList.add(threadChat);
                    saveThreads(threadList);
                    updateListUI(threadList);

                } else {
                    Toast.makeText(mContext, " Request failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveThreads(ArrayList<Thread> threadList) {
        Log.d("saveThreads", "Size: " + threadList.size());
        System.out.println("saveThreads Size: " + threadList.size());
        Threads threads = new Threads();
        threads.setThreads(threadList);
        Util.saveOfflineKeywordsThreads(mContext, threads);

    }

    private void updateListUI(final ArrayList<Thread> threadList) {
        Log.d("updateListUI", "Size: " + threadList.size());
        System.out.println("updateListUI Size: " + threadList.size());
        ThreadListAdapter adapter = new ThreadListAdapter(mContext, threadList);
        lv_menu.setAdapter(adapter);
        lv_menu.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (threadList.get(position).getThreadID().trim().equalsIgnoreCase("6")) {

                    Intent intent = new Intent(mContext, SchoolFormActivity.class);
                    intent.putExtra("thread", threadList.get(position));
                    startActivity(intent);

                } else if (threadList.get(position).getThreadID().trim().equalsIgnoreCase("7")) {
                    Intent intent = new Intent(mContext, RathFormActivity.class);
                    intent.putExtra("thread", threadList.get(position));
                    startActivity(intent);
                    //Toast.makeText(mContext, "Coming Soon....", Toast.LENGTH_SHORT).show();
                } else if (threadList.get(position).getThreadName().equalsIgnoreCase("Members Directory")) {

                    Intent intent = new Intent(mContext, EkalChaptersActivity.class);
                    intent.putExtra("thread", threadList.get(position));
                    startActivity(intent);
                  /*  UserClass userClass = Util.fetchUserClass(mContext);
                    boolean isMembersDirectoryEmpty = new MembersDB().isMembersDirectoryEmpty(mContext);
                    Log.e("isMembersDirectoryEmpty", "isMembersDirectoryEmpty: " + isMembersDirectoryEmpty);
                    if (isMembersDirectoryEmpty) {
                        userClass.setIsMembersDirectoryComplete(false);
                        userClass.setCurrentCityIndex(-1);
                        userClass.setIsFirstTimeAccess(true);
                        userClass.setCurrentMemberCount(0);
                        Util.saveUserClass(mContext, userClass);
                    }
                    if (userClass.getIsMembersDirectoryComplete()) {
                        Intent intent = new Intent(mContext, MembersSHSSDirectory.class);
                        intent.putExtra("thread", threadList.get(position));
                        startActivity(intent);
                    } else {
                        if (Util.isInternetAvailable(mContext)) {
                            Intent intent = new Intent(mContext, MembersSHSSDirectory.class);
                            intent.putExtra("thread", threadList.get(position));
                            startActivity(intent);

                        } else {
                            Toast.makeText(mContext, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                        }
                    }*/

                } else if (threadList.get(position).getThreadName().equalsIgnoreCase("Revenue Village")) {

                    Intent intent = new Intent(mContext, RevenueVillageActivity.class);
                    //  intent.putExtra("thread", threadList.get(position));
                    startActivity(intent);
                    // Toast.makeText(mContext, "Revenue Village", Toast.LENGTH_SHORT).show();

                } else if (threadList.get(position).getThreadName().equalsIgnoreCase("Ekal Prayash")) {

                   /* Snackbar sb = Snackbar.make(view, "Ekal Prayash", Snackbar.LENGTH_LONG).setAction("Action", null);
                    sb.getView().setBackgroundColor(Color.GRAY);
                    sb.show();*/
                    Intent intent = new Intent(mContext, ComingSoonActivity.class);
                    intent.putExtra("thread", threadList.get(position));
                    startActivity(intent);

                } else if (threadList.get(position).getThreadName().equalsIgnoreCase("Resources")) {

                    DashboardFragment.this.startActivity(new Intent(DashboardFragment.this.mContext, ResourcesActivity.class));

                } else if (threadList.get(position).getThreadName().equalsIgnoreCase("ConnectApp Chat")) {
                    if (!Util.fetchUserClass(mContext).isOfflineLogin)
                        verifyUserLogin();
                    else
                        Util.showMessageWithOk(getActivity(), "Chat not available on offline mode.");

                }
                /*else {

                    openKeyWordsActivity(position);
                }*/

            }

        });
    }

    private void signOutFromFirebase() {
        mFirebaseAuth.signOut();
        // Google sign out
        //Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    private void openKeyWordsActivity(int position) {

        Intent intent = new Intent(mContext, KeyWordActivity.class);
        Thread thread = new Thread();
        thread = threadList.get(position);
        intent.putExtra("threadAndKeywords", thread);
        startActivity(intent);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("SignIn", "onConnectionFailed:" + connectionResult);
        Util.initToast(getActivity(), "Google Play Services error.");
        hideProgressDialog();
    }

    public void showProgressDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("------>> onActivityResult CALLED  >>---------------");
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                Toast.makeText(mContext, "Google Account Verified.", Toast.LENGTH_SHORT).show();
                GoogleSignInAccount account = result.getSignInAccount();

                showProgressDialog();
                firebaseAuthWithGoogle(account);
            } else {
                Log.e("Signin", "Google Sign In failed.");
                Toast.makeText(mContext, "Google Sign In failed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            //Pass the activity result back to the Facebook SDK
            // mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        this.acct = acct;
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        hideProgressDialog();
                        if (!task.isSuccessful()) {

                            Log.e(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(mContext, "Authentication failed", Toast.LENGTH_SHORT).show();
                        } else {

                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            addUserToDatabase(FirebaseAuth.getInstance().getCurrentUser());
                            String instanceId = FirebaseInstanceId.getInstance().getToken();

                            // Call update user API to update firebase id, email etc.
                            HashMap<String, String> requestMap = new HashMap<>();
                            requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
                            requestMap.put("emailID", "" + acct.getEmail());
                            requestMap.put("imageUrl", "" + mFirebaseAuth.getCurrentUser().getPhotoUrl());
                            requestMap.put("firebaseID", "" + mFirebaseAuth.getCurrentUser().getUid());
                            requestMap.put("firebaseInstanceID", "" + acct.getId());
                            // requestMap.put("deviceToken", "" + instanceId);
                            Log.e("firebaseId", "U Id: " + mFirebaseAuth.getCurrentUser().getUid());
                            Log.e("Request", "" + new JSONObject(requestMap));
                            // volleyTaskManager.doRegistration(requestMap, true);
                            //Toast.makeText(mContext,"Logging in! Please wait...",Toast.LENGTH_SHORT).show();
                            PostWithJsonWebTask.callPostWithJsonObjectWebtask(getActivity(), Consts.UPDATE_CHAT_PROFILE, requestMap, new ServerResponseCallback() {
                                @Override
                                public void onSuccess(JSONObject resultJsonObject) {
                                    Log.e("onSuccess", "resultJsonObject: " + resultJsonObject);
                                    if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
                                        // Save userChat details
                                        UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
                                        if (userChatClass == null)
                                            userChatClass = new UserChatClass();

                                        userChatClass.displayName = "" + mFirebaseAuth.getCurrentUser().getDisplayName();
                                        userChatClass.profileUrl = "" + mFirebaseAuth.getCurrentUser().getPhotoUrl();
                                        userChatClass.setEmail("" + mFirebaseAuth.getCurrentUser().getEmail());
                                        userChatClass.firebaseId = "" + mFirebaseAuth.getCurrentUser().getUid();
                                        userChatClass.setUserId(Util.fetchUserClass(mContext).getUserId());
                                        String instanceId = FirebaseInstanceId.getInstance().getToken();
                                        userChatClass.firebaseInstanceId = instanceId;
                                        Util.saveUserChatClass(mContext, userChatClass);
                                        // Goto ChatContactsActivity
                                        Intent intent = new Intent(mContext, ChatContactsActivity.class);
                                        startActivity(intent);

                                    } else {
                                        Util.showMessageWithOk(getActivity(), "Something went wrong and please try again.");
                                    }
                                }

                                @Override
                                public void onError() {

                                }
                            }, true, Request.Method.POST);

                        }
                    }
                });
    }


    // Add user to firebase User list [Table Name ==>> users]
    private void addUserToDatabase(FirebaseUser firebaseUser) {
        User user = new User(
                firebaseUser.getDisplayName(),
                firebaseUser.getEmail(),
                firebaseUser.getUid(),
                firebaseUser.getPhotoUrl() == null ? "" : firebaseUser.getPhotoUrl().toString()
        );

        mDatabase.child("users")
                .child(user.getUid()).setValue(user);

        String instanceId = FirebaseInstanceId.getInstance().getToken();
        if (instanceId != null) {
            mDatabase.child("users")
                    .child(firebaseUser.getUid())
                    .child("instanceId")
                    .setValue(instanceId);
        }
    }


    /**
     * Verify user is logged in
     */
    private void verifyUserLogin() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // User could not be verified.
            signOutFromFirebase();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);

        } else {
            // User verified.
           /* //User already logged in
            UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
            if (userChatClass == null)
                userChatClass = new UserChatClass();

            userChatClass.displayName = "" + mFirebaseAuth.getCurrentUser().getDisplayName();
            userChatClass.profileUrl = "" + mFirebaseAuth.getCurrentUser().getPhotoUrl();
            userChatClass.setEmail("" + mFirebaseAuth.getCurrentUser().getEmail());
            userChatClass.firebaseId = "" + mFirebaseAuth.getCurrentUser().getUid();
            userChatClass.setUserId(Util.fetchUserClass(mContext).getUserId());
            Util.saveUserChatClass(mContext, userChatClass);*/

            openChatPage();
        }
    }

    private void openChatPage() {
        {

            mDatabase = FirebaseDatabase.getInstance().getReference();
            addUserToDatabase(FirebaseAuth.getInstance().getCurrentUser());
            String instanceId = FirebaseInstanceId.getInstance().getToken();

            // Call update user API to update firebase id, email etc.
            HashMap<String, String> requestMap = new HashMap<>();
            requestMap.put("userID", "" + Util.fetchUserClass(mContext).getUserId());
            requestMap.put("emailID", "" + mFirebaseAuth.getCurrentUser().getEmail());
            requestMap.put("imageUrl", "" + mFirebaseAuth.getCurrentUser().getPhotoUrl());
            requestMap.put("firebaseID", "" + mFirebaseAuth.getCurrentUser().getUid());
            requestMap.put("firebaseInstanceID", "" + FirebaseInstanceId.getInstance().getToken());
            // requestMap.put("deviceToken", "" + instanceId);
            Log.e("firebaseId", "U Id: " + mFirebaseAuth.getCurrentUser().getUid());
            Log.e("Request", "" + new JSONObject(requestMap));
            // volleyTaskManager.doRegistration(requestMap, true);
            //Toast.makeText(mContext,"Logging in! Please wait...",Toast.LENGTH_SHORT).show();
            PostWithJsonWebTask.callPostWithJsonObjectWebtask(getActivity(), Consts.UPDATE_CHAT_PROFILE, requestMap, new ServerResponseCallback() {
                @Override
                public void onSuccess(JSONObject resultJsonObject) {
                    Log.e("onSuccess", "resultJsonObject: " + resultJsonObject);
                    if (resultJsonObject.optString("code").trim().equalsIgnoreCase("200")) {
                        // Save userChat details
                        UserChatClass userChatClass = Util.fetchUserChatClass(mContext);
                        if (userChatClass == null)
                            userChatClass = new UserChatClass();

                        userChatClass.displayName = "" + mFirebaseAuth.getCurrentUser().getDisplayName();
                        userChatClass.profileUrl = "" + mFirebaseAuth.getCurrentUser().getPhotoUrl();
                        userChatClass.setEmail("" + mFirebaseAuth.getCurrentUser().getEmail());
                        userChatClass.firebaseId = "" + mFirebaseAuth.getCurrentUser().getUid();
                        userChatClass.setUserId(Util.fetchUserClass(mContext).getUserId());
                        String instanceId = FirebaseInstanceId.getInstance().getToken();
                        userChatClass.firebaseInstanceId = instanceId;
                        Util.saveUserChatClass(mContext, userChatClass);
                        // Goto ChatContactsActivity
                        Intent intent = new Intent(mContext, ChatContactsActivity.class);
                        startActivity(intent);

                    } else {
                        Util.showMessageWithOk(getActivity(), "Something went wrong and please try again.");
                    }
                }

                @Override
                public void onError() {

                }
            }, true, Request.Method.POST);

        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // TODO Uncomment for Firebase Chat
       /* mGoogleApiClient.stopAutoManage((FragmentActivity) getActivity());
        mGoogleApiClient.disconnect();*/
    }
}
