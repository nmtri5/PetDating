package com.example.petdating;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.net.URI;

public class SettingsActivity extends AppCompatActivity {
    private EditText mNameField, mPhoneField;
    private Button mBack, mConfirm;
    private ImageView mProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String userID, name, phone, profileImageURL;

    private URI resultURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mPhoneField = (EditText) findViewById(R.id.phone);
        mProfileImage = (ImageView) findViewById(R.id.profileimage);
        mConfirm = (Button) findViewById(R.id.confirm);
        mBack = (Button) findViewById(R.id.back);
    }
}
