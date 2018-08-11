package com.example.home.socialservicenetworkapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView commentsList;
    private ImageButton postCommentBtn;
    private EditText commentInputTxt;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;
    private String postKey, currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        postKey = getIntent().getExtras().get("PostKey").toString();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).child("Comments");

        commentsList = findViewById(R.id.comments_list);
        commentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        commentsList.setLayoutManager(linearLayoutManager);

        postCommentBtn = findViewById(R.id.post_comment_btn);
        commentInputTxt = findViewById(R.id.comment_input);

        postCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userName = dataSnapshot.child("username").getValue().toString();
                            ValidateComment(userName);
                            commentInputTxt.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(
                Comments.class, R.layout.all_comments_layout, CommentsViewHolder.class, PostsRef
        ) {
            @Override
            protected void populateViewHolder(CommentsViewHolder viewHolder, Comments model, int position) {
                viewHolder.setUsername(model.getUsername());
                viewHolder.setComment(model.getComment());
                viewHolder.setDate(model.getDate());
                viewHolder.setTime(model.getTime());
            }
        };
        commentsList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class CommentsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public CommentsViewHolder(View itemView) {

            super(itemView);

            mView = itemView;
        }

        public void setUsername(String username) {
            TextView myUserName = mView.findViewById(R.id.comment_username);
            myUserName.setText(" " + username + " ");
        }

        public void setComment(String comment) {
            TextView myComment = mView.findViewById(R.id.comment_text);
            myComment.setText(comment);
        }

        public void setDate(String date){
            TextView myDate = mView.findViewById(R.id.comment_date);
            myDate.setText(" on: " + date);
        }

        public void setTime(String time) {
            TextView myTime = mView.findViewById(R.id.comment_time);
            myTime.setText(" at: " + time);
        }

    }

    private void ValidateComment(String userName) {
        String commentText = commentInputTxt.getText().toString();
        if (TextUtils.isEmpty(commentText)) {
            Toast.makeText(this, "Please add a comment", Toast.LENGTH_SHORT).show();
        } else {
            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calFordDate.getTime());

            Calendar calFordTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calFordDate.getTime());

            final String RandomKey = currentUserId + saveCurrentDate + "-" + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid", currentUserId);
            commentsMap.put("comment", commentText);
            commentsMap.put("date", saveCurrentDate);
            commentsMap.put("time", saveCurrentTime);
            commentsMap.put("username", userName);
            PostsRef.child(RandomKey).updateChildren(commentsMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(CommentsActivity.this, "Comment saved", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(CommentsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }
}
