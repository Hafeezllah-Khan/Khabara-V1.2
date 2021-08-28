package com.example.otptest;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.otptest.databinding.ActivityContactsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ContactsActivity extends AppCompatActivity {

    ActivityContactsBinding binding;
    private static final String TAG = "ContactsActivity";
    //private ActivityContactsBinding binding;
    //private List<User> list = new ArrayList<>();
    private ContactsAdapter adapter;
    //private FirebaseUser firebaseUser;
    //private FirebaseFirestore firestore;

    public static final int REQUEST_READ_CONTACTS = 79;

    private ArrayList mobileArray;

    //private ListView contactlist;

    //ActivityMainBinding binding;
    FirebaseDatabase database;
    ArrayList<User> users;
    UsersAdapter usersAdapter;
    TopStatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;

    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this,R.layout.activity_contacts);

       /* binding = ActivityContactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());*/


        database = FirebaseDatabase.getInstance();
        users = new ArrayList<>();
        userStatuses = new ArrayList<>();

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //firestore = FirebaseFirestore.getInstance();

        if (database!=null){
            getContactFromPhone(); // If they using this app
            // getContactList();
        }

        if (mobileArray!=null) {
            getContactList();

        }
        //L//og.i(TAG, "onCreate: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        //Log.i(TAG, String.valueOf(mobileArray));
        //Log.i(TAG, "onCreate: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//        for (User ii: users) {
//            Log.i(TAG, ii.getPhoneNumber());
//            Log.i(TAG, "xxxxxxxxxxxxxxxxxxxxxxx");
//        }

    }

    private void getContactFromPhone() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED) {
            mobileArray = getAllPhoneContacts();
        } else {
            requestPermission();
        }

    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
            // show UI part if you want here to show some rationale !!!
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_CONTACTS)) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mobileArray = getAllPhoneContacts();
                } else {
                    finish();
                }
                return;
            }
        }
    }
    private ArrayList getAllPhoneContacts() {
        //String ISOPrefix = getCountryISO();

        ArrayList<String> phoneList = new ArrayList<>();
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
//                String name = cur.getString(cur.getColumnIndex(
//                        ContactsContract.Contacts.DISPLAY_NAME));
//                nameList.add(name);

                if (cur.getInt(cur.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNo.replaceAll("\\s","");
                        //Log.i(TAG, "getAllPhoneContacts: " + phoneNo);
                        //Log.d(TAG, "getAllPhoneContacts: ");
//                        if(!String.valueOf(phoneNo.charAt(0)).equals("+"))
//                            phoneNo = ISOPrefix + phoneNo;
                        phoneList.add(phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        return phoneList;
    }

    private void getContactList() {
        database.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                users.clear();
          /*      for (User ii: users) {
                    Log.i(TAG, ii.getPhoneNumber());
                    Log.i(TAG, "xxxxxxxxxxxxxxxxxxxxxxx");
                }*/
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid())){
                        if (mobileArray.contains(user.getPhoneNumber())){
                            users.add(user);
     /*                       for (User ii: users) {
                                Log.i(TAG, ii.getPhoneNumber());
                                Log.i(TAG, "yyyyyyyyyyyyyy");
                            }*/
                        }
                    }
                        //users.add(user);
                }
                //binding.recyclerView.hideShimmerAdapter();
                //usersAdapter.notifyDataSetChanged();

                for (User ii: users) {
                    Log.i(TAG, ii.getPhoneNumber());
                    Log.i(TAG, "zzzzzzzzzzzzzz");
                }
                adapter = new ContactsAdapter(users,ContactsActivity.this);
                binding.recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

/*        firestore.collection("Users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot snapshots : queryDocumentSnapshots){
                    String userID = snapshots.getString("userID");
                    String userName = snapshots.getString("userName");
                    String imageUrl = snapshots.getString("imageProfile");
                    String desc = snapshots.getString("bio");
                    String phone = snapshots.getString("userPhone");

                    Users user = new Users();
                    user.setUserID(userID);
                    user.setBio(desc);
                    user.setUserName(userName);
                    user.setImageProfile(imageUrl);
                    user.setUserPhone(phone);

                    if (userID != null && !userID.equals(firebaseUser.getUid())) {
                        if (mobileArray.contains(user.getUserPhone())){
                            list.add(user);
                        }
                    }
                }


//                for (Users user : list){
//                    if (mobileArray.contains(user.getUserPhone())){
//                        Log.d(TAG, "getContactList: true "+user.getUserPhone() );
//                    } else {
//                        Log.d(TAG, "getContactList: false"+user.getUserPhone());
//                    }
//                }

                adapter = new ContactsAdapter(list,ContactsActivity.this);
                binding.recyclerView.setAdapter(adapter);
            }

        });*/

    }

/*    String getCountryISO(){
        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null)
            if (!telephonyManager.getNetworkCountryIso().toString().equals(""))
                iso = telephonyManager.getNetworkCountryIso().toString();

        return CountryToPhonePrefix.getPhone(iso);
    }*/

}