package com.connectapp.user.dropDownActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.connectapp.user.R;
import com.connectapp.user.adapter.RathNumberListArrayAdapter;
import com.connectapp.user.data.PrefUtils;
import com.connectapp.user.data.Rath;
import com.connectapp.user.data.RathClass;
import com.connectapp.user.data.State;
import com.connectapp.user.util.Util;

/**
 * Dialog Activity to select the Rath Number.
 * */
public class RathNumberActivity extends Activity {

	private Context mContext;
	private ListView listView_rath;
	public static String RATH = "rath";
	private String[] rathNames, rathCodes;
	private TypedArray imgs;
	private List<Rath> rathLIst;
	private PrefUtils prefUtils;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dropdown_layout);
		mContext = RathNumberActivity.this;
		prefUtils = Util.fetchPrefUtilClass(mContext);
		listView_rath = (ListView) findViewById(R.id.item_listView_dropDown);
		populateCountryList();
		RathNumberListArrayAdapter adapter = new RathNumberListArrayAdapter(RathNumberActivity.this, rathLIst);

		listView_rath.setAdapter(adapter);

		listView_rath.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Rath c = rathLIst.get(position);
				Intent returnIntent = new Intent();
				returnIntent.putExtra(RATH, c.getCode());
				setResult(RESULT_OK, returnIntent);
				//imgs.recycle();
				// recycle images
				finish();
			}
		});

	}

	private void populateCountryList() {
		rathLIst = new ArrayList<Rath>();

		if (prefUtils!=null && prefUtils.getRathClasses().size() > 0) {
			Log.d("RathNumberActivity", "Loading from preference");
			rathNames = getRathNames();
			rathCodes = getRathCodes();
			//imgs = getResources().obtainTypedArray(R.array.country_flags);
			for (int i = 0; i < rathNames.length; i++) {
				rathLIst.add(new Rath(rathNames[i], rathCodes[i]));
			}
		} else {
			Log.d("RathNumberActivity", "Loading from string.xml");
			rathNames = getResources().getStringArray(R.array.rath_number_header_array);
			rathCodes = getResources().getStringArray(R.array.rath_number_array);
			//imgs = getResources().obtainTypedArray(R.array.country_flags);
			for (int i = 0; i < rathNames.length; i++) {
				rathLIst.add(new Rath(rathNames[i], rathCodes[i]));
			}

		}

		/*JSONObject jsonObject = null;
		try {
			jsonObject = Util.loadJSONFromAsset(mContext, "rath_number.json");
		} catch (JSONException e) {
			e.printStackTrace();
			//TODO Show appropriate message and call finish
			Util.showMessageWithOk(RathNumberActivity.this, "Please reload the form");
		}*/

	}

	private String[] getRathNames() {

		ArrayList<RathClass> rathClasses = prefUtils.getRathClasses();
		String[] rathNames = new String[rathClasses.size()];
		for (int i = 0; i < rathClasses.size(); i++) {
			rathNames[i] = rathClasses.get(i).getRathName();

		}

		return rathNames;
	}

	private String[] getRathCodes() {

		ArrayList<RathClass> rathClasses = prefUtils.getRathClasses();
		String[] rathCodes = new String[rathClasses.size()];
		for (int i = 0; i < rathClasses.size(); i++) {
			rathCodes[i] = rathClasses.get(i).getRathCode();

		}

		return rathCodes;
	}

	/**
	 * Dummy Method for future use if needed to fetch rathNames from code and
	 * vice versa.
	 * */
	private String fetchRathNameFromCode(String rathCode, List<Rath> rathList) {
		System.out.println("Country Name: " + rathCode);
		HashMap<String, String> rathMap = new HashMap<String, String>();
		for (int i = 0; i < rathList.size(); i++) {
			String[] rathArray = rathList.get(i).getName().split("\\,");
			//String countryNamefromMap = countryArray[1];

			//countryCodeMap.put(countryNamefromMap, countryList.get(i).getCode());

		}
		Log.v("Hashmap", "" + rathMap.toString());
		System.out.println("" + rathMap.get(rathCode));
		return rathMap.get(rathCode).toString();
	}

}
