package info.androidhive.volleyexamples;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import info.androidhive.volleyexamples.dbhelper.DatabaseHandler;
import info.androidhive.volleyexamples.listners.CommonListioner;
import info.androidhive.volleyexamples.listners.PermissionListioner;
import info.androidhive.volleyexamples.models.Contact;
import info.androidhive.volleyexamples.utils.PermissionUtil;
import info.androidhive.volleyexamples.utils.Util;
import info.androidhive.volleyexamples.webapi.GetWebservice;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends Activity implements OnClickListener {
	private Button btnJson, btnString, btnImage;
	PermissionUtil permissionUtil;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btnString = (Button) findViewById(R.id.btnStringRequest);
		btnJson = (Button) findViewById(R.id.btnJsonRequest);
		btnImage = (Button) findViewById(R.id.btnImageRequest);

		// button click listeners
		btnString.setOnClickListener(this);
		btnJson.setOnClickListener(this);
		btnImage.setOnClickListener(this);

		if (Build.VERSION.SDK_INT >= M) {
			permissionUtil = PermissionUtil.getInstance(this);
			permissionUtil.checkAudioPermission(permissionListioner);
		} else {
			//setTabsAdapter();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnStringRequest:
			startActivity(new Intent(MainActivity.this,
					StringRequestActivity.class));
			break;
		case R.id.btnJsonRequest:
			/*startActivity(new Intent(MainActivity.this,
					JsonRequestActivity.class));*/
			mskeJsonRequest();
			break;
		case R.id.btnImageRequest:
			startActivity(new Intent(MainActivity.this,
					ImageRequestActivity.class));
			break;
		default:
			break;
		}
	}

	private void mskeJsonRequest(){
		JSONObject params = new JSONObject();
		try {
			params.put("name","Androidhive");
			params.put("email", "abc@androidhive.info");
			params.put("pass", "password123");
			GetWebservice.getInstance(MainActivity.this).jsonRequest(params, commonListioner);
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	PermissionListioner permissionListioner = new PermissionListioner() {
		@Override
		public void onSuccess() {
			//setTabsAdapter();
		}

		@Override
		public void onDailogSuccess() {

		}

		@Override
		public void onFail() {

		}
	};


	CommonListioner commonListioner = new CommonListioner() {
		@Override
		public void onLoadFail(JSONObject error) {
			//400,401,403
			try {
			} catch (Exception e) {
			}
		}

		@Override
		public void onLoadSuccess(JSONObject success) {
			try {
				Util.getInstance(MainActivity.this).showLog(success.toString());
			} catch (Exception e) {
				Util.getInstance(MainActivity.this).showLog("Login..." + e.toString());
			}
		}
	};

	public void dbUse(){
		DatabaseHandler db = new DatabaseHandler(this);

		/**
		 * CRUD Operations
		 * */
		// Inserting Contacts
		Log.d("Insert: ", "Inserting ..");
		db.addContact(new Contact("Ravi", "9100000000"));
		db.addContact(new Contact("Srinivas", "9199999999"));
		db.addContact(new Contact("Tommy", "9522222222"));
		db.addContact(new Contact("Karthik", "9533333333"));

		// Reading all contacts
		Log.d("Reading: ", "Reading all contacts..");
		List<Contact> contacts = db.getAllContacts();

		for (Contact cn : contacts) {
			String log = "Id: " + cn.getID() + " ,Name: " + cn.getName() + " ,Phone: " + cn.getPhoneNumber();
			// Writing Contacts to log
			Log.d("Name: ", log);
		}
	}
}
