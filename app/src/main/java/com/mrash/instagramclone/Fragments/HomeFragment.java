package com.mrash.instagramclone.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mrash.instagramclone.Adapter.PostAdapter;
import com.mrash.instagramclone.Model.Post;
import com.mrash.instagramclone.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private List<String> followingList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Started");

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //initializing PostList
        postList = new ArrayList<>();
        //initializing following List - >will add to this if person has follow someone
        followingList = new ArrayList<>();

        recyclerViewPosts = view.findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true); // new post at top
        linearLayoutManager.setReverseLayout(true);
        //setting linear layout of post on recycler view
        recyclerViewPosts.setLayoutManager(linearLayoutManager);

        //setting posts on adapter

        postAdapter = new PostAdapter(getContext(),postList);
        recyclerViewPosts.setAdapter(postAdapter);


        //this will check for those following peoples
        checkFollowingUser();
        return view;
    }

    /**
     * Check if User is following Someone
     */
    private void checkFollowingUser() {
        Log.d(TAG, "checkFollowingUser: Called");
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                Log.d(TAG, "onDataChange: Getting Following List");
                followingList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Log.d(TAG, "onDataChange: getting following list");
                    followingList.add(dataSnapshot.getKey());
                }
                Log.d(TAG, "onDataChange: going to read post for followings");
                readPosts(); // get Following people post on Home Activity

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: Started");

            }
        });
    }

    /**
     * Checking and Reading Follow people post and set it on Home Screen using post Adapter
     */
    private void readPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Post post = dataSnapshot.getValue(Post.class);
                    for(String id : followingList)
                    {
                        if(post.getPublisher().equals(id))
                        {
                            postList.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}