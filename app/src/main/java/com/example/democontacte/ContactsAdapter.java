package com.example.democontacte;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.democontacte.Model.Contacte;

public class ContactsAdapter extends ArrayAdapter<Contacte> {

	public ContactsAdapter(Context context, ArrayList<Contacte> contacts) {
		super(context, 0, contacts);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the data item
		Contacte contact = getItem(position);
		// Check if an existing view is being reused, otherwise inflate the view
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = LayoutInflater.from(getContext());
			view = inflater.inflate(R.layout.adapter_contact_item, parent, false);
		}
		// Populate the data into the template view using the data object
		TextView tvName = (TextView) view.findViewById(R.id.tvName);
		TextView tvEmail = (TextView) view.findViewById(R.id.tvEmail);
		TextView tvPhone = (TextView) view.findViewById(R.id.tvPhone);
		tvName.setText(contact.getName());
		tvEmail.setText(contact.getEmail());
		tvPhone.setText(contact.getPhone());
		return view;
	}

}
