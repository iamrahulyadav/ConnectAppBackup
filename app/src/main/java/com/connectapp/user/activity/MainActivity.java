package com.connectapp.user.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.DrawerAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.OfflineSubmission;
import com.connectapp.user.data.UserClass;
import com.connectapp.user.db.HistoryDB;
import com.connectapp.user.dropDownActivity.StateCodeActivity;
import com.connectapp.user.font.RobotoTextView;
import com.connectapp.user.fragment.DashboardFragment;
import com.connectapp.user.syncadapter.Constant;
import com.connectapp.user.syncadapter.DBConstants;
import com.connectapp.user.syncadapter.OfflineDB;
import com.connectapp.user.util.ImageUtil;
import com.connectapp.user.util.Util;
import com.connectapp.user.view.MaterialRippleLayout;
import com.connectapp.user.volley.VolleyTaskManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * @author raisahab.ritwik
 */
public class MainActivity extends AppCompatActivity implements DBConstants, OnClickListener {

    private String TAG = getClass().getSimpleName();
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();
    private Context mContext;
    private Dialog customDialog;
    private Dialog unsyncedDataDialog;

    private VolleyTaskManager volleyTaskManager;

    private int dataCount = 0;
    private boolean isPostOfflineData = false;
    private UserClass userClass;
    private Dialog schoolCodeDialog;

    private TextView tvCountryCode;
    private TextView tv_stateCode;
    private EditText et_anchal;
    private EditText et_sankul;
    private EditText et_sanch;
    private EditText et_upsanch;
    private EditText et_village;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_raga);
        mContext = MainActivity.this;

        userClass = Util.fetchUserClass(mContext);

        ImageLoader imageLoader = ImageLoader.getInstance();
        if (!imageLoader.isInited()) {
            imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ConnectApp");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mTitle = mDrawerTitle = getTitle();
        mDrawerList = (ListView) findViewById(R.id.list_view);

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        View headerView = getLayoutInflater().inflate(R.layout.header_navigation_drawer_social, mDrawerList, false);
        RobotoTextView robotoTextView = (RobotoTextView) headerView.findViewById(R.id.tv_userName);
        if (userClass != null)
            robotoTextView.setText(userClass.getName());
        ImageView iv = (ImageView) headerView.findViewById(R.id.image);

        ImageUtil.displayRoundImage(iv, "", null);

        mDrawerList.addHeaderView(headerView);// Add header before adapter (for
        // pre-KitKat)
        mDrawerList.setAdapter(new DrawerAdapter(this));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        int color = getResources().getColor(R.color.material_grey_100);
        color = Color.argb(0xCD, Color.red(color), Color.green(color), Color.blue(color));
        mDrawerList.setBackgroundColor(color);
        mDrawerList.getLayoutParams().width = (int) getResources().getDimension(R.dimen.drawer_width_social);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            displayView(1);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            displayView(position);

            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }


    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     **/
    private void displayView(int position) {
        // update the main content by replacing fragments
        Fragment fragment = null;
        switch (position) {

            case 1:
                // Main Dashboard fragment/Home
                fragment = new DashboardFragment();
                //Util.showMessageWithOk(mContext, "HOME");
                break;

            case 2:

                // UNSYNCED DATA
                showUnsyncedDataCount();
                break;

            case 3:
                // Submission History
                onHistoryClick();
                break;

            case 4:
                gotoSchoolViewMenu();
                //showSchoolCodeEntryMenu();
                break;

            case 5:
                gotoRathViewMenu();
                break;
		/*case 6:
			// Resources and gallery
			onResourcesClick();
			break;*/
            case 6:
                String url = Consts.PRIVACY_POLICY_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            default:
                break;
        }

        if (fragment != null) {
            replaceFragment(fragment, position);
        } else {
            // error in creating fragment
            Log.e("FireBaseActivity", "Error in creating fragment");
        }
    }

    private void replaceFragment(Fragment fragment, int position) {

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    /**
     * Un-synced Data option
     */
	/*
	private void onDataSettingClick() {

	customDialog = new Dialog(mContext);

	customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

	LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
	View view = layoutInflater.inflate(R.layout.dialog_offline_data_status, null);

	ImageButton btn_close = (ImageButton) view.findViewById(R.id.btn_close);

	btn_close.setOnClickListener(new OnClickListener() {

	@Override
	public void onClick(View v) {
	customDialog.dismiss();

	}
	});

	TextView tv_unsyncedData = (TextView) view.findViewById(R.id.tv_unsyncedData);

	offlineSubmissions.clear();
	try {
	fetchOfflineRows();

	} catch (Exception e) {
	e.printStackTrace();
	} finally {
	tv_unsyncedData.setText("Unsynced Data: (" + offlineSubmissions.size() + ")");

	}

	RelativeLayout rl_unsyncedDataCountLayout = (RelativeLayout) view.findViewById(R.id.rl_unsyncedDataCount);

	rl_unsyncedDataCountLayout.setOnClickListener(new OnClickListener() {
	@Override
	public void onClick(View v) {
	if (offlineSubmissions.size() > 0) {
	customDialog.dismiss();
	startActivity(new Intent(mContext, UnSyncedDataActivity.class));
	}

	}
	});

	customDialog.setCancelable(false);
	customDialog.setContentView(view);
	customDialog.setCanceledOnTouchOutside(false);
	// Start AlertDialog
	customDialog.show();
	}*/
    private void showUnsyncedDataCount() {
        unsyncedDataDialog = new Dialog(mContext);
        unsyncedDataDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(
                mContext.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.dialog_unsynced_data_info, null);
        RobotoTextView dialog_universal_info_text = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_text);
        RobotoTextView dialog_universal_info_ok = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_ok);
        RobotoTextView dialog_universal_info_cancel = (RobotoTextView) view.findViewById(R.id.dialog_universal_info_cancel);
        MaterialRippleLayout rippleLayoutCancel = (MaterialRippleLayout) view.findViewById(R.id.rippleLayoutCancel);

        offlineSubmissions.clear();
        try {
            fetchOfflineRows();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (offlineSubmissions.size() > 0) {
                unsyncedDataDialog.dismiss();
                startActivity(new Intent(mContext, UnSyncedDataActivity.class));
            } else {
                dialog_universal_info_text.setText("You have No Unsynced Data at the moment.");
                rippleLayoutCancel.setVisibility(View.GONE);

            }

        }

        dialog_universal_info_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (offlineSubmissions.size() > 0) {

                    unsyncedDataDialog.dismiss();
                    startActivity(new Intent(mContext, UnSyncedDataActivity.class));
                } else
                    unsyncedDataDialog.dismiss();
            }
        });
        dialog_universal_info_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                unsyncedDataDialog.dismiss();
            }
        });
        unsyncedDataDialog.setContentView(view);

        unsyncedDataDialog.show();
        if (offlineSubmissions.size() > 0)
            unsyncedDataDialog.dismiss();

    }

    private void onResourcesClick() {

        startActivity(new Intent(mContext, ResourcesActivity.class));

    }

    private void fetchOfflineRows() {
        OfflineDB mDb = new OfflineDB(mContext);
        SQLiteDatabase database = mDb.getReadableDatabase();

        try {
            Cursor cur = database.query(OFFLINE_TABLE, null, null, null, null, null, null);
            System.out.println("Count: " + cur.getCount());
            if (cur != null) {
                while (cur.moveToNext()) {
                    offlineSubmissions.add(Constant.getOfflineSubmissionFromCursor(cur));
                }
                cur.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onHistoryClick() {

        ArrayList<OfflineSubmission> offlineSubmissions = new ArrayList<OfflineSubmission>();
        offlineSubmissions = new HistoryDB().getHistory(mContext);
        if (offlineSubmissions.size() > 0)
            startActivity(new Intent(mContext, HistoryActivity.class));
        else
            Util.showMessageWithOk(MainActivity.this, "You have no submission History!");

    }

    private void showSchoolCodeEntryMenu() {

        schoolCodeDialog = new Dialog(MainActivity.this);

        schoolCodeDialog.setContentView(R.layout.dialog_schoolcode_menu);
        // Set dialog title
        schoolCodeDialog.setTitle("Custom Dialog");

        tvCountryCode = (TextView) schoolCodeDialog.findViewById(R.id.tvCountryCode);
        tv_stateCode = (TextView) schoolCodeDialog.findViewById(R.id.tv_stateCode);
        et_anchal = (EditText) schoolCodeDialog.findViewById(R.id.et_anchal);
        et_sankul = (EditText) schoolCodeDialog.findViewById(R.id.et_sankul);
        et_sanch = (EditText) schoolCodeDialog.findViewById(R.id.et_sanch);
        et_upsanch = (EditText) schoolCodeDialog.findViewById(R.id.et_upsanch);
        et_village = (EditText) schoolCodeDialog.findViewById(R.id.et_village);

        tv_stateCode.setFocusable(true);
        tv_stateCode.requestFocus();
        tv_stateCode.setCursorVisible(true);
        tv_stateCode.setOnClickListener(this);
        setTextWatcher();
        RobotoTextView submit = (RobotoTextView) schoolCodeDialog.findViewById(R.id.submit);
		/*ImageButton ib_closeWindow = (ImageButton) schoolCodeDialog.findViewById(R.id.ib_closeWindow);
		ib_closeWindow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				schoolCodeDialog.dismiss();
			}
		});*/

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String countryCode = tvCountryCode.getText().toString().trim();
                String stateCode = tv_stateCode.getText().toString().trim();
                String anchal = et_anchal.getText().toString().trim();
                String sankul = et_sankul.getText().toString().trim();
                String sanch = et_sanch.getText().toString().trim();
                String upsanch = et_upsanch.getText().toString().trim();
                String village = et_village.getText().toString().trim();

                if (stateCode.isEmpty() || stateCode.equalsIgnoreCase("-")) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the State Code.");
                } else if (anchal.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Anchal.");
                } else if (sankul.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Sankul.");
                } else if (sanch.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Sanch.");
                } else if (upsanch.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Up-Sanch.");
                } else if (village.isEmpty()) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the Village.");
                } else if (anchal.length() < 2) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the correct Anchal.");
                } else if (village.length() < 2) {
                    Util.showMessageWithOk(MainActivity.this, "Please enter the correct Village.");
                } else {
                    String enteredSchoolCode = new StringBuilder(String.valueOf(countryCode)).append(stateCode).append(anchal)
                            .append(sankul).append(sanch).append(upsanch).append(village).toString();
                    schoolCodeDialog.dismiss();

                    Intent intent = new Intent(mContext, WebViewActivity.class);
                    intent.putExtra("loadUrl", "http://connectapp.net/dev/fts/portal/schools/" + enteredSchoolCode);
                    startActivity(intent);
                }

				/*if (enteredSchoolCode.isEmpty()) {
					Toast.makeText(mContext, "Please enter the School Code.", Toast.LENGTH_LONG).show();
				} else if (enteredSchoolCode.length() < 11) {
					Toast.makeText(mContext, "Please enter the correct School Code.", Toast.LENGTH_LONG).show();
				} */

            }
        });
        schoolCodeDialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(schoolCodeDialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        schoolCodeDialog.show();
        schoolCodeDialog.getWindow().setAttributes(lp);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_stateCode:
                startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);
                break;

            default:
                break;
        }
        startActivityForResult(new Intent(this.mContext, StateCodeActivity.class), 11);

    }

    private void setTextWatcher() {
        et_anchal.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "anchal on text changed count " + s.length());
                if (s.length() == 2)
                    et_sankul.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        et_sankul.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1)
                    et_sanch.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_sanch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1)
                    et_upsanch.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_upsanch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1)
                    et_village.requestFocus();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        et_village.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 2) {
                    et_village.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    Util.hideSoftKeyboard(mContext, et_village);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("------>> onActivityResult CALLED  >>---------------");
        if (requestCode == 11 && resultCode == Activity.RESULT_OK) {
            this.tv_stateCode.setText(data.getStringExtra(StateCodeActivity.RESULT_STATECODE));
            this.et_anchal.requestFocus();
            Util.showSoftKeyboard(this.mContext, this.et_anchal);
        }
    }

    private void gotoSchoolViewMenu() {
        Intent intent = new Intent(mContext, SchoolViewMenuActivity.class);
        startActivity(intent);

    }

    private void gotoRathViewMenu() {
        Intent intent = new Intent(mContext, RathViewMenuActivity.class);
        startActivity(intent);

    }

}
