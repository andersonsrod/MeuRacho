package com.br.arapps.meurancho.ui.listaitem;

import android.content.ContentValues;
import android.os.AsyncTask;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.br.arapps.meurancho.classes.ListaItem;
import com.br.arapps.meurancho.global.AppGlobal;

import java.util.ArrayList;
import java.util.List;

public class ListaItemViewModel extends ViewModel {
    private ListaItemListener listener;
    private MutableLiveData<List<ContentValues>> data;

    private String nomeLista;

    public interface ListaItemListener{
        void loadObjectsView();
        void loadObjectEvents();
        void showProgress(boolean show);
    }

    public void init(ListaItemListener listener, String nomeLista){
        this.listener =  listener;
        this.nomeLista = nomeLista;

        if (data == null)
            data = new MutableLiveData<>();

        this.listener.loadObjectsView();
        this.listener.loadObjectEvents();

        refresh("");
    }

    public MutableLiveData<List<ContentValues>> getData() {
        return data;
    }

    public void refresh(final String filtro){
        new AsyncTask<Void, Void, List<ListaItem>>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listener.showProgress(true);
            }

            @Override
            protected List<ListaItem> doInBackground(Void... voids) {
                if (filtro.trim().equals(""))
                    return AppGlobal.getDatabase().listaItemDao().allItens(nomeLista);
                else
                    return AppGlobal.getDatabase().listaItemDao().itensFiltro(nomeLista, filtro);
            }

            @Override
            protected void onPostExecute(List<ListaItem> listas) {
                super.onPostExecute(listas);

                List<ContentValues> lista = new ArrayList<>();
                for (ListaItem l: listas){
                    ContentValues c = new ContentValues();
                    c.put("nomeLista", l.nomeLista);
                    c.put("nomeItem", l.nomeItem);
                    c.put("qtd", l.qtd);
                    c.put("unidade", l.unidade);
                    c.put("comprado", l.comprado);
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
