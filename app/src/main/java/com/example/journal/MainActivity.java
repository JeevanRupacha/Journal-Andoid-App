package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.journal.model.JournalDetails;
import com.example.journal.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    private Button getStarted;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseUser user;
    private CollectionReference collectionReference = db.collection("users");
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();


       authStateListener = new FirebaseAuth.AuthStateListener() {
           @Override
           public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

               user = firebaseAuth.getCurrentUser();
               if(user != null){
                   Log.d(TAG, "onAuthStateChanged: called ");
                   user = firebaseAuth.getCurrentUser();
                   String currentUserId = user.getUid();
                   Log.d(TAG, "onAuthStateChanged: current user user id is " + currentUserId);
                   collectionReference.whereEqualTo("currentUserId", currentUserId)

                           .addSnapshotListener(new EventListener<QuerySnapshot>() {
                               @Override
                               public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                   Log.d(TAG, "onEvent: called");
                                   if( error != null){
                                       return ;
                                   }

                                   assert value != null;
                                   if(!value.isEmpty()){

                                       for(QueryDocumentSnapshot snapshot : value){
                                           User user = User.getInstance();
                                           user.setUsername(snapshot.getString("username"));
                                           user.setCurrentUserId(snapshot.getString("currentUserId"));

                                           startActivity(new Intent(MainActivity.this, PostListActivity.class));
                                           finish();
                                       }

                                   }
                               }
                           });
               }else{

               }
           }
       };



        getStarted = (Button) findViewById(R.id.get_started);
        getStarted.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.get_started :
                //after get started
                //start login activity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                Toast.makeText(this, "get stared ", Toast.LENGTH_SHORT).show();
                break;
            default: break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth != null){
        firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}