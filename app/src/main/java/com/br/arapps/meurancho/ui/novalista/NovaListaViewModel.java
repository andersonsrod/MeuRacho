package com.br.arapps.meurancho.ui.novalista;

import androidx.lifecycle.ViewModel;

public class NovaListaViewModel extends ViewModel {

    private NovaListaListener listener;

    public interface NovaListaListener{
        void loadObjectsView();
        void loadObjectsEvents();
        void loadImportar();
    }

    public void init(NovaListaListener listener){
        this.listener = listener;

        this.listener.loadObjectsView();
        this.listener.loadObjectsEvents();
        this.listener.loadImportar();
    }

}
