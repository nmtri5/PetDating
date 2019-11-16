package com.example.petdating;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private Button mRegister;
    private EditText mEmail, mPassword, mName, mBreed, mDOB, mPhone;
    private RadioGroup rdg;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        mRegister = (Button) findViewById(R.id.register);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        mName = (EditText) findViewById(R.id.name);
        mBreed = (EditText) findViewById(R.id.breed);
        mDOB = (EditText) findViewById(R.id.dob);
        mPhone = (EditText) findViewById(R.id.phone);

        rdg = (RadioGroup) findViewById(R.id.rdg);

        mRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int selectedID = rdg.getCheckedRadioButtonId();
                final RadioButton radioButton = (RadioButton) findViewById(selectedID);

                if(radioButton.getText() == null){
                    return;
                }
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                final String name = mName.getText().toString();
                final String dob = mDOB.getText().toString();
                final String breed = mBreed.getText().toString();
                final String phone = mPhone.getText().toString();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "Unsuccessful", Toast.LENGTH_LONG).show();
                        } else {
                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance()
                                    .getReference().child("Users")
                                    .child(userID);
                            DatabaseReference currentUserDbName = FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(userID)
                                    .child("name");
                            currentUserDbName.setValue(name);
                            DatabaseReference currentUserDbBreed = FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(userID)
                                    .child("breed");
                            currentUserDbBreed.setValue(breed);
                            DatabaseReference currentUserDbPhone = FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(userID)
                                    .child("phone");
                            currentUserDbPhone.setValue(phone);
                            DatabaseReference currentUserDbDob = FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(userID)
                                    .child("dob");
                            currentUserDbDob.setValue(dob);
                            DatabaseReference currentUserDbFilter = FirebaseDatabase.getInstance()
                                    .getReference().child("Users").child(userID)
                                    .child("searchFilter");
                            currentUserDbFilter.setValue("default");
                            Map userInfo = new HashMap<>();
                            userInfo.put("name", name);
                            userInfo.put("sex", radioButton.getText().toString());
                            userInfo.put("profileImageUrl", "default");
                            userInfo.put("searchFilter", "All");

                            currentUserDb.updateChildren(userInfo);

                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}
