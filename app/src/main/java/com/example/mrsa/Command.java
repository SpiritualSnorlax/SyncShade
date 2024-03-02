package com.example.mrsa;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Command extends AppCompatActivity {

    protected Button openBtn;
    protected Button closeBtn;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        EdgeToEdge.enable(this);

        openBtn = findViewById(R.id.openBtn);
        closeBtn = findViewById(R.id.closeBtn);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Roller Shade Control");

        openFully();
        closeFully();

    }

    public void openFully() {
        openBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("App Request").setValue(1);
                myRef.child("Open-Close Fully").setValue(1);
                myRef.child("Open-Close Roller Shade").setValue("Opened");
                Toast.makeText(Command.this,"Opening", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void closeFully() {
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef.child("App Request").setValue(1);
                myRef.child("Open-Close Fully").setValue(0);
                myRef.child("Open-Close Roller Shade").setValue("Closed");
                Toast.makeText(Command.this, "Closing", Toast.LENGTH_SHORT).show();
            }
        });
    }
}