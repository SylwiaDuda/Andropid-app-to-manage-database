package com.example.sylwi.baza_telefonow;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by sylwi on 20.04.2018.
 */

public class TelefonyContentProvider extends ContentProvider {
    private PomocnikBD pomocnikBD;
    private static final String IDENTYFIKATOR = "com.example.sylwi.baza_telefonow.TelefonyContentProvider";
    public static final Uri URI_ZAWARTOSCI = Uri.parse("content://"+IDENTYFIKATOR+"/"+PomocnikBD.NAZWA_TABELI);
    private static final int CALA_TABELA =1;
    private static final int WYBRANY_WIERSZ =2;
    private static final UriMatcher sDopasowanieUri = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sDopasowanieUri.addURI(IDENTYFIKATOR, PomocnikBD.NAZWA_TABELI,CALA_TABELA);
        sDopasowanieUri.addURI(IDENTYFIKATOR,PomocnikBD.NAZWA_TABELI+"/#",WYBRANY_WIERSZ);
    }
    @Override
    public boolean onCreate() {
        pomocnikBD = new PomocnikBD(getContext());
        return false;
    }
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = pomocnikBD.getWritableDatabase();
        Cursor kursor = null;
        switch (typUri){
            //strings - określa interesujące kolumy z tabeli
            //s, strings1 - wybranie określonych wierszy, które zostaną zwrócone  przez  dostawcę
            //s1 - kreśla  w  jakiej  kolejności,  dostawca  powinien  zwrócić  dane
            case CALA_TABELA:
                kursor =baza.query(false,PomocnikBD.NAZWA_TABELI,strings,s,strings1,null,null,s1,null,null);
                break;
            case WYBRANY_WIERSZ:
                kursor=baza.query(false,PomocnikBD.NAZWA_TABELI,strings,dodajIdDoSelekcji(s,uri),strings1,null,null,s1,null,null);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: "+ uri);
                //Logger.getAnonymousLogger().log(Level.WARNING,"Nieznane URI: "+ uri);
        }
        kursor.setNotificationUri(getContext().getContentResolver(),uri);
        return kursor;
    }
    private String dodajIdDoSelekcji(String selekcja, Uri uri){
        if(selekcja!=null && !selekcja.equals("")){
            selekcja = selekcja + " and " + PomocnikBD.ID + "=" +uri.getLastPathSegment();
        }else {
            selekcja = PomocnikBD.ID + "=" + uri.getLastPathSegment();
        }
        return selekcja;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = pomocnikBD.getWritableDatabase();
        long idDodanego =0;
        switch (typUri){
            case CALA_TABELA:
                idDodanego = baza.insert(PomocnikBD.NAZWA_TABELI,null,contentValues); /////////MOZE JESZCZE JEDEN CASE
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return Uri.parse(PomocnikBD.NAZWA_TABELI + "/" + idDodanego);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = pomocnikBD.getWritableDatabase();
        int liczbaUsunietych = 0;
        switch (typUri){
            case CALA_TABELA:
                liczbaUsunietych = baza.delete(PomocnikBD.NAZWA_TABELI,s,strings);
                break;
            case WYBRANY_WIERSZ:
                liczbaUsunietych = baza.delete(PomocnikBD.NAZWA_TABELI,dodajIdDoSelekcji(s,uri),strings);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: "+ uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return liczbaUsunietych;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int typUri = sDopasowanieUri.match(uri);
        SQLiteDatabase baza = pomocnikBD.getWritableDatabase();
        int liczbaZaktualizowanych = 0;
        switch (typUri) {
            case CALA_TABELA:
                liczbaZaktualizowanych=baza.update(PomocnikBD.NAZWA_TABELI, contentValues, s, strings);
                break;
            case WYBRANY_WIERSZ:
                liczbaZaktualizowanych = baza.update(PomocnikBD.NAZWA_TABELI, contentValues, dodajIdDoSelekcji(s, uri), strings);
                break;
            default:
                throw new IllegalArgumentException("Nieznane URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return liczbaZaktualizowanych;
    }
}
