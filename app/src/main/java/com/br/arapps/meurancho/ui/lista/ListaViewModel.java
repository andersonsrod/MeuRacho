package com.br.arapps.meurancho.ui.lista;

import android.content.ContentValues;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.br.arapps.meurancho.classes.Lista;
import com.br.arapps.meurancho.global.AppGlobal;

import java.util.ArrayList;
import java.util.List;

public class ListaViewModel extends ViewModel {

    private ListaListener listener;

    private MutableLiveData<List<ContentValues>> data;

    public interface ListaListener{
        void loadObjectsView();
        void loadObjectEvents();
        void showProgress(boolean show);
    }

    public void init(ListaListener listener){
        this.listener =  listener;

        if (data == null)
            data = new MutableLiveData<>();

        this.listener.loadObjectsView();
        this.listener.loadObjectEvents();

        refresh();
    }

    public MutableLiveData<List<ContentValues>> getData() {
        return data;
    }

    public void refresh(){
        new AsyncTask<Void, Void, List<Lista>>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listener.showProgress(true);
            }

            @Override
            protected List<Lista> doInBackground(Void... voids) {
                return AppGlobal.getDatabase().listaDao().allListas();
            }

            @Override
            protected void onPostExecute(List<Lista> listas) {
                super.onPostExecute(listas);

                List<ContentValues> lista = new ArrayList<>();
                for (Lista l: listas){
                    ContentValues c = new ContentValues();
                    c.put("nomeLista", l.nomeLista);
                    c.put("selecionado", false);
                    lista.add(c);
                }

                data.setValue(lista);

                listener.showProgress(false);
            }
        }.execute();
    }

    public boolean possuiItemSelecionado(){
        List<ContentValues> l = data.getValue();
        for (ContentValues c: l){
            if (c.getAsBoolean("selecionado"))
                return true;
        }

        return false;
    }
}
