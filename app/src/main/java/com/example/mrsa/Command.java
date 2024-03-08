package com.example.mrsa;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Command extends AppCompatActivity {

    protected Button openBtn;
    protected Button closeBtn;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://mrsa-test-services-default-rtdb.firebaseio.com/");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    String uid = currentUser.getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        EdgeToEdge.enable(this);

        openBtn = findViewById(R.id.openBtn);
        closeBtn = findViewById(R.id.closeBtn);

        openFully();
        closeFully();

    }

    public void openFully() {
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rollerShadeReference = databaseReference.child("Users").child(uid).child("Roller Shade Control");
                rollerShadeReference.child("App Request").setValue(1);
                rollerShadeReference.child("Open-Close Fully").setValue(1);
                rollerShadeReference.child("Open-Close Roller Shade").setValue("Opened");
                rollerShadeReference.child("State").setValue(1);
            }
        });
    }

    public void closeFully() {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rollerShadeReference = databaseReference.child("Users").child(uid).child("Roller Shade Control");
                rollerShadeReference.child("App Request").setValue(1);
                rollerShadeReference.child("Open-Close Fully").setValue(0);
                rollerShadeReference.child("Open-Close Roller Shade").setValue("Closed");
                rollerShadeReference.child("State").setValue(0);
            }
        });
    }
}