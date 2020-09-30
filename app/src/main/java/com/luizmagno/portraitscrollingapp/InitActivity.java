package com.luizmagno.portraitscrollingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;

public class InitActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_READ_CARD = 0;
    private SharedPreferences sharedPreferences;
    private SwitchCompat switchCompat;
    private TextView textOk, tudoOk;
    private Button buttonChoose;
    private String pathDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        //Preferences
        sharedPreferences = getSharedPreferences("com.luizmagno.music.preferences", Context.MODE_PRIVATE);
        pathDirectory = sharedPreferences.getString("directoryMusic", "noExists");

        switchCompat = findViewById(R.id.switchReadId);
        textOk = findViewById(R.id.okId);
        buttonChoose = findViewById(R.id.buttonChooseId);
        tudoOk = findViewById(R.id.buttonTudoOkId);

        if (checkPermission()) {
            textOk.setVisibility(View.VISIBLE);
            switchCompat.setChecked(true);
            switchCompat.setVisibility(View.INVISIBLE);
        } else {
            textOk.setVisibility(View.INVISIBLE);
            switchCompat.setChecked(false);
            switchCompat.setVisibility(View.VISIBLE);
        }

        if (switchCompat.getVisibility() == View.VISIBLE) {
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                        requestReadCardPermission();
                }
            });
        }

        if ( !pathDirectory.equals("noExists") ) {
            buttonChoose.setText(pathDirectory);
        }

        buttonChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDirectory();
            }
        });

        tudoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pathDirectory.equals("noExists") &&
                        switchCompat.getVisibility() == View.INVISIBLE) {
                    startMain();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Dê permissão e escolha a pasta!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!pathDirectory.equals("noExists") &&
                switchCompat.getVisibility() == View.INVISIBLE) {
            startMain();
        }

    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("directoryMusic", pathDirectory);
        startActivity(intent);
        finish();
    }

    private boolean checkPermission() {

        // Check if the read permission has been granted
        // Permission is already available
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
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
                pathDirectory = path;
                sharedPreferences.edit().putString("directoryMusic", path).apply();
                buttonChoose.setText(path);
            }
        });

        // 3. Display File Picker whenever you need to !
        chooser.show();

    }

    private void requestReadCardPermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_READ_CARD);

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_CARD);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_READ_CARD) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                //switchCompat.setChecked(true);
                switchCompat.setVisibility(View.INVISIBLE);
                textOk.setVisibility(View.VISIBLE);

            } else {
                // Permission request was denied.
                switchCompat.setChecked(false);
                switchCompat.setVisibility(View.VISIBLE);
                textOk.setVisibility(View.INVISIBLE);
            }
        }

    }

}