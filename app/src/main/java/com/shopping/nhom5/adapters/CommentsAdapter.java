package com.shopping.nhom5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shopping.nhom5.R;
import com.shopping.nhom5.models.Comment;
import com.shopping.nhom5.utils.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private List<Comment> commentsList;

    public CommentsAdapter(List<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        holder.commentText.setText(comment.getText());

        // Fetch user information based on userId from the comment
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(comment.getUserId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String avatarUrl = dataSnapshot.child("avatarUrl").getValue(String.class);

                    // Set the user's full name
                    String fullName = (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
                    holder.userName.setText(fullName.trim());

                    // Check if avatarUrl is valid
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Picasso.get()
                            .load(avatarUrl)
                            .transform(new CircleTransform())
                            .into(holder.userImage);
                    } else {
                        Picasso.get()
                                .load(R.drawable.ic_user)
                                .transform(new CircleTransform())
                                .into(holder.userImage);// Default image if not available
                    }
                } else {
                    holder.userName.setText("Anonymous");
                    Picasso.get()
                            .load(R.drawable.ic_user)
                            .transform(new CircleTransform())
                            .into(holder.userImage);// Default image if not available
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView userName, commentText;
        ImageView userImage;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.comment_user_name);
            commentText = itemView.findViewById(R.id.comment_text);
            userImage = itemView.findViewById(R.id.comment_user_image);
        }
    }

    // Method to fetch comments for a specific GameId
    public void fetchCommentsForGame(String gameId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("games").child(gameId).child("comments");

        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear(); // Clear the existing list
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentsList.add(comment);
                }
                notifyDataSetChanged(); // Notify the adapter to refresh the view
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }
} 