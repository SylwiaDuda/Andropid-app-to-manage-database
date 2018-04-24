package com.example.sylwi.baza_telefonow;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapterKursora;
    private ListView listaView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listaView = (ListView)findViewById(R.id.listView);
        uruchomLoadera();
         listaView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                 onListItemClick(id);
             }
         });

        listaView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listaView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater pompka = actionMode.getMenuInflater();
                pompka.inflate(R.menu.pasek_usowania,menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.akcjaUsun:
                        kasujZaznaczone();
                        return true;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });
    }
////////////////////////loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projekcja = {PomocnikBD.ID,PomocnikBD.KOLUMNA1,PomocnikBD.KOLUMNA2};
        CursorLoader loaderKursora = new CursorLoader(this,TelefonyContentProvider.URI_ZAWARTOSCI,projekcja,null,null,null);
        return loaderKursora;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor dane) {
        adapterKursora.swapCursor(dane);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        adapterKursora.swapCursor(null);
    }
    private void uruchomLoadera(){
        getLoaderManager().initLoader(0,null,this);
        String[] mapujZ = new String[]{PomocnikBD.KOLUMNA1,PomocnikBD.KOLUMNA2};
        int[] mapujDo = new  int[]{R.id.producentListaTV,R.id.modelListaTV};
        adapterKursora = new SimpleCursorAdapter(this,R.layout.activity_main_wiersze,null, mapujZ, mapujDo, 0);
        listaView.setAdapter(adapterKursora);
    }
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pasek_akcji, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()) {
            case R.id.akcjaDodaj:
                Intent zamiar = new Intent(this, MainActivity2.class);
                zamiar.putExtra(PomocnikBD.ID, (long) -1);
                startActivityForResult(zamiar, 0);
                break;

        }
        return  super.onOptionsItemSelected(menuItem);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getLoaderManager().restartLoader(0,null,this);
    }
    private void kasujZaznaczone(){
        long zaznaczone[] = listaView.getCheckedItemIds();
        for (int i = 0; i < zaznaczone.length; ++i){
            getContentResolver().delete(ContentUris.withAppendedId(
                    TelefonyContentProvider.URI_ZAWARTOSCI,zaznaczone[i]), null, null);
        }
    }
    protected void onListItemClick(long id) {
        Intent zamiar = new Intent(this, MainActivity2.class);
        zamiar.putExtra(PomocnikBD.ID, id);
        startActivityForResult(zamiar, 0);
    }

}
