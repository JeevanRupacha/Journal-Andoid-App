package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.journal.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignoutActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private CollectionReference collectionReference;

    private EditText username, email;
    private EditText password;
    private Button createAccount;
    private ProgressBar progressBar;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signout_activity);

        username = (EditText) findViewById(R.id.username_create);
        email = (EditText) findViewById(R.id.input_email_create);
        password = (EditText)findViewById(R.id.enter_password_create);
        createAccount = (Button) findViewById(R.id.create_account_btn);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users");


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // I am not re register currentuser
                if(currentUser != null){
                    //user already logged in

                }else{
                    //user is not logged in
                }

            }
        };


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( !TextUtils.isEmpty(email.getText().toString().trim())
                && !TextUtils.isEmpty(password.getText().toString().trim())
                && !TextUtils.isEmpty(username.getText().toString().trim())){
                    if( password.getText().toString().trim().length() <= 6 ) {
                        Toast.makeText(SignoutActivity.this, "Password must be grater than 6 character", Toast.LENGTH_SHORT).show();
                        return ;
                    }

//                    user = new User(email.getText().toString().trim(), password.getText().toString().trim(),username.getText().toString().trim());
                    user = User.getInstance();
                    User.getInstance().setEmail(email.getText().toString().trim());
                    User.getInstance().setPassword(password.getText().toString().trim());
                    User.getInstance().setUsername(username.getText().toString().trim());
                    createUserAccount();
                }else{
                    Toast.makeText(SignoutActivity.this, "Empty field is not allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void createUserAccount(){
        if( !TextUtils.isEmpty(User.getInstance().getEmail())
        && !TextUtils.isEmpty(User.getInstance().getPassword())
        && !TextUtils.isEmpty(User.getInstance().getUsername())){
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(User.getInstance().getEmail(), User.getInstance().getPassword())

                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                //user is created successfully
                                Map<String, String> newUser = new HashMap<>();
                                newUser.put("email",user.getEmail());
                                newUser.put("password",user.getPassword());
                                newUser.put("username",user.getUsername());
                                newUser.put("dateOfBirth",user.getDateOfBirth());
                                newUser.put("birthPlace",user.getBirthPlace());
                                newUser.put("profileImageId",user.getProfileImageId());


                                currentUser = firebaseAuth.getCurrentUser();
                                user.setCurrentUserId(currentUser.getUid());
                                newUser.put("currentUserId",currentUser.getUid());

                                collectionReference.add(newUser)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                //user data is saved success
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                progressBar.setVisibility(View.GONE);
                                                                if(Objects.requireNonNull(task.getResult()).exists()){
                                                                String username = task.getResult().getString("username");
                                                                Intent intent = new Intent(getApplicationContext(), PostActivity.class);
                                                                intent.putExtra("username",username);
                                                                startActivity(intent);
                                                                }
                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignoutActivity.this, "Fail to save data user " + e, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                //something went wrong
                            }

                        }
                    }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("sign out", "onFailure: " + e);
                    Toast.makeText(SignoutActivity.this, "Failed to create user  " + e, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}