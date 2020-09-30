package com.luizmagno.portraitscrollingapp.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.luizmagno.portraitscrollingapp.AlbumActivity;
import com.luizmagno.portraitscrollingapp.R;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyView> {

    private ArrayList<Album> list;
    Activity activity;

    public static class MyView extends RecyclerView.ViewHolder {

        TextView titleAlbum, qntdMusics;
        ImageView capaAlbum;

        public MyView(View view) {
            super(view);

            titleAlbum = view.findViewById(R.id.titleAlbumId);
            capaAlbum = view.findViewById(R.id.capaAlbumId);
            qntdMusics = view.findViewById(R.id.numOfMusicsId);

        }
    }

    public AlbumAdapter(ArrayList<Album> horintalList, Activity activity) {
        this.list = horintalList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_albuns, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyView holder, int position) {

        final Album album = list.get(position);
        holder.titleAlbum.setText(album.getNameAlbum());
        if (album.getPathCapaAlbum().equals("")){
            holder.capaAlbum.setImageDrawable(ResourcesCompat.getDrawable(activity.getResources(), R.drawable.ic_warning, null));
        } else {
            holder.capaAlbum.setImageURI(Uri.parse(album.getPathCapaAlbum()));
        }

        holder.capaAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, AlbumActivity.class);
                intent.putExtra("pathAlbum", album.getPathAlbum());
                intent.putExtra("titleAlbum", album.getNameAlbum());
                intent.putExtra("capaAlbum", album.getPathCapaAlbum());
                startActivityWithTransition(holder.capaAlbum, intent);

            }
        });

        String qnt = album.getNumOfMusics()+" Músicas";
        holder.qntdMusics.setText(qnt);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void startActivityWithTransition(View v, Intent intent) {
        //Transição com elemento comum
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(activity, v, "transform");
            activity.startActivity(intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

}
