package com.luizmagno.portraitscrollingapp.utils;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luizmagno.portraitscrollingapp.AlbumActivity;
import com.luizmagno.portraitscrollingapp.R;

import java.util.ArrayList;

public class MusicAdapter extends BaseAdapter {

    private final ArrayList<Music> listMusics;
    private final Activity activity;

    public MusicAdapter(ArrayList<Music> musics, Activity activity) {
        this.listMusics = musics;
        this.activity = activity;
    }
    @Override
    public int getCount() {
        return listMusics.size();
    }

    @Override
    public Object getItem(int position) {
        return listMusics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View v = activity.getLayoutInflater().inflate(R.layout.item_list_musics, parent, false);

        TextView nameMusic = v.findViewById(R.id.nameMusicId);
        nameMusic.setText(listMusics.get(position).getNameMusic());

        return v;
    }
}
