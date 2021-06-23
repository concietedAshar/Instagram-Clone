package com.mrash.instagramclone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowId;
import android.widget.Button;
import android.widget.ListView;
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
import com.mrash.instagramclone.Model.User;
import com.mrash.instagramclone.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFragment) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        firebaseUser  = FirebaseAuth.getInstance().getCurrentUser();

        User user = mUsers.get(position);
        holder.follow.setVisibility(View.VISIBLE);
        holder.userName.setText(user.getUsername());
        holder.fullName.setText(user.getName());
        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageProfile);
        isFollowed(user.getId(),holder.follow);

        if(user.getId().equals(firebaseUser.getUid()))
        {
            holder.follow.setVisibility(View.GONE);
        }
        //database ->create a branch called follow then under that id current user and
        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.follow.getText().toString().equals("follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("follow").child((firebaseUser.getUid()))
                            .child("following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getId()).child("folowers").child(firebaseUser.getUid()).setValue(true);
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference().child("follow").child((firebaseUser.getUid()))
                            .child("following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("follow")
                            .child(user.getId()).child("folowers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    private void isFollowed(String id, Button follow) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                if(snapshot.child(id).exists())
                {
                    follow.setText("following");
                }
                else
                    follow.setText("follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public CircleImageView imageProfile;
        public TextView userName;
        public TextView fullName;
        public Button follow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            init();
        }
        private void init()
        {
            imageProfile = itemView.findViewById(R.id.image_profile);
            userName = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.full_name);
            follow  = itemView.findViewById(R.id.btn_follow);
        }
    }
}
