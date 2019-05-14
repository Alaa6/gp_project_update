package com.example.test;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CommentSecondActivity extends AppCompatActivity {
    private ImageButton post_comment;
    private EditText comment_input;
    private RecyclerView commentList;
    private DatabaseReference UserRef,PostRef,PostRefOnly;
    private String PostKey,CurrentUser_id;
    private FirebaseAuth mAuth;
    public Context context;
    Intent intent;
   // public List<CommentsSecond> comments_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_second);

        post_comment =findViewById(R.id.post_comment_btn);
        comment_input = findViewById(R.id.comment_input);
        commentList =  findViewById(R.id.comments_list);
        commentList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentList.setLayoutManager(linearLayoutManager);

        UserRef = FirebaseDatabase.getInstance().getReference().child("users");

//Get Post Key :
        intent =getIntent();
        PostKey = intent.getStringExtra("PostKey");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey).child("Comments");
        PostRefOnly = FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser_id = mAuth.getCurrentUser().getUid();


        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                PostRefOnly.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            String postDescription = dataSnapshot.child("desc").getValue().toString();
                            String userProfile = dataSnapshot.child("profile_image").getValue().toString();
                            String userProfileSec = dataSnapshot.child("image_uri").getValue().toString();
                            ValidateComment(postDescription,userProfile,userProfileSec);
                           // comment_input.setText("");

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

               // ValidateComment();
            }
        });

    }
///////////////////////////////////////////


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<CommentsSecond> options=
                new FirebaseRecyclerOptions.Builder<CommentsSecond>()
                        .setQuery(PostRef,CommentsSecond.class)
                        .build();
        final FirebaseRecyclerAdapter<CommentsSecond, CommentsViewHolder> adapter =
                new FirebaseRecyclerAdapter<CommentsSecond, CommentsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull CommentsSecond model) {

                        holder.comment.setText(model.getComment());
                        holder.date.setText(model.getDate());

                       // String profileImage = model.getImage_uri();
                    //    holder.setImage(profileImage);

                        String image = model.getProfile_image();
                        holder.setImageSec(image);
                    }

                    @NonNull
                    @Override
                    public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_comments_layout,parent,false);
                        context = parent.getContext();
                        CommentsViewHolder viewHolder= new CommentsViewHolder(view);
                        return viewHolder;                    }
                };
        commentList.setAdapter(adapter);
        adapter.startListening();

    }

    ////////////////////////////////////////////
    public  class CommentsViewHolder extends RecyclerView.ViewHolder {
        View mview;
        TextView comment,date;
        ImageView profileImage;
        ImageView profileImageSec;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            mview=itemView;
            comment = itemView.findViewById(R.id.comment_text);
            date = itemView.findViewById(R.id.comment_time);
            profileImageSec = mview.findViewById(R.id.comment_profile_image);
        }

       /* public void setImage(String imageDesc)
        {
            profileImage = mview.findViewById(R.id.comment_profile_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageDesc).into(profileImage);
        }*/
        public void setImageSec(String imageDesc)
        {
            //profileImageSec = mview.findViewById(R.id.comment_profile_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.profile);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(imageDesc).into(profileImageSec);
        }
    }

    private void ValidateComment(final String postDescription ,final String userProfile,final String userProfileSec ) {
        String commentText = comment_input.getText().toString();
        if (TextUtils.isEmpty(commentText)){
            Toast.makeText(this,"Please write comment",Toast.LENGTH_SHORT).show();

        }
        else{
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String  saveCurrentTime = currentTime.format(calFordTime.getTime());
            final String RandomKey = CurrentUser_id + saveCurrentDate + saveCurrentTime;
            HashMap commentMap = new HashMap();
            commentMap.put("Uid",CurrentUser_id);
            commentMap.put("comment",commentText);
            commentMap.put("date",saveCurrentDate);
            commentMap.put("time",saveCurrentTime);
            commentMap.put("Post Desc",postDescription);
            commentMap.put("User Profile",userProfile);
            commentMap.put("User ProfileSec",userProfileSec);
            commentMap.put("postId",PostKey);
            PostRef.child(RandomKey).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(CommentSecondActivity.this,"You have commented successfully",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(CommentSecondActivity.this,"Error",Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    }
