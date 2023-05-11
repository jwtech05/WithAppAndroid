package com.example.withapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.withapp.POJO.SearchRecyclerData;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<SearchRecyclerData> otherMemberData;

    public SearchAdapter(List<SearchRecyclerData> searchData){
        this.otherMemberData = searchData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView otherImage;
        public TextView otherNickName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            otherImage = itemView.findViewById(R.id.otherImage);
            otherNickName = itemView.findViewById(R.id.otherNickName);

        }
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_search_item,parent,false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {

        String base64Image = otherMemberData.get(position).getImagePath();
        byte[] decodedString = Base64.getDecoder().decode(base64Image);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.otherImage.setImageBitmap(bitmap);
        holder.otherNickName.setText(otherMemberData.get(position).getNickName());

    }

    @Override
    public int getItemCount() {
        return otherMemberData.size();
    }
}
