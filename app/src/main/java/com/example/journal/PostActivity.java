package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.journal.model.User;
import com.example.journal.model.JournalDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int GALLERY_INTENT_CODE = 1;
    private static final String TAG = "PostActivity";
    private ImageView profileImage, cameraImage;
    private TextView nameTextView, dateTextView;
    private EditText inputTitle, inputThoughts;
    private ProgressBar progressBar;
    private Button submitButton;
//    private User user;

    private String currentUserId;
    private String currentUsername;

    // Firebase Firestore variables
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("User_details");
    private StorageReference storageReference;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser firebaseUser;
    private Uri imageUri;


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        profileImage = (ImageView) findViewById(R.id.profile_img);
        cameraImage = (ImageView) findViewById(R.id.camera_btn);
        nameTextView = (TextView) findViewById(R.id.name_textView);
        dateTextView = (TextView) findViewById(R.id.post_date);
        inputTitle = (EditText) findViewById(R.id.editText_title);
        inputThoughts = (EditText) findViewById(R.id.editText_thoughts);
        progressBar = (ProgressBar) findViewById(R.id.post_progressbar);
        submitButton = (Button) findViewById(R.id.submit_post);

        cameraImage.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        if(User.getInstance() != null){
            currentUserId = User.getInstance().getCurrentUserId();
            currentUsername = User.getInstance().getUsername();
            nameTextView.setText(currentUsername);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();

                if( firebaseUser != null){

                    //authenticated user

                }else{
                    //no user 
                }
            }
        };



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.camera_btn :
                //camera button clicked upload image
                //create camera implicit intent
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_INTENT_CODE);
                break;
            case R.id.submit_post:
                //submit button is clicked
                submitJournal();
                break;
            default: break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT_CODE && resultCode == RESULT_OK){
            if( null != data){
              imageUri = data.getData();
              profileImage.setImageURI(imageUri);
            }
        }
    }

    private void submitJournal() {
        progressBar.setVisibility(View.VISIBLE);

        String title = inputTitle.getText().toString().trim();
        String thoughts = inputThoughts.getText().toString().trim();

        if(!TextUtils.isEmpty(title)
        && !TextUtils.isEmpty(thoughts)
        && imageUri != null){
            //TODO save images in storage
            uploadImage(imageUri, title, thoughts);

//            Map<String,String> userDetails = new HashMap<>();
//            userDetails.put("")
//            collectionReference.add()
        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Input Field should not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(final Uri imageUri, final String title, final String thoughts) {
        final StorageReference profileImageFile = storageReference
                .child("profile_images")
                .child("image"+ Timestamp.now().getSeconds() + ".jpg");

                 profileImageFile.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        profileImageFile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //TODO save journal details in collection
                                if (uri != null){
                                uploadJournalDetails(uri, title, thoughts);
                                finish();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(PostActivity.this, "Failed to save journal details in firestore ", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PostActivity.this, "Failed to save Image file ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadJournalDetails(Uri uri, String title , String thoughts) {
        Log.d(TAG, "uploadJournalDetails: is called " );

        Timestamp timestamp = new Timestamp(new Date());

        //setter for journalDetails
        String imageUri = uri.toString();
        JournalDetails journalDetails = new JournalDetails();
        journalDetails.setImageUrl(imageUri);
        journalDetails.setTitle(title);
        journalDetails.setThought(thoughts);
        journalDetails.setTimeAdded(timestamp);
        journalDetails.setUserName(currentUsername);
        journalDetails.setUserId(currentUserId);


       collectionReference.add(journalDetails)
               .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                   @Override
                   public void onComplete(@NonNull Task<DocumentReference> task) {
                       progressBar.setVisibility(View.GONE);
                       Intent intent = new Intent(PostActivity.this, PostListActivity.class);
                       startActivity(intent);
                       //TODO go to all post page after success save of data of journal
                   }
               }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Toast.makeText(PostActivity.this, "Failed to save journal details ", Toast.LENGTH_SHORT).show();
           }
       });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}