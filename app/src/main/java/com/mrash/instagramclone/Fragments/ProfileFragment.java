package com.mrash.instagramclone.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrash.instagramclone.Adapter.PhotoAdapter;
import com.mrash.instagramclone.EditProfileActivity;
import com.mrash.instagramclone.Model.Post;
import com.mrash.instagramclone.Model.User;
import com.mrash.instagramclone.OptionActivity;
import com.mrash.instagramclone.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private List<Post> myPhotoList;

    private CircleImageView imageProfile;
    private ImageView options;
    private TextView posts;
    private TextView followers;
    private TextView following;
    private TextView fullName;
    private TextView bio;
    private TextView username;

    private ImageView myPictures;
    private ImageView savedPictures;
    private Button editProfile;

    private FirebaseUser fUser;

    private String profileId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      View view = inflater.inflate(R.layout.fragment_profile, container, false);

      fUser = FirebaseAuth.getInstance().getCurrentUser();
      profileId = fUser.getUid();

      editProfile =view.findViewById(R.id.edit_profile);
      imageProfile = view.findViewById(R.id.image_profile);
      options = view.findViewById(R.id.options);
      posts = view.findViewById(R.id.posts);
      followers = view.findViewById(R.id.followers);
      following =view.findViewById(R.id.followings);
      fullName = view.findViewById(R.id.full_name);
      bio = view.findViewById(R.id.bio);
      username = view.findViewById(R.id.username);
      myPictures = view.findViewById(R.id.my_pictures);
      savedPictures = view.findViewById(R.id.saved_pictures);
      recyclerView = view.findViewById(R.id.recycler_view_pictures);
      recyclerView.setHasFixedSize(true);
      recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
      myPhotoList = new ArrayList<>();
      photoAdapter = new PhotoAdapter(getContext(),myPhotoList);
      recyclerView.setAdapter(photoAdapter);

      userInfo();
      getFollowerAndFollowingCount();
      getPostCount();
      getMyPhotos();
      if(profileId.equals(fUser.getUid()))
      {
          editProfile.setText("Edit Profile");
      }else {
          checkFollowingStatus();
      }

      setEditProfile();

      options.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              startActivity(new Intent(getContext(), OptionActivity.class));
          }
      });


        return view;
    }



    /**
     * Edit Profile Button
     */
    private void setEditProfile()
    {
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = editProfile.getText().toString();
                if(buttonText.equals("Edit Profile"))
                {
                    // goto edit profile
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }else
                {
                    if(buttonText.equals("follow"))
                    {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following")
                                .child(profileId).setValue(true);

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers")
                                .child(fUser.getUid()).setValue(true);
                    }else
                    {
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following")
                                .child(profileId).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers")
                                .child(fUser.getUid()).removeValue();
                    }

                }
            }
        });

    }

    /**
     * Get All photos on the base of current user profile id
     */
    private void getMyPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myPhotoList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    //post is made by current user then add to list
                    if(post.getPublisher().equals(profileId))
                    {
                        myPhotoList.add(post);

                    }
                }
                Collections.reverse(myPhotoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

    /**
     * Checking the follow or following status to set  Edit Profile Button to Follow or following Button
     */
    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid()).child("following")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.child(profileId).exists())
                        {
                            editProfile.setText("following");
                        }else
                        {
                            editProfile.setText("follow");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    /**
     * Get post Counts and display on the profile using profile id
     */
    private void getPostCount()
    {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);

                    if(post.getPublisher().equals(profileId))
                    {
                        counter++;
                    }
                }
                posts.setText(String.valueOf(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onCancelled:"+ error.getMessage());

            }
        });
    }

    /**
     * Get Followers and Following Count using getChildren()
     */
    private void getFollowerAndFollowingCount()
    {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId);
        ref.child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(snapshot.getChildrenCount()+"");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(snapshot.getChildrenCount()+"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     *  getting user info then set on profile Screen
     */
    private void userInfo()
    {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User user = snapshot.getValue(User.class);
                        Picasso.get().load(user.getImageurl()).into(imageProfile);
                        username.setText(user.getUsername());
                        fullName.setText(user.getName());
                        bio.setText(user.getBio());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}