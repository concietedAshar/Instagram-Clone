package com.mrash.instagramclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private static final String TAG = "PostActivity";

    private ImageView close;
    private ImageView imageAdded;
    private TextView post;
    SocialAutoCompleteTextView description;
    private String imageUrl;

    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        init();
        close();
        cropImage();
        post();


    }


    private void imageUpload()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if(imageUri != null)
        {
            /**Create a storage reference to child Posts and saving post with name
             *  of current milliseconds.fileextension(image uploading from phone)
             */
            StorageReference filePath = FirebaseStorage.getInstance().getReference("Posts")
                    .child(System.currentTimeMillis()+"." +getFileExtension(imageUri)); //saving fileName

        //uploading image in post folder
            StorageTask uploadTask = filePath.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull  Task task) throws Exception {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();

                    }
                    return filePath.getDownloadUrl(); //it will get downloadable image url

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull  Task<Uri> task) {

                    Uri downloadUri = task.getResult(); // get returning url from StorageTask if successful

                    //convert that url in string because we have to put it in firebase database
                    imageUrl = downloadUri.toString();

                    //going to posts reference in database to save post detail
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

                    String postId = ref.push().getKey(); //it will generate a unique key using push()

                    //adding post data on the base of current user who is uploading post
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("postid",postId);
                    map.put("imageurl",imageUrl); //getting downloadable url which will later used to download image from firebase
                    map.put("description",description.getText().toString());
                    map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid()); //
                    ref.child(postId).setValue(map);

                    // Creating reference to hashtag object in firebase
                    DatabaseReference hashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    //social view seperate automatically hashtag from description and
                    // we are creating hashtag list if user has put multiple hashtag in desc...
                    List<String> hashTags = description.getHashtags();

                    //checking if hashtag is not empty then putting the hashtag in hashtag reference
                    // on the base of postId so we can see whose posts tags is this
                    if(!hashTags.isEmpty())
                    {
                        for(String tag:hashTags)
                        {
                            map.clear();
                            map.put("tag",tag.toLowerCase());
                            map.put("postid",postId);
                            hashTagRef.child(tag.toLowerCase()).child(postId).setValue(map);
                        }

                    }

                    //if post image and post detail added successfully then it will dismiss the pd...
                    progressDialog.dismiss();
                    //and take you to main activity...
                    startActivity(new Intent(PostActivity.this,com.mrash.instagramclone.MainActivity.class));
                    finish();

                }
                //if failed to upload
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else //if failed to select image or didn't get image
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "imageUpload: No Image Selected");
        }
    }

    //return file Extension type of image this function used in upload image()
    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }

    //post button to post the post with image description and other details
    private void post()
    {
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageUpload();
            }
        });
    }

    //using CropImage Library ->simply crop the image if you want to using crop image.
    private void cropImage()
    {
        CropImage.activity().start(PostActivity.this);

    }

    //Using cropImage Class  we will check if image is get from user successfully or not
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            //setting the image on the post activity after getting post with cropImge
            imageAdded.setImageURI(imageUri);
        }
        else
        {
            Toast.makeText(this, "Try Again - Error", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this,com.mrash.instagramclone.MainActivity.class));
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        //setting HashTag adapter to get hashtag from Description it is built in social View Class
        ArrayAdapter<Hashtag> hashtagAdapter = new HashtagArrayAdapter<>(getApplicationContext());
         // getting hashtag and showing as a suggestion of the samename hashtags
        FirebaseDatabase.getInstance().getReference().child("HashTags")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    hashtagAdapter.add(new Hashtag(dataSnapshot.getKey(),(int)snapshot.getChildrenCount()));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        description.setHashtagAdapter(hashtagAdapter);

    }

    //init
    private void init()
    {
        close = findViewById(R.id.close);
        imageAdded = findViewById(R.id.image_added);
        post = findViewById(R.id.post);
        description = findViewById(R.id.description);

    }
    //if not to upload simply close
    private void close()
    {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,com.mrash.instagramclone.MainActivity.class));
                finish();
            }
        });
    }
}