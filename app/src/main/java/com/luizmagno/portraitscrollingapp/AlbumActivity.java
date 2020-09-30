package com.luizmagno.portraitscrollingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luizmagno.portraitscrollingapp.utils.Music;
import com.luizmagno.portraitscrollingapp.utils.MusicAdapter;
import com.luizmagno.portraitscrollingapp.utils.PlayListAdapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AlbumActivity extends AppCompatActivity {

    private PlayListAdapter playListAdapter;
    private ArrayList<Music> listMusics = new ArrayList<>();
    private ArrayList<Music> playListMusics = new ArrayList<>();
    private boolean stop = true;
    private boolean pause = false;
    private int currentSongIndex = 0;
    private TextView nameMusicIsPlaying;

    private FloatingActionButton fabPlayList;

    private MediaPlayer mp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        //Player
        mp = new MediaPlayer();

        //Pegando path da Capa e nome do álbum
        Bundle bundle = getIntent().getExtras();
        TextView album = findViewById(R.id.titleAlbumId);
        assert bundle != null;
        album.setText(bundle.getString("titleAlbum", "Não foi dessa vez!"));
        String pathCapaAlbum = bundle.getString("capaAlbum", "");

        //View's
        ImageView capaAlbum = findViewById(R.id.imageAlbumId);
        ListView listViewMusics = findViewById(R.id.listMusicasId);
        ListView playListView = findViewById(R.id.listEscolhidasId);
        FloatingActionButton fabAddAll = findViewById(R.id.fabAddAllId);
        fabPlayList = findViewById(R.id.fabPlayListId);
        FloatingActionButton fabNext = findViewById(R.id.fabNextId);
        FloatingActionButton fabPrevious = findViewById(R.id.fabPreviousId);
        nameMusicIsPlaying = findViewById(R.id.nameMusicIsPlayingId);

        //Set Capa
        if (pathCapaAlbum.equals("")) {
            capaAlbum.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                    R.drawable.ic_warning, null));
        } else {
            capaAlbum.setImageURI(Uri.parse(pathCapaAlbum));
        }

        //Set back
        FloatingActionButton fabBack = findViewById(R.id.fabBackId);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumActivity.super.onBackPressed();
            }
        });

        //Pegando lista de Músicas do Álbum
        String pathAlbum = bundle.getString("pathAlbum", "noExistis");
        if (!pathAlbum.equals("noExistis")) {
            getMusicsOfAlbum(pathAlbum);
        }

        //Adapter das Escolhidas
        playListAdapter = new PlayListAdapter(playListMusics, this);
        playListView.setAdapter(playListAdapter);
        playListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                currentSongIndex = i;
                playSong(currentSongIndex);
            }
        });

        //Menu de contexto das escolhidas
        playListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(Menu.NONE, 1, Menu.NONE, "Excluir da Lista");
            }
        });

        //Adapter de todas as músicas do álbum
        MusicAdapter musicAdapter = new MusicAdapter(listMusics, this);
        listViewMusics.setAdapter(musicAdapter);
        listViewMusics.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                playListMusics.add(listMusics.get(i));
                playListAdapter.notifyDataSetChanged();
            }
        });

        //Click Fab add todas as músicas na playlist
        fabAddAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playListMusics.addAll(listMusics);
                playListAdapter.notifyDataSetChanged();
            }
        });

        //Click Fab Play playlist
        fabPlayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               playList();
            }
        });

        //Click Fab Next
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextMusic();
            }
        });

        //Click Fab Previous
        fabPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousMusic();
            }
        });

        //OnCompletionListener
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if (currentSongIndex == playListMusics.size() - 1) {
                    //Música que está tocando é a última da lista, então parar de tocar
                    mp.stop();
                    stop = true;
                    pause = false;
                    currentSongIndex = 0;
                    notifyPlayListAdapter();
                } else {
                    //Não é a última então toque a próxima música
                    mp.stop();
                    stop = true;
                    pause = false;
                    currentSongIndex++;
                    notifyPlayListAdapter();
                    playList();
                }

            }
        });

    }

    private void playList(){
        if (playListMusics.isEmpty()) {
            if (mp.isPlaying()) {
                mp.stop();
                stop = true;
                pause = false;
                currentSongIndex = 0;
                fabPlayList.setImageResource(R.drawable.ic_play);
                notifyPlayListAdapter();
            } else {
                Toast.makeText(this,
                        "Lista Vazia. Escolha ao menos uma música", Toast.LENGTH_SHORT).show();
            }
        } else if(stop) {
            playSong(currentSongIndex);
        } else {
            playPauseSong();
        }
    }

    private void playPauseSong() {

        // check for already playing
        if(mp.isPlaying()){
            if(mp!=null){
                mp.pause();
                // Changing button image to play button
                fabPlayList.setImageResource(R.drawable.ic_play);

                pause = true;
                notifyPlayListAdapter();
            }
        }else{
            // Resume song
            if(mp!=null){
                mp.start();
                // Changing button image to pause button
                fabPlayList.setImageResource(R.drawable.ic_pause);

                pause = false;
                notifyPlayListAdapter();
            }
        }
    }

    private void playSong(int songIndex) {
        // Play song
        try {
            mp.reset();
            mp.setDataSource(playListMusics.get(songIndex).getAbsolutePathMusic());
            mp.prepare();
            mp.start();

            //Set Stop para falso
            stop = false;

            //Set Pause para falso
            pause = false;

            //Notify Adapter para mudança de cor do texto
            notifyPlayListAdapter();

            //Increment Index
            //currentSongIndex++;

            // Displaying Song title
            //String songTitle = songsList.get(songIndex).get("songTitle");
            //songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            fabPlayList.setImageResource(R.drawable.ic_pause);

            //Changing Text music playing
            nameMusicIsPlaying.setText(playListMusics.get(songIndex).getNameMusic());

            // set Progress bar values
            //songProgressBar.setProgress(0);
            //songProgressBar.setMax(100);

            // Updating progress bar
            //updateProgressBar();

        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void nextMusic() {
        if (playListMusics.isEmpty()) {
            Toast.makeText(this,
                    "Lista Vazia. Escolha ao menos uma música", Toast.LENGTH_SHORT).show();
            if (mp.isPlaying()) {
                mp.stop();
                stop = true;
                pause = false;
                fabPlayList.setImageResource(R.drawable.ic_play);
            }
        } else {
            if (currentSongIndex == playListMusics.size() - 1) {
                currentSongIndex = 0;
            } else {
                currentSongIndex++;
            }
            stop = true;
            pause = false;
            notifyPlayListAdapter();
            playList();
        }
    }

    private void previousMusic () {
        if (playListMusics.isEmpty()) {
            Toast.makeText(this,
                    "Lista Vazia. Escolha ao menos uma música.", Toast.LENGTH_SHORT).show();
            if (mp.isPlaying()) {
                mp.stop();
                stop = true;
                pause = false;
                fabPlayList.setImageResource(R.drawable.ic_play);
            }
        } else {
            if (currentSongIndex == 0) {
                currentSongIndex = playListMusics.size() - 1;
            } else {
                currentSongIndex--;
            }
            stop = true;
            pause = false;
            notifyPlayListAdapter();
            playList();

        }
    }

    private void notifyPlayListAdapter() {
        playListAdapter.setCurrentPlaying(currentSongIndex);
        playListAdapter.setStopped(stop);
        playListAdapter.setPaused(pause);
        playListAdapter.notifyDataSetChanged();
    }

    private void getMusicsOfAlbum(String path) {

        File pastaAlbum = new File(path);
        File[] listaMusicas = pastaAlbum.listFiles();

        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é música
            for (File music : listaMusicas) {
                String name = music.getName();
                if (name.endsWith(".mp3")) {
                    Music m = new Music();
                    m.setNameMusic(music.getName());
                    m.setAbsolutePathMusic(music.getAbsolutePath());
                    listMusics.add(m);
                }
            }

        }

    }

    private void excludeOfList(int index) {
        if (mp.isPlaying() && currentSongIndex == index) {
            //Se a música que está tocando é igual a que vai ser removida
            mp.stop();
            stop = true;
            pause = false;
            fabPlayList.setImageResource(R.drawable.ic_play);
            playListMusics.remove(index);
            notifyPlayListAdapter();
            playList();
        } else if (pause && currentSongIndex == index) {
            //Se a música pausada é igual a que vai ser removida
            mp.stop();
            stop = true;
            pause = false;
            fabPlayList.setImageResource(R.drawable.ic_play);
            playListMusics.remove(index);
            if (index == playListMusics.size() - 1) {
                currentSongIndex = 0;
            } else {
                currentSongIndex++;
            }
            notifyPlayListAdapter();
        } else if (index < currentSongIndex){
            currentSongIndex = currentSongIndex - 1;
            playListMusics.remove(index);
            notifyPlayListAdapter();
        } else {
            playListMusics.remove(index);
            notifyPlayListAdapter();
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo =
               (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

       int position = menuInfo.position;

        if (item.getItemId() == 1) {
            excludeOfList(position);
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
    }

}