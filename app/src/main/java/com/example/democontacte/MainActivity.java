package com.example.democontacte;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.democontacte.Model.Contacte;

import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.WRITE_CONTACTS;

public class MainActivity extends Activity {
	ArrayList<Contacte> arrayList;
	ContactsAdapter adapter;
	ListView lvContacts;

	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle("loading");

		lvContacts = (ListView) findViewById(R.id.lvContacts);
		/*listContacts = new ContactFetcher(this).fetchAll();
		lvContacts = (ListView) findViewById(R.id.lvContacts);
		ContactsAdapter adapterContacts = new ContactsAdapter(this, listContacts);
		lvContacts.setAdapter(adapterContacts);
		new StartExport().execute();*/
		requestContactsPermissions();

		lvContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String name = arrayList.get(position).getName();
				Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
			}
		});

		Button btnContacts = findViewById(R.id.btn_contacts);
		btnContacts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new StartExport().execute();
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	private void requestContactsPermissions(){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {// NOPMD
			if (ActivityCompat.checkSelfPermission(MainActivity.this, READ_CONTACTS)// NOPMD
					!= PackageManager.PERMISSION_GRANTED
					|| ActivityCompat.checkSelfPermission(MainActivity.this, WRITE_CONTACTS)
					!= PackageManager.PERMISSION_GRANTED){
				ActivityCompat.requestPermissions(MainActivity.this,
						new String[]{READ_CONTACTS,WRITE_CONTACTS}, 1);
			}else {
				new StartExport().execute();
			}
		}else {
			new StartExport().execute();
		}
	}
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1) {
			if (grantResults.length > 0
					&& grantResults[0] != PackageManager.PERMISSION_GRANTED
					&& grantResults[1] != PackageManager.PERMISSION_GRANTED) {
				//Toast.makeText(this, R.string.permissions_contacts, Toast.LENGTH_SHORT).show();
			} else {
				requestContactsPermissions();
			}
		}
	}
	public class StartExport extends AsyncTask<Void,Void,Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			arrayList = new ArrayList<>();
//        progressDialog.setMessage(getResources().getString(R.string.loading));
       progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... voids) {
			Set<String> setNumbers = new HashSet<>();
			Uri readContactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
			Cursor cursor = getContentResolver().query(readContactsUri, null, null, null, null);
			if (cursor != null) {
				while (cursor.moveToNext()){
					int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
					String userDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
					String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					String email = getContactEmail(id);
					Contacte contact = new Contacte();
					contact.setId(id);
					contact.setName(userDisplayName);
					contact.setPhone(phoneNumber);
					contact.setEmail(email);
					//  contact.setChecked(false);
					if (!setNumbers.contains(phoneNumber)) {
						setNumbers.add(phoneNumber);
						arrayList.add(contact);
					}
				}
				cursor.close();
			}
			return null;
		}



		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
			progressDialog.dismiss();
			if (!arrayList.isEmpty()) {
				//liste bien importer et n'est pas vide alors tu fait ce que tu veux
				Log.e("contacts", arrayList.toString());
				adapter = new ContactsAdapter(MainActivity.this, arrayList);
				lvContacts.setAdapter(adapter);
			}else {
				//liste vide affiche un message d'erreur
				Log.e("contacts", "empty");
			}
		}
		//get contact email
		private String getContactEmail(int contactId){
			String email = "null";
			Uri readContactsUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
			Cursor cursor = getContentResolver().query(readContactsUri, null,
					ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				int displayEmailIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
				email = cursor.getString(displayEmailIndex);
				cursor.close();
			}
			return email;
		}
	}

}
