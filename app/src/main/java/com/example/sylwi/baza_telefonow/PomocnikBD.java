package com.example.sylwi.baza_telefonow;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sylwi on 10.04.2018.
 */

public class PomocnikBD extends SQLiteOpenHelper{

    private Context context;
    public final static int WERSJA_BAZY = 1;
    public final static String ID = "_id";
    public final static String NAZWA_BAZY = "baza_telefonow";
    public final static String NAZWA_TABELI = "telefony";
    public final static String KOLUMNA1 = "producent";
    public final static String KOLUMNA2 = "model";
    public final static String WERSJA_ANDROIDA = "wersja_ndroida";
    public final static String WWW = "www";
    public final static String TW_BAZY = "CREATE TABLE " + NAZWA_TABELI +
            "("+ID+" integer primary key autoincrement, " + KOLUMNA1+" text not null,"+ KOLUMNA2+" text not null,"+ WERSJA_ANDROIDA+" text not null," + WWW+" text);";
    private static final String KAS_BAZY = "DROP TABLE IF EXISTS "+NAZWA_TABELI;

    public PomocnikBD(Context context) {
        super(context, NAZWA_BAZY, null, WERSJA_BAZY); //super(context, name, factory, version);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(TW_BAZY); //tworzenie bazy
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //aktualizacja bazy do nowej wersji
        sqLiteDatabase.execSQL(KAS_BAZY);
        onCreate(sqLiteDatabase);
    }
}
