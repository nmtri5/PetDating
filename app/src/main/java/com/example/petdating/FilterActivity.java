package com.example.petdating;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class FilterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        final Spinner spinner = (Spinner) findViewById(R.id.dogbreed);
        android.widget.ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.dogbreed, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        mAuth = FirebaseAuth.getInstance();
        String userID = mAuth.getCurrentUser().getUid();

        SharedPreferences prefs = getSharedPreferences("mysettings", MODE_PRIVATE);
        if(prefs != null) {
            spinner.setSelection(prefs.getInt("spinnerSelection", 0));
        }

        Button apply = (Button) findViewById(R.id.apply);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences("mysettings", MODE_PRIVATE);
                int selectedPosition = spinner.getSelectedItemPosition();
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("spinnerSelection", selectedPosition);
                editor.putString("spinnerValue", spinner.getSelectedItem().toString());
                editor.apply();

                String userID = mAuth.getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                HashMap map = new HashMap();
                map.put("searchFilter", spinner.getSelectedItem().toString());
                ref.updateChildren(map);

                Intent intent = new Intent(FilterActivity.this, MainActivity.class);
                intent.putExtra("PET_BREED", spinner.getSelectedItem().toString());
                startActivity(intent);
            }
        });
    }
}
