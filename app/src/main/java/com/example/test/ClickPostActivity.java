package com.example.test;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ClickPostActivity extends AppCompatActivity {
    private ImageView PostImage;
    private Button Btn_delete,Btn_edit;
    private TextView PostDescription;
    private  String PostKey,img,desc,CurrentUser_id,User_id;
private DatabaseReference ClickPostRef;
    private FirebaseAuth mAuth;
   public Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        setContentView(R.layout.activity_click_post);
PostImage = (ImageView) findViewById(R.id.post_img);
      //  mAuth = FirebaseAuth.getInstance();
     //   CurrentUser_id = mAuth.getCurrentUser().getUid();
PostDescription = (TextView) findViewById(R.id.text_description);
Btn_delete = (Button) findViewById(R.id.btn_delete);
Btn_edit = (Button) findViewById(R.id.btn_edit);

PostKey = getIntent().getExtras().get("PostKey").toString();
        Log.d("sssssssssssssssss",PostKey);
ClickPostRef = FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey);

//        Btn_delete.setVisibility(View.INVISIBLE);
       // Btn_edit.setVisibility(View.INVISIBLE);

ClickPostRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    desc = dataSnapshot.child("desc").getValue().toString();
                    img = dataSnapshot.child("image_uri").getValue().toString();
                //    User_id = dataSnapshot.child("uid").getValue().toString();


                    PostDescription.setText(desc);

                   // Picasso.with(ClickPostActivity.this).load(img).into(PostImage);
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.profile);
                    Glide.with(ClickPostActivity.this).applyDefaultRequestOptions(requestOptions).load(img).into(PostImage);


                  //  if (CurrentUser_id.equals(User_id)) {
                     //   Btn_delete.setVisibility(View.VISIBLE);
                      //  Btn_edit.setVisibility(View.VISIBLE);
                    //}
                   Btn_edit.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           EditCurrentPost(desc);
                       }
                   });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       Btn_delete.setOnClickListener(new View.OnClickListener() {
           @Override
            public void onClick(View view) {
                DeleteCurrentPost();
            }
        });
    }
/**/
    private void EditCurrentPost(String description) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");
        final EditText inputField = new EditText(ClickPostActivity.this);
        inputField.setText(description);
        builder.setView(inputField);
        builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ClickPostRef.child("desc").setValue(inputField.getText().toString());
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);
    }

    private void DeleteCurrentPost() {
        ClickPostRef.removeValue();
        Toast.makeText(this, "Post Has Deleted Successfully !!!!", Toast.LENGTH_SHORT).show();

        gotoMain();
    }
   public void gotoMain(){
        Intent intent = new Intent(ClickPostActivity.this, MainSecondActivity.class);
        startActivity(intent);
        finish();
    }
}
