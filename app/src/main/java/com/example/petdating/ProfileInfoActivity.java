package com.example.petdating;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class ProfileInfoActivity extends AppCompatActivity {

    TextView name, breed, bio;
    ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        Intent i = getIntent();
        Cards pet = (Cards)i.getSerializableExtra("userinfo");

        name = findViewById(R.id.name);
        breed = findViewById(R.id.breed);
        bio = findViewById(R.id.bio);
        avatar = findViewById(R.id.avatar);

        if(pet.getProfileImageUrl().equals("default")) {
            Glide.with(getApplication()).load(ResourcesCompat.getDrawable(getResources(), R.drawable.defaultavatar, null)).into(avatar);
        } else {
            Glide.with(getApplication()).load(pet.getProfileImageUrl()).override(1000, 1000).into(avatar);
        }

        name.setText(pet.getName());
        breed.setText(pet.getBreed());
        bio.setText(pet.getBio());
    }
}
