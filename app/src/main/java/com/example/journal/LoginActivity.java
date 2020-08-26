package com.example.journal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button signoutBtn;
    private Button loginBtn;
    private AutoCompleteTextView email;
    private EditText password;
    private String currentUserId;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("users");

    private ProgressBar progressBar;

    private PopupWindow popupWindow;
    private LayoutInflater layoutInflater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        signoutBtn = (Button) findViewById(R.id.signout_btn);
        loginBtn = (Button) findViewById(R.id.sign_in_btn);
        email =(AutoCompleteTextView) findViewById(R.id.input_email);
        password = (EditText) findViewById(R.id.input_password);
        progressBar = (ProgressBar) findViewById(R.id.login_progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);



        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,
                        SignoutActivity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginWithEmailPassword(email.getText().toString().trim(),
                        password.getText().toString().trim());
            }
        });
    }

    private void loginWithEmailPassword(String userEmail, String userPass) {
        if( !TextUtils.isEmpty(userEmail) && !TextUtils.isEmpty(userPass)){
            firebaseAuth.signInWithEmailAndPassword(userEmail, userPass)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
//                            Toast.makeText(LoginActivity.this, "Success login",
//                            Toast.LENGTH_SHORT).show();
                            user = firebaseAuth.getCurrentUser();
                            currentUserId = user.getUid();
                            Toast.makeText(LoginActivity.this, " user id is " +currentUserId, Toast.LENGTH_SHORT).show();
//

                                    Log.d(TAG, "userID " + User.getInstance().getCurrentUserId());

                            collectionReference
                                    .whereEqualTo("currentUserId", currentUserId)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                            @Nullable FirebaseFirestoreException error) {

                                            if(error != null) {
                                                progressBar.setVisibility(View.GONE);
                                                Log.d(TAG, "onEvent: "+ error);
                                                return ;
                                            }

                                            assert queryDocumentSnapshots != null;
                                            if( !queryDocumentSnapshots.isEmpty()){

                                                for ( QueryDocumentSnapshot snapshots: queryDocumentSnapshots){
                                                    User connectedUser = User.getInstance();
                                                    connectedUser.setCurrentUserId(snapshots.getString("currentUserId"));
                                                    connectedUser.setUsername(snapshots.getString("username"));
                                                    progressBar.setVisibility(View.GONE);
                                                    startActivity(new Intent(LoginActivity.this, PostActivity.class));

                                                }
                                            }else {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(LoginActivity.this, "queryDocumentSnapshot is empty", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.GONE);
                    loginFailPopup();
                    Log.d(TAG, "onFailure: "+e.getMessage());

                }
            });

        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Enter email and password ", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginFailPopup() {
        progressBar.setVisibility(View.GONE);
        View popupView = layoutInflater.inflate(R.layout.login_failed_popup, null,false);
        popupWindow = new PopupWindow(popupView ,600,400, true);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0,0);
    }
}