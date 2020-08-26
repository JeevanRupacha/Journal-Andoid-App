package com.example.journal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.journal.model.JournalDetails;
import com.example.journal.model.User;
import com.example.journal.recyclerviewAdapter.ListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    private static final String TAG = "PostListActivity";
    private ArrayList<JournalDetails> mDataset;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private CollectionReference collectionReference;

    //Recycler view
    private RecyclerView recyclerView;
    private RecyclerView.Adapter listAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_list_activity);

        firebaseAuth =  FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        mDataset = new ArrayList<>();



        collectionReference = db.collection("User_details");

        fetchJournalData();


        Log.d("PostListActivity", "arraylist " + mDataset);

        recyclerView = (RecyclerView) findViewById(R.id.post_list_recycler);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);





    }

    private synchronized void fetchJournalData() {
        if(firebaseAuth != null && user != null){

            Log.d(TAG, "mydebug ");
            collectionReference
                    //TODO make sure selected user post shown not all post
                    .whereEqualTo("userId",user.getUid())
                    .orderBy("timeAdded", Query.Direction.DESCENDING)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            if( queryDocumentSnapshots.isEmpty()){
                                Log.d(TAG, "onSuccess: queryDocument is Empty");
//                               return ;
                            }else{
                                for(QueryDocumentSnapshot snapshot : queryDocumentSnapshots){
//                                    String title = snapshot.getString("title");
//                                    String thought = snapshot.getString("thought");
//                                    String userId =snapshot.getString("userId");
//                                    String imageUrl =snapshot.getString("imageUrl");
//                                    String userName =snapshot.getString("userName");
//                                    Timestamp timeAdded =snapshot.getTimestamp("timeAdded");
//                                    JournalDetails journalDetails = new JournalDetails(title, thought, userId,imageUrl,userName,timeAdded);
                                    JournalDetails journalDetails = snapshot.toObject(JournalDetails.class);
                                    mDataset.add(journalDetails);


                                    Log.d(TAG, "onSuccess: " + journalDetails.getUserName());}

                            }

                            listAdapter = new ListAdapter(mDataset);
                            recyclerView.setAdapter(listAdapter);
                            listAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onSuccess: array " + mDataset);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: "+ e);
                            Toast.makeText(PostListActivity.this, "Fail to fetch data from firestore " +e , Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.post_list_menu, m);

        return super.onCreateOptionsMenu(m);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_post:
                //adding new post
                if(firebaseAuth != null && user != null)
                {
                    startActivity(new Intent(PostListActivity.this, PostActivity.class));
                    Toast.makeText(this, "add post", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.menu_dropdown_signout:
                //signout from activity
                if(firebaseAuth != null && user!= null){
                    firebaseAuth.signOut();
                    startActivity(new Intent(PostListActivity.this, LoginActivity.class));
                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }
}