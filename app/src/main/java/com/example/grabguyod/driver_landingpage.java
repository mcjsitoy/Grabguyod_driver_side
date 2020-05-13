package com.example.grabguyod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class driver_landingpage extends AppCompatActivity {

    TextView tv_fName, tv_tplate, tv_pNumber,tv_dLicense;
    Button bt_logout, bt_editPofile, bt_startduty;
    ArrayAdapter<String> adapter;
    DatabaseReference databaseReference, addDriverDB;
    FirebaseUser user;
    String uid, driverStat = "Available";
    List<String> itemlist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_landingpage);
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = user.getUid();
        itemlist = new ArrayList<>();


        tv_fName = findViewById(R.id.textview_Name);
        tv_tplate = findViewById(R.id.textView_Plate);
        tv_pNumber = findViewById(R.id.textView_PhoneNum);
        tv_dLicense = findViewById(R.id.textview_License);
        bt_logout = findViewById(R.id.button_offDuty);/*
        bt_editPofile = findViewById(R.id.button_editProfile);*/
        bt_startduty = findViewById(R.id.button_Start);


        databaseReference = FirebaseDatabase.getInstance().getReference("users");



        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String fname = dataSnapshot.child("drivers").child(uid).child("tb_FullName").getValue(String.class);
                String tplate = dataSnapshot.child("drivers").child(uid).child("licensePlate").getValue(String.class);
                String number = dataSnapshot.child("drivers").child(uid).child("tb_PhoneNumber").getValue(String.class);
                String license = dataSnapshot.child("drivers").child(uid).child("tb_DriverLicense").getValue(String.class);

                tv_fName.setText("Name: " + fname);
                tv_tplate.setText("Tricycle Plate: " + tplate);
                tv_pNumber.setText("Phone Number: " + number);
                tv_dLicense.setText("License Number " + license);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(driver_landingpage.this, "Logged Out", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(driver_landingpage.this, "Not Logged Out", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(driver_landingpage.this, Main3Activity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        bt_startduty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDriverToList();
                Intent intent = new Intent(driver_landingpage.this, driver_driveMode.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }

    private void addDriverToList(){
        addDriverDB = FirebaseDatabase.getInstance().getReference("table_AvailableDriver");
        addDriverDB.child(uid).child("Driver_Status").setValue(driverStat);

    }

}
