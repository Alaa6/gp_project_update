package com.example.test;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditQuestionCommentActivity extends AppCompatActivity
{
    private Button btnSaveComment ;
    private DatabaseReference  CommentRef ,savedComment,UserRef;
    private String currentUserId ;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_comment);

        /*---------------------INTENT DATA-------------------------*/
        Bundle bundle =getIntent().getExtras();
        final String CommentId =bundle.getString("QuestionCommentKey");
        final String QuestionId =bundle.getString("QuestionKey");
        final String selectedArea = bundle.getString("SelectedArea");
        final String selectedCategory = bundle.getString("SelectedCategory");



        mAuth=FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        //UserRef= FirebaseDatabase.getInstance().getReference().child("users").child(currentUserId);
        CommentRef=FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("Questions").child(selectedArea).child(selectedCategory).child("categoryQuestions").child(QuestionId).child("QuestionCategoryComments").child(CommentId);
        savedComment=FirebaseDatabase.getInstance().getReference().child("AbuEl3orifDB").child("SavedQuestionComment");
        btnSaveComment =findViewById(R.id.btn_savedComment);
        btnSaveComment.setOnClickListener(new View.OnClickListener() {
            final ArrayList<String>  SavedComments =new ArrayList<String>();
            @Override
            public void onClick(View v) {

                if (!SavedComments.contains(CommentId))
                {
                    CommentRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String CommentContent  =dataSnapshot.child("questionCommentContent").getValue().toString();
                            String CommentDate  =dataSnapshot.child("questionCommentDate").getValue().toString();
                            String CommentTime  =dataSnapshot.child("questionCommentTime").getValue().toString();
                            String Comment_UserName  =dataSnapshot.child("questionCommentUserName").getValue().toString();
                            String Comment_UserName_pp =dataSnapshot.child("questionCommentUserPP").getValue().toString();

                            savedComment.child(currentUserId).child(CommentId).child("questionCommentContent").setValue(CommentContent);
                            savedComment.child(currentUserId).child(CommentId).child("questionCommentDate").setValue(CommentDate);
                            savedComment.child(currentUserId).child(CommentId).child("questionCommentTime").setValue(CommentTime);
                            savedComment.child(currentUserId).child(CommentId).child("questionCommentUserName").setValue(Comment_UserName);
                            savedComment.child(currentUserId).child(CommentId).child("questionCommentUserPP").setValue(Comment_UserName_pp);







                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    SavedComments.add(CommentId);
                    Toast.makeText(EditQuestionCommentActivity.this, "New comment is saved ...", Toast.LENGTH_SHORT).show();



                }

                else
                {
                    Toast.makeText(EditQuestionCommentActivity.this, "This comment is saved already ! ..", Toast.LENGTH_SHORT).show();
                }



            }
        });
    }
}
