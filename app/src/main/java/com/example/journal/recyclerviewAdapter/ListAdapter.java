package com.example.journal.recyclerviewAdapter;

import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.journal.R;
import com.example.journal.model.JournalDetails;
import com.google.firebase.Timestamp;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.MyViewHolder> {

    private ArrayList<JournalDetails> mDataSet;

    public ListAdapter(ArrayList<JournalDetails> mDataSet) {
        this.mDataSet = mDataSet;
    }

    @NonNull
    @Override
    public ListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_component_post_list,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListAdapter.MyViewHolder holder, int position) {
      if( mDataSet != null){
          Picasso.get()
                  .load(Uri.parse(mDataSet.get(position).getImageUrl()))
                  .into(holder.profileImg);
//          holder.profileImg.setImageURI(Uri.parse(mDataSet.get(position).getImageUrl()));
          holder.title.setText(mDataSet.get(position).getTitle());
          holder.thoughts.setText(mDataSet.get(position).getThought());
//          holder.date.setText(mDataSet.get(position).getTimeAdded());
//          int date = mDataSet.get(position).getTimeAdded();
//          String addedTime = date.toString();
          String timeAgo = (String)DateUtils.getRelativeTimeSpanString(mDataSet.get(position)
                  .getTimeAdded()
                  .getSeconds() * 1000);
          holder.date.setText(timeAgo);
      }

    }

    @Override
    public int getItemCount() {
        if( mDataSet != null){
        return mDataSet.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg;
        TextView title,thoughts,date;
        public MyViewHolder(@NonNull View view) {
            super(view);

            profileImg = view.findViewById(R.id.list_profile_img);
           title= view.findViewById(R.id.list_title);
           thoughts = view.findViewById(R.id.list_thoughts);
           date = view.findViewById(R.id.list_date_added);
        }
    }
}