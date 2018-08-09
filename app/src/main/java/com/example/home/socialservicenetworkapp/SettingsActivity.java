package com.example.home.socialservicenetworkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Toolbar mToolbar;
    private EditText userName, userProfName, userStatus, userCountry, userGender, userDOB, userRelationshipStatus;
    private Button UpdateAcountSettingsButton;
    private CircleImageView userProfImage;

    private DatabaseReference SettingsUserRef;
    private FirebaseAuth mAuth;

    private String currentUserId;

    final static int Gallery_Pick = 1;

    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;
    private Spinner statusSpinner;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("Profile");
        mToolbar.setTitleTextColor(Color.BLACK);
        mToolbar.setTitleTextAppearance(this, R.style.NavigationText);
        mToolbar.setNavigationIcon(R.drawable.backbutton);
        statusSpinner = findViewById(R.id.spinnerStatus);

        userName = findViewById(R.id.settings_username);
        userProfName = findViewById(R.id.settings_full_name);
        userStatus = findViewById(R.id.settings_status);
        userCountry = findViewById(R.id.settings_country);
        userDOB = findViewById(R.id.settings_dob);
        userProfImage = findViewById(R.id.settings_profile_image);
        userRelationshipStatus = findViewById(R.id.settings_relationship_status);
        userGender = findViewById(R.id.settings_gender);
        UpdateAcountSettingsButton = findViewById(R.id.update_account_settings_button);
        loadingBar = new ProgressDialog(this);

        //statusAdapter//
        ArrayAdapter<CharSequence> statusAdapter = ArrayAdapter
                .createFromResource(this, R.array.status_array,
                        android.R.layout.simple_spinner_item);
        statusAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(statusAdapter);
        statusSpinner.setOnItemSelectedListener(this);
        statusSpinner.getBackground().setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);


        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String status = preferences.getString("status", "");
        if(!status.equalsIgnoreCase(""))
        {
            int spinnerPosition = statusAdapter.getPosition(status);
            statusSpinner.setSelection(spinnerPosition);

        }
        //

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        SettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        SettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileimage").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullname").getValue().toString();
                    String myProfileStatus = dataSnapshot.child("status").getValue().toString();
                    String myProfileDOB = dataSnapshot.child("dob").getValue().toString();
                    String myProfileCountry = dataSnapshot.child("country").getValue().toString();
                    String myProfileGender = dataSnapshot.child("gender").getValue().toString();
                    String myRelationshipStatus = dataSnapshot.child("relationshipstatus").getValue().toString();
                    String myUserName = dataSnapshot.child("username").getValue().toString();

                    Picasso.with(SettingsActivity.this).load(myProfileImage).placeholder(R.drawable.profile).into(userProfImage);
                    userName.setText(myUserName);
                    userProfName.setText(myProfileName);
                    userStatus.setText(myProfileStatus);
                    userDOB.setText(myProfileDOB);
                    userCountry.setText(myProfileCountry);
                    userGender.setText(myProfileGender);
                    userRelationshipStatus.setText(myRelationshipStatus);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        UpdateAcountSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });

        userProfImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Please wait, while we updating your profile image...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                Uri resultUri = result.getUri();

                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SettingsActivity.this, "Profile Image stored successfully to Firebase storage...", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            SettingsUserRef.child("profileimage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent selfIntent = new Intent(SettingsActivity.this, SettingsActivity.class);
                                                startActivity(selfIntent);

                                                Toast.makeText(SettingsActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(SettingsActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Error occured: Image can not be cropped. Try Again.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void ValidateAccountInfo() {
        String username = userName.getText().toString();
        String usersprofilename = userProfName.getText().toString();
        String userstatus = userStatus.getText().toString();
        String usersdob = userDOB.getText().toString();
        String userscountry = userCountry.getText().toString();
        String usersgender = userGender.getText().toString();
        String usersrelationshipstatus = userRelationshipStatus.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Please add your name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(usersprofilename)) {
            Toast.makeText(this, "Please add your profile name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userstatus)) {
            Toast.makeText(this, "Please add your status", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(usersdob)) {
            Toast.makeText(this, "Please add your date of birth", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userscountry)) {
            Toast.makeText(this, "Please add your country", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(usersgender)) {
            Toast.makeText(this, "Please add your gender", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(usersrelationshipstatus)) {
            Toast.makeText(this, "Please add your relationship status", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Profile Image");
            loadingBar.setMessage("Please wait, while we updating your settings...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            UpdateAccountInfo(username, usersprofilename, userstatus, usersdob, userscountry, usersgender, usersrelationshipstatus);
        }
    }

    private void UpdateAccountInfo(String username, String usersprofilename, String userstatus, String usersdob, String userscountry, String usersgender, String usersrelationshipstatus) {
        HashMap usersMap = new HashMap();
        usersMap.put("username", username);
        usersMap.put("fullname", usersprofilename);
        usersMap.put("status", userstatus);
        usersMap.put("dob", usersdob);
        usersMap.put("country", userscountry);
        usersMap.put("gender", usersgender);
        usersMap.put("relationshipstatus", usersrelationshipstatus);
        SettingsUserRef.updateChildren(usersMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    SendUserToMainActivity();
                    Toast.makeText(SettingsActivity.this, "Account settings updated", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                } else {
                    Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                userStatus.setText("Active");
                break;
            case 1:
                userStatus.setText("Inactive");
                break;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("status",statusSpinner.getSelectedItem().toString());
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
