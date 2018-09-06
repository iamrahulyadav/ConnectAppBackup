package com.connectapp.user.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
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
import com.connectapp.user.adapter.ImageAdapter;
import com.connectapp.user.constant.Consts;
import com.connectapp.user.data.ImageClass;
import com.connectapp.user.data.Spouse;
import com.connectapp.user.util.Util;
import com.connectapp.user.view.DropDownViewForXML;
import com.connectapp.user.volley.PostWithJsonWebTask;
import com.connectapp.user.volley.ServerResponseCallback;
import com.connectapp.user.volley.ServerStringResponseCallback;
import com.connectapp.user.volley.VolleyTaskManager;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.games.quest.Quest;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private ProgressDialog mProgressDialog;
    private final int CAMERA_PIC_REQUEST = 7464;
    private static Uri mCapturedImageURI;

    private String TAG = getClass().getSimpleName();
    private ArrayList<ImageClass> imagesList = new ArrayList<>();
    private String imageFileName = "";
    private int imageCount = 0;
    private int personCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_village_form);
        mContext = VillageFormActivity.this;
        stateName = getIntent().getStringExtra("state");
        districtName = getIntent().getStringExtra("district");
        villageName = getIntent().getStringExtra("village");
        personCount = getIntent().getIntExtra("personCount", 0);
        initView();
        // Initialize volley
        volleyTaskManager = new VolleyTaskManager(mContext);
        // Initialize progress dialog
        mProgressDialog = new ProgressDialog(this.mContext);
        mProgressDialog.setProgressStyle(0);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
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
        int count = Util.fetchPersonCount(mContext);
        if (count != 0)
            count = count - 1;
        Log.e("onCancelClicked", "Person Count: " + count);
        Util.savePersonCount(mContext, count);
        finish();
    }

    public void onPostClicked(View view) {
        validateAndPostFormData();
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
            if (selectedView.getId() == R.id.tv_dom) {
                Toast.makeText(mContext, "Add Spouse", Toast.LENGTH_SHORT).show();
                ll_spouse.setVisibility(View.VISIBLE);

            }
        }
    };

    public void onCameraClicked(View view) {
        if (imagesList.size() < 1) {
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.setCancelable(true);
            showProgressDialog();
            cameraSelectedPic();
            return;
        }
        Util.showMessageWithOk(VillageFormActivity.this, "Maximum number of images has already been selected!");
    }

    private void showProgressDialog() {
        if (!mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    protected void cameraSelectedPic() {
        //camera stuff
        Intent imageIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        //folder stuff
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "ConnectAppImages");
        imagesFolder.mkdirs();

        File image = new File(imagesFolder, "VILLAGE_" + timeStamp + ".png");
        imageFileName = "VILLAGE_" + timeStamp + ".png";
        Uri uriSavedImage = Uri.fromFile(image);
        mCapturedImageURI = uriSavedImage;
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(imageIntent, CAMERA_PIC_REQUEST);
    }

    private void processImagePath(String picturePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inScaled = true;
        int bitWidth = BitmapFactory.decodeFile(picturePath).getWidth();
        int bitHeight = BitmapFactory.decodeFile(picturePath).getHeight();
        System.out.println("width : " + bitWidth + " bitHeight : " + bitHeight);
        if (bitWidth <= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitHeight <= 1536) {
            opt.inSampleSize = 4;
        } else if ((bitHeight <= 1536 || bitHeight > 1944)
                && (bitWidth <= AccessibilityNodeInfoCompat.ACTION_PREVIOUS_HTML_ELEMENT || bitWidth > 2592)) {
            opt.inSampleSize = 8;
        } else {
            opt.inSampleSize = 6;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath, opt);
        if (bitmap != null) {
            try {
                int orientation = new ExifInterface(picturePath).getAttributeInt("Orientation", 1);
                Log.e("orientation", new StringBuilder(String.valueOf(orientation)).append("<<<").toString());
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case CompletionEvent.STATUS_FAILURE /*1*/:
                        Log.e("Case:", "1");
                        break;
                    case CompletionEvent.STATUS_CONFLICT /*2*/:
                        Log.e("Case:", "2");
                        break;
                    case CompletionEvent.STATUS_CANCELED /*3*/:
                        Log.e("Case:", "3");
                        matrix.postRotate(BitmapDescriptorFactory.HUE_CYAN);
                        break;
                    case GeofencingRequest.INITIAL_TRIGGER_DWELL /*4*/:
                        Log.e("Case:", "4");
                        break;
                    case DetectedActivity.TILTING /*5*/:
                        Log.e("Case:", "5");
                        break;
                    case Quest.STATE_FAILED /*6*/:
                        Log.e("Case:", "6");
                        matrix.postRotate(90.0f);
                        break;
                    case DetectedActivity.WALKING /*7*/:
                        Log.e("Case:", "7");
                        break;
                    case DetectedActivity.RUNNING /*8*/:
                        Log.e("Case:", "8");
                        matrix.postRotate(-90.0f);
                        break;
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ImageClass imageClass = new ImageClass();
            imageClass.setBase64value(Util.getBase64StringFromBitmap(bitmap));
            imageClass.setImageCount(this.imageCount + 1);
            imagesList.add(imageClass);
            imageCount++;
        } else {
            Toast.makeText(this, new StringBuilder(String.valueOf(picturePath)).append("not found").toString(), Toast.LENGTH_LONG).show();
        }
        imageUpdateOnView();
    }

    private void imageUpdateOnView() {
        vp_selectedImages.setAdapter(new ImageAdapter(this, imagesList));
        if (imagesList.size() == 0) {
            vp_selectedImages.setBackgroundResource(R.drawable.default_empty);
            //ib_reject_picture.setVisibility(View.INVISIBLE);
        } else {
            vp_selectedImages.setBackgroundColor(Color.parseColor("#D7D7D7"));
            vp_selectedImages.setCurrentItem(imagesList.size() - 1);
            //ib_reject_picture.setVisibility(View.VISIBLE);
        }
        if (imagesList.size() <= 1) {
            tv_imageProgress.setText("[Image added " + imagesList.size() + "/1]");
            // v_swipeRight.setVisibility(View.INVISIBLE);
            //v_swipeLeft.setVisibility(View.INVISIBLE);
            return;
        }
        tv_imageProgress.setText("Slide to view other images\n[Images added " + imagesList.size() + "/1]");
        // v_swipeRight.setVisibility(View.VISIBLE);
        //v_swipeLeft.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        hideProgressDialog();
        if (requestCode == CAMERA_PIC_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                String picturePath = mCapturedImageURI.getPath();
                processImagePath(picturePath);
            } else {
                Toast.makeText(this.mContext, "No image selected", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void onAddMoreClick(View view) {
        Log.e("onAddMoreClick", "Person Count: " + Util.fetchPersonCount(mContext));
        if (Util.fetchPersonCount(mContext) < 2) {
            personCount = personCount + 1;
            Util.savePersonCount(mContext, personCount);
            Intent intent = new Intent(mContext, VillageFormActivity.class);
            intent.putExtra("state", stateName);
            intent.putExtra("district", districtName);
            intent.putExtra("village", villageName);
            intent.putExtra("personCount", personCount);
            startActivity(intent);
        } else {
            Toast.makeText(mContext, "Max number of person already added", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        // DO nothing-- disable backpressed
    }
}
