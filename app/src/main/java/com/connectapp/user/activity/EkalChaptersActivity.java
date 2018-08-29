package com.connectapp.user.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.connectapp.user.R;
import com.connectapp.user.data.Thread;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.db.MembersDB;
import com.connectapp.user.members.MembersDirectory;
import com.connectapp.user.util.Util;

public class EkalChaptersActivity extends AppCompatActivity {

    private CardView cv_fts, cv_shss;
    private Context mContext;
    private Thread mThread;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapters);
        cv_fts = (CardView) findViewById(R.id.cv_fts);
        cv_shss = (CardView) findViewById(R.id.cv_shss);

        mContext = EkalChaptersActivity.this;

        mThread = (Thread) getIntent().getSerializableExtra("thread");

        getSupportActionBar().setTitle("Ekal Chapters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cv_fts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserClass userClass = Util.fetchUserClass(mContext);
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
                    Intent intent = new Intent(mContext, MembersDirectory.class);
                    intent.putExtra("thread", mThread);
                    startActivity(intent);
                } else {
                    if (Util.isInternetAvailable(mContext)) {
                        Intent intent = new Intent(mContext, MembersDirectory.class);
                        intent.putExtra("thread", mThread);
                        startActivity(intent);

                    } else {
                        Toast.makeText(mContext, "No Internet Connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        cv_shss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}
