package com.luizmagno.portraitscrollingapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import com.codekidlabs.storagechooser.StorageChooser;
import com.luizmagno.portraitscrollingapp.utils.AlbumAdapter;
import com.luizmagno.portraitscrollingapp.utils.Album;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    RecyclerView reclycerViewAlbuns;
    ArrayList<Album> listAlbuns = new ArrayList<>();
    RecyclerView.LayoutManager layoutManager;
    AlbumAdapter albumAdapter;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_main);

        //View's
        reclycerViewAlbuns = findViewById(R.id.recyclerViewAlbunsId);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        reclycerViewAlbuns.setLayoutManager(layoutManager);
        reclycerViewAlbuns.setHasFixedSize(true);

        //Pegar caminho do diretório de Álbuns
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        String pathMusics = bundle.getString("directoryMusic","noExists");

        //Pegar albuns e add na lista
        if (!pathMusics.equals("noExists")) {

            File pastaMusicas = new File(pathMusics);
            File[] listaAlbuns = pastaMusicas.listFiles();

            if (listaAlbuns != null && listaAlbuns.length != 0) {
                for (File listaAlbun : listaAlbuns) {
                    Album album = getAlbum(listaAlbun);
                    listAlbuns.add(album);
                }
            } else {
                showAlert();
            }

        } else {
            chooseDirectory();
        }

        albumAdapter = new AlbumAdapter(listAlbuns, this);

        linearLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        reclycerViewAlbuns.setLayoutManager(linearLayoutManager);

        reclycerViewAlbuns.setAdapter(albumAdapter);


    }

    private Album getAlbum(File dirAlbum) {

        Album album = new Album();

        album.setNameAlbum(dirAlbum.getName());
        album.setPathCapaAlbum(getPathCapa(dirAlbum.getAbsolutePath()));
        album.setPathAlbum(dirAlbum.getAbsolutePath());
        album.setNumOfMusics(getNumOfMusics(dirAlbum.getAbsolutePath()));

        return  album;
    }

    private String getPathCapa(String pathAlbum) {

        String pathCapa = "";
        File pastaAlbum = new File(pathAlbum);
        File[] listaMusicas = pastaAlbum.listFiles();

        int temCapa = -1;
        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é imagem
            for (int i = 0; i < listaMusicas.length; i++) {
                //Se no nome conter extensão .png, .jpg ou .jpeg, set a capa.
                if (listaMusicas[i].getName().endsWith(".png") ||
                        listaMusicas[i].getName().endsWith(".jpeg") ||
                        listaMusicas[i].getName().endsWith(".jpg")) {
                    temCapa = i;
                }
            }

        }

        if (temCapa != -1){
            pathCapa = listaMusicas[temCapa].getAbsolutePath();
        }

        return pathCapa;
    }

    private int getNumOfMusics(String pathAlbum) {

        int cont = 0;

        File pastaAlbum = new File(pathAlbum);
        File[] listaMusicas = pastaAlbum.listFiles();

        if (listaMusicas != null && listaMusicas.length != 0) {
            //Percorre a lista e verifica se algum é imagem
            for (File listaMusica : listaMusicas) {
                //Se no nome conter extensão .mp3
                if (listaMusica.getName().endsWith(".mp3")) {
                    cont++;
                }
            }

        }

        return cont;
    }


    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Escolha a pasta dos Álbuns")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        chooseDirectory();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Create the AlertDialog object and return it
        builder.create().show();
    }

    private void chooseDirectory() {

        final StorageChooser chooser = new StorageChooser.Builder()
                // Specify context of the dialog
                .withActivity(this)
                .withFragmentManager(getFragmentManager())
                .withMemoryBar(true)
                .allowCustomPath(true)
                // Define the mode as the FOLDER/DIRECTORY CHOOSER
                .setType(StorageChooser.DIRECTORY_CHOOSER)
                .build();

        // 2. Handle what should happend when the user selects the directory !
        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
            @Override
            public void onSelect(String path) {
                // e.g /storage/emulated/0/Documents
                preferences.edit().putString("directoryMusic", path).apply();
                restartApp();
            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();

    }

    private void restartApp() {
        Intent i = new Intent(this, InitActivity.class);
        startActivity(i);
        finish();
    }

}