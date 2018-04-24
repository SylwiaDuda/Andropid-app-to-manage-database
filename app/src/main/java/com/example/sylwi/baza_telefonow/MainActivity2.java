package com.example.sylwi.baza_telefonow;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity{
    private long idWiersza;
    private EditText producentET ;
    private EditText modelET;
    private EditText wersjaAndroidaET;
    private EditText wwwET;
    private Button wwwButton;
    private Button anulujButton;
    private Button zapiszButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        producentET = (EditText)findViewById(R.id.producentET);
        modelET = (EditText)findViewById(R.id.modelET);
        wersjaAndroidaET = (EditText)findViewById(R.id.wersjaAndroidaET);
        wwwET = (EditText)findViewById(R.id.wwwET);
        wwwButton = (Button)findViewById(R.id.buttonWWW);
        anulujButton = (Button)findViewById(R.id.buttonAnuluj);
        zapiszButton = (Button)findViewById(R.id.buttonZapisz);
        idWiersza=-1;
        if (savedInstanceState != null) {
            idWiersza=savedInstanceState.getLong(PomocnikBD.ID);
        }else{
            Bundle tobolek = getIntent().getExtras();
            if(tobolek!=null){
                idWiersza = tobolek.getLong(PomocnikBD.ID);
            }
        }
        if(idWiersza!=-1){
            wypelnijPola();
        }

        anulujButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        zapiszButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sprawdzNapisy()) {
                    ContentValues wartosci = new ContentValues();
                    wartosci.put(PomocnikBD.KOLUMNA1, producentET.getText().toString());
                    wartosci.put(PomocnikBD.KOLUMNA2, modelET.getText().toString());
                    wartosci.put(PomocnikBD.WERSJA_ANDROIDA, wersjaAndroidaET.getText().toString());
                    wartosci.put(PomocnikBD.WWW, wwwET.getText().toString());
                    if (idWiersza == -1){
                        Uri uriNowego = getContentResolver().insert(TelefonyContentProvider.URI_ZAWARTOSCI, wartosci);
                        idWiersza = Integer.parseInt(uriNowego.getLastPathSegment());
                    } else {
                        int liczbaZaktualizowanch = getContentResolver().update(ContentUris.withAppendedId(
                                TelefonyContentProvider.URI_ZAWARTOSCI, idWiersza), wartosci, null, null);
                    }
                    setResult(RESULT_OK);
                    finish();
                }else {
                    wyswietlKomunikat();
                }
            }
        });
        wwwButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!wwwET.getText().toString().equals("")){
                    String adres = wwwET.getText().toString();
                    if(!adres.startsWith("http://")&&!adres.startsWith("https://")){
                        adres = "http://" + adres;
                        Intent zamiarPrzegladarki = new Intent("android.intent.action.VIEW",Uri.parse(adres));
                        startActivity(zamiarPrzegladarki);
                    }else{
                        wyswietlKomunikat();
                    }
                }
            }
        });
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putLong(PomocnikBD.ID,idWiersza);
    }
    private boolean sprawdzNapisy(){
        return !(producentET.getText().toString().equals("")|| modelET.getText().toString().equals("")||
                wersjaAndroidaET.getText().toString().equals("")||wwwET.getText().toString().equals(""));
    }
    private void wypelnijPola(){
        String projekcja[]= {PomocnikBD.KOLUMNA1, PomocnikBD.KOLUMNA2, PomocnikBD.WERSJA_ANDROIDA, PomocnikBD.WWW};
        Cursor kursor = getContentResolver().query(ContentUris.withAppendedId(TelefonyContentProvider.URI_ZAWARTOSCI,idWiersza),projekcja,null,null,null);
        kursor.moveToFirst();
        int indeksKolumny = kursor.getColumnIndexOrThrow(PomocnikBD.KOLUMNA1);
        String wartosc = kursor.getString(indeksKolumny);
        producentET.setText(wartosc);
        modelET.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.KOLUMNA2)));
        wersjaAndroidaET.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.WERSJA_ANDROIDA)));
        wwwET.setText(kursor.getString(kursor.getColumnIndexOrThrow(PomocnikBD.WWW)));
        kursor.close();
    }
    private void wyswietlKomunikat(){
        Toast.makeText(this, getString(R.string.empty), Toast.LENGTH_SHORT).show();
    }

}
