package com.example.petdating;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private EditText mNameField, mDOBField, mPhoneField, mBioField;
    private Spinner mBreedSpinner;
    private Button mBack, mConfirm;
    private ImageView mProfileImage;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private String userID, name, breed, profileImageURL, dob, phone, userSex, bio;

    private Uri resultUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mNameField = (EditText) findViewById(R.id.name);
        mBreedSpinner = (Spinner) findViewById(R.id.breed);
        mDOBField = (EditText) findViewById(R.id.dob);
        mPhoneField = (EditText) findViewById(R.id.editphone);
        mBioField = (EditText) findViewById(R.id.editbio);
        mProfileImage = (ImageView) findViewById(R.id.profileimage);
        mConfirm = (Button) findViewById(R.id.confirm);
        mBack = (Button) findViewById(R.id.back);
        android.widget.ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.breedregistration, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1);
        mBreedSpinner.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        getUserInfo();
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestRead();
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission Denied
                Toast.makeText(SettingsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getUserInfo() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if(map.get("name") != null){
                        name = map.get("name").toString();
                        mNameField.setText(name);
                    }
                    if(map.get("breed") != null){
                        breed = map.get("breed").toString();
                        for(int i= 0; i < mBreedSpinner.getAdapter().getCount(); i++){
                            if(mBreedSpinner.getAdapter().getItem(i).toString().contains(breed))
                            {
                                mBreedSpinner.setSelection(i);
                            }
                        }
                    }
                    if(map.get("dob") != null){
                        dob = map.get("dob").toString();
                        mDOBField.setText(dob);
                    }
                    if(map.get("phone") != null){
                        phone = map.get("phone").toString();
                        mPhoneField.setText(phone);
                    }
                    if(map.get("sex") != null){
                        userSex = map.get("sex").toString();
                    }
                    if(map.get("bio") != null){
                        bio = map.get("bio").toString();
                        mBioField.setText(bio);
                    }
                    if(map.get("profileImageUrl") != null){
                        profileImageURL = map.get("profileImageUrl").toString();
                        switch (profileImageURL) {
                            case "default":
                                mProfileImage.setImageResource(R.drawable.defaultavatar);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageURL).into(mProfileImage);
                                break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void saveUserInformation() {
        name = mNameField.getText().toString();
        breed = mBreedSpinner.getSelectedItem().toString();
        dob = mDOBField.getText().toString();
        phone = mPhoneField.getText().toString();
        bio = mBioField.getText().toString();

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("breed", breed);
        userInfo.put("dob", dob);
        userInfo.put("phone", phone);
        userInfo.put("bio", bio);

        mDatabase.updateChildren(userInfo);
        if(resultUri != null) {
            final StorageReference filepath = FirebaseStorage.getInstance().getReference().child("profileImages").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = filepath.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map userInfo = new HashMap();
                            userInfo.put("profileImageUrl", uri.toString());
                            mDatabase.updateChildren(userInfo);

                            finish();
                            return;
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            finish();
                            return;
                        }
                    });
                }
            });
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            mProfileImage.setImageURI(resultUri);
        }
    }
}
