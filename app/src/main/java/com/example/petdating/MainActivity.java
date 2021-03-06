package com.example.petdating;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.petdating.Matches.MatchesActivity;
import com.example.petdating.Utils.SendNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.onesignal.OneSignal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Cards cards_data[];
    private ArrayAdapter arrayAdapter;
    private int i;
    private Button mSettings;
    private FirebaseAuth mAuth;
    private String currentUid;
    private DatabaseReference UserDb;
    List<Cards> row_items;

    SwipeFlingAdapterView flingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();

        OneSignal.startInit(this).init();
        OneSignal.setSubscription(true);
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(currentUid).child("notificationKey").setValue(userId);
            }
        });
        OneSignal.setInFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification);

        new SendNotification("test", "heading", null);

        UserDb = FirebaseDatabase.getInstance().getReference().child("Users");

        checkUserSex();

        row_items = new ArrayList<Cards>();

        arrayAdapter = new com.example.petdating.ArrayAdapter(this, R.layout.item, row_items);

        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        mSettings = (Button) findViewById(R.id.settings);
        mSettings.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return;
            }
        });

        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                row_items.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Cards object = (Cards) dataObject;
                String userId = object.getUserID();
                UserDb.child(userId).child("connections").child("nope").child(currentUid).setValue(true);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards object = (Cards) dataObject;
                String userId = object.getUserID();
                UserDb.child(userId).child("connections").child("yep").child(currentUid).setValue(true);
                isConnectionMatch(userId);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {

            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Intent intent = new Intent(MainActivity.this, ProfileInfoActivity.class);
                intent.putExtra("userinfo", (Serializable) dataObject);
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()){
            case R.id.filter:
                Intent intent1 = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    private void isConnectionMatch(String userId) {
        DatabaseReference currentUserConnectionsDb = UserDb.child(currentUid).child("connections").child("yep").child(userId);
        currentUserConnectionsDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(MainActivity.this, "new Connection", Toast.LENGTH_LONG).show();

                    String key = FirebaseDatabase.getInstance().getReference().child("Chat").push().getKey();
                    UserDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUid).setValue(true);
                    UserDb.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUid).child("chatId").setValue(key);
                    UserDb.child(currentUid).child("connections").child("matches").child(dataSnapshot.getKey()).setValue(true);
                    UserDb.child(currentUid).child("connections").child("matches").child(dataSnapshot.getKey()).child("chatId").setValue(key);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    static void makeToast(Context ctx, String s) {
        Toast.makeText(ctx, s, Toast.LENGTH_SHORT).show();
    }

    private String userSex, oppositeUserSex;

    public void checkUserSex() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference usersDb = UserDb.child(user.getUid());
        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("sex").getValue() != null) {
                        userSex = dataSnapshot.child("sex").getValue().toString();
                        switch (userSex) {
                            case "Male":
                                oppositeUserSex = "Female";
                                break;
                            case "Female":
                                oppositeUserSex = "Male";
                                break;
                        }
                        if (dataSnapshot.child("searchFilter").getValue().toString().equals("All")) {
                            getOppositeSexUsers();
                        } else {
                            getOppositeUserSexWithFilter();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getOppositeSexUsers() {
        UserDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    try {
                        if (dataSnapshot.child("sex").getValue() != null) {
                                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUid)
                                        && !dataSnapshot.child("connections").child("yep").hasChild(currentUid)
                                        && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)) {
                                    String profileImageUrl = "default";
                                    if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                                    }
                                    Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl, dataSnapshot.child("dob").getValue().toString(), dataSnapshot.child("breed").getValue().toString(), dataSnapshot.child("bio").getValue().toString());
                                    row_items.add(item);
                                    arrayAdapter.notifyDataSetChanged();
                                }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getOppositeUserSexWithFilter() {
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users");
        SharedPreferences settings = getSharedPreferences("mysettings", MODE_PRIVATE);
        final String StoredValue=settings.getString("spinnerValue", "All");
        currentUserDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                try {
                    if (dataSnapshot.child("sex").getValue() != null) {
                        if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("nope").hasChild(currentUid)
                                && !dataSnapshot.child("connections").child("yep").hasChild(currentUid)
                                && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex)
                                && dataSnapshot.child("breed").getValue().toString().equals(StoredValue)) {
                            String profileImageUrl = "default";
                            if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")) {
                                profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                            }
                            Cards item = new Cards(dataSnapshot.getKey(), dataSnapshot.child("name").getValue().toString(), profileImageUrl, dataSnapshot.child("dob").getValue().toString(), dataSnapshot.child("breed").getValue().toString(), dataSnapshot.child("bio").getValue().toString());
                            row_items.add(item);
                            arrayAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void logoutUser(View view) {
        OneSignal.setSubscription(false);
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        return;
    }

    public void goToMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        startActivity(intent);
        return;
    }
}

