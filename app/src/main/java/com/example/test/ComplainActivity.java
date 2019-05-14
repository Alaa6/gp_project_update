package com.example.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ComplainActivity extends AppCompatActivity {

    Button btnCompalin;
    EditText complainText;

    private DatabaseReference complain ;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain);

         btnCompalin = findViewById(R.id.btnComplain);
        complainText = findViewById(R.id.complainText);
//Get Id Of The Post :
        Intent intent = getIntent();
        final String postId = intent.getStringExtra("PostId");


        complain = FirebaseDatabase.getInstance().getReference().child("Complains");

        btnCompalin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String complainContent =complainText.getText().toString();
//Get Id of our complain :
                String complainId = complain.child("Complains").push().getKey();
                Complain comp = new Complain(complainId,complainContent,postId,"1");
                complain.child(complainId).setValue(comp);
            }
        });





    }
}
