package com.adliano.contentprovider3;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
Adriano Alves
July 22 2017
Application to show how to get Contacts list from the device using
content provider
 */

public class MainActivity extends AppCompatActivity
{
    ArrayList<ContactInfo> contactList;
    ListView theListView;
    //get access to location permission
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        theListView = findViewById(R.id.listView);
        contactList = new ArrayList<>();

        checkUserPermissions();
    }

    // get the contacts form the device and load it to a ArrayList
    // Contacts are located on a database therefore we will use a cursor
    void readContact()
    {
        String name ;
        String phoneNumber ;
        String email="";

        // query for Phone
        Cursor cursorPhone = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        // query for Email
        Cursor cursorEmail = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null, null, null, null);

        if(cursorPhone != null)
        {
            while(cursorPhone.moveToNext())
            {
                // get the string name by using the column index DISPLAY_NAME
                name = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                phoneNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                // check if email is available
                if (cursorEmail != null && cursorEmail.moveToNext())
                    email = cursorEmail.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

                // add the found data to the list
                contactList.add(new ContactInfo(name, phoneNumber, email));
            }

            // free the cursor
            cursorPhone.close();
            if(cursorEmail != null)cursorEmail.close();

            // populate the ListView
            MyCustomContactAdapter adapter = new MyCustomContactAdapter(contactList);
            theListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
    // Check for Permission
    void checkUserPermissions()
    {
        if(Build.VERSION.SDK_INT >= 23)
        {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
            {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_ASK_PERMISSIONS);
               // return;
            }
        }
        // if permission granted red the contacts
       // readContact();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // if permission granted red the contacts
                    readContact();
                }
                else
                {
                    // Permission Denied
                    Toast.makeText(this, R.string.contact_permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    // Adapter for the ListView
    private class MyCustomContactAdapter extends BaseAdapter
    {
        ArrayList<ContactInfo> listContactAdapter;

        MyCustomContactAdapter(ArrayList<ContactInfo> listContactAdapter)
        {
            this.listContactAdapter = listContactAdapter;
        }


        @Override
        public int getCount()
        {
            return listContactAdapter.size();
        }

        @Override
        public String getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getLayoutInflater();
            @SuppressLint({"ViewHolder", "InflateParams"}) View myView = mInflater.inflate(R.layout.contact_list_itens_layout, null);

            final ContactInfo s = listContactAdapter.get(position);

            // Name
            TextView tvName = myView.findViewById(R.id.textViewName);
            tvName.setText(s.name);
            // Phone Number
            TextView tvPhoneNumber = myView.findViewById(R.id.textViewPhoneNumber);
            tvPhoneNumber.setText(s.phoneNumber);
            // Email
            TextView tvEmail = myView.findViewById(R.id.tvEmail);
            tvEmail.setText(s.email);

            return myView;
        }

    }
}
