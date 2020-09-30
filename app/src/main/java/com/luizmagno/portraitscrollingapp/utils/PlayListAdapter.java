package com.luizmagno.portraitscrollingapp.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.luizmagno.portraitscrollingapp.R;

import java.util.ArrayList;

public class PlayListAdapter extends BaseAdapter {

    private final ArrayList<Music> listMusics;
    private final Activity activity;
    private int currentPlaying;
    private boolean stopped = true;
    private boolean paused = false;

    public PlayListAdapter(ArrayList<Music> musics, Activity activity) {
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
        View v = activity.getLayoutInflater().inflate(R.layout.item_playlist, parent, false);

        TextView nameMusic = v.findViewById(R.id.nameMusicId);
        nameMusic.setText(listMusics.get(position).getNameMusic());
        nameMusic.setTextColor(Color.BLACK);

        LottieAnimationView playingAnimation = v.findViewById(R.id.playingAnimationId);
        playingAnimation.setBackgroundColor(activity.getResources().getColor(R.color.background_album));
        playingAnimation.setVisibility(View.INVISIBLE);

        if (!stopped && currentPlaying == position) {
            nameMusic.setTextColor(Color.LTGRAY);
            playingAnimation.setVisibility(View.VISIBLE);
            playingAnimation.playAnimation();
        }
        if (currentPlaying == position && paused) {
            playingAnimation.pauseAnimation();
        }

        return v;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    public void setCurrentPlaying(int index) {
        this.currentPlaying = index;
    }
    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
