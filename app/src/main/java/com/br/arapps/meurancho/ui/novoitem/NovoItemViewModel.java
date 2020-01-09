package com.br.arapps.meurancho.ui.novoitem;

import androidx.lifecycle.ViewModel;

public class NovoItemViewModel extends ViewModel {

    private NovoItemListener listener;

    public interface NovoItemListener{
        void loadObjectsView();
        void loadObjectsEvents();
    }

    public void init(NovoItemListener listener){
        this.listener = listener;

        this.listener.loadObjectsView();
        this.listener.loadObjectsEvents();
    }
}
