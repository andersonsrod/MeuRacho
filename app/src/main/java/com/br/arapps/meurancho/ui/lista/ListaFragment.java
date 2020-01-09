package com.br.arapps.meurancho.ui.lista;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.br.arapps.meurancho.R;
import com.br.arapps.meurancho.classes.ListaItem;
import com.br.arapps.meurancho.global.AppGlobal;
import com.br.arapps.meurancho.ui.listaitem.ListaItemFragment;
import com.br.arapps.meurancho.ui.novalista.NovaListaFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class ListaFragment extends Fragment implements ListaViewModel.ListaListener, ListaAdapter.ListaAdapterListener {
    private ListaViewModel mViewModel;
    private NovaListaFragment novaListaFragment;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private FloatingActionButton btnCompartilhar;

    private RecyclerView rvLista;

    public static ListaFragment newInstance() {
        return new ListaFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        return inflater.inflate(R.layout.lista_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(ListaViewModel.class);
        mViewModel.init(this);

        mViewModel.getData().observe(this, new Observer<List<ContentValues>>() {
            @Override
            public void onChanged(List<ContentValues> listas) {
                rvLista.setAdapter(new ListaAdapter(ListaFragment.this, getActivity(), listas));

                if (listas.size() > 0)
                    getView().findViewById(R.id.tvMsgOps).setVisibility(View.GONE);
                else
                    getView().findViewById(R.id.tvMsgOps).setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void loadObjectsView() {
        this.toolbar = getView().findViewById(R.id.toolbar);
        this.toolbar.setTitle("Meu Rancho");

        this.rvLista = getView().findViewById(R.id.rvLista);
        this.rvLista.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.progressBar = getView().findViewById(R.id.progressBar);

        this.btnCompartilhar = getView().findViewById(R.id.btnCompartilhar);
    }

    @Override
    public void loadObjectEvents() {
        toolbar.inflateMenu(R.menu.menu_lista);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.menu_excluir:
                        if (!mViewModel.possuiItemSelecionado()){
                            AppGlobal.messageInfo(getActivity(), getString(R.string.text_msg_nenhum_selecionado));
                            break;
                        }

                        DialogInterface.OnClickListener evtSim = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new AsyncTask<Void, String, Boolean>(){
                                    @Override
                                    protected Boolean doInBackground(Void... voids) {
                                        try {
                                            List<ContentValues> data = mViewModel.getData().getValue();
                                            for (ContentValues c : data) {
                                                if (c.getAsBoolean("selecionado")) {
                                                    AppGlobal.getDatabase().listaItemDao().deleteAll(c.getAsString("nomeLista"));
                                                    AppGlobal.getDatabase().listaDao().delete(c.getAsString("nomeLista"));
                                                }
                                            }
                                        }catch (SQLiteException e){
                                            publishProgress(e.getMessage());
                                            return false;
                                        }

                                        return true;
                                    }

                                    @Override
                                    protected void onProgressUpdate(String... values) {
                                        super.onProgressUpdate(values);
                                        AppGlobal.messageInfo(getActivity(), values[0]);
                                    }

                                    @Override
                                    protected void onPostExecute(Boolean aBoolean) {
                                        super.onPostExecute(aBoolean);

                                        if (aBoolean)
                                            mViewModel.refresh();
                                    }
                                }.execute();
                            }
                        };

                        DialogInterface.OnClickListener evtNao = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        };

                        AppGlobal.messageDialog(getActivity(), getString(R.string.text_msg_excluir_lista), evtSim, evtNao);

                        break;
                    case R.id.menu_novo:
                        novaListaFragment = new NovaListaFragment();
                        novaListaFragment.setListaFragment(ListaFragment.this);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_to_top, R.anim.slide_top_to_bottom);
                        fragmentTransaction.add(R.id.container, novaListaFragment, "novaListaFragment").addToBackStack("").commit();

                        break;

                    case R.id.menu_editar:
                        String nomeLista = null;
                        for (ContentValues c: mViewModel.getData().getValue()){
                            if (c.getAsBoolean("selecionado"))
                                nomeLista = c.getAsString("nomeLista");
                        }

                        if (nomeLista != null) {
                            novaListaFragment = new NovaListaFragment();
                            novaListaFragment.setListaFragment(ListaFragment.this);
                            novaListaFragment.setNomeListaArg(nomeLista);
                            fragmentTransaction = getFragmentManager().beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_to_top, R.anim.slide_top_to_bottom);
                            fragmentTransaction.add(R.id.container, novaListaFragment, "novaListaFragment").addToBackStack("").commit();
                        }else
                            AppGlobal.messageInfo(getActivity(), getString(R.string.text_msg_nenhum_selecionado));
                    default:
                        break;
                }

                return false;
            }
        });

        this.btnCompartilhar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (final ContentValues c: mViewModel.getData().getValue()){
                    if (c.getAsBoolean("selecionado")){
                        new AsyncTask<Void, Void, List<ListaItem>>(){
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                showProgress(true);
                            }

                            @Override
                            protected List<ListaItem> doInBackground(Void... voids) {
                                List<ListaItem> lista;
                                try{
                                    return AppGlobal.getDatabase().listaItemDao().allItens(c.getAsString("nomeLista"));
                                }catch (SQLiteException e){
                                    return null;
                                }
                            }

                            @Override
                            protected void onPostExecute(List<ListaItem> listaItems) {
                                super.onPostExecute(listaItems);

                                if (listaItems != null){
                                    String itens = "";
                                    for (ListaItem item: listaItems){
                                        itens += String.format("%s - %.1f %s\n", item.nomeItem, item.qtd, item.unidade);
                                    }

                                    String shareBody = itens;
                                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                                    sharingIntent.setType("text/plain");
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, String.format("Lista %s", c.getAsString("nomeLista")));
                                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                                    startActivity(Intent.createChooser(sharingIntent, ""));
                                }

                                showProgress(false);
                            }
                        }.execute();

                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onClickItem(String nomeLista) {
        ListaItemFragment fragment = ListaItemFragment.newInstance();
        fragment.setNomeLista(nomeLista);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_to_top, R.anim.slide_top_to_bottom);
        fragmentTransaction.add(R.id.container, fragment, "listaItemFragment").addToBackStack("").commit();
    }

    @Override
    public void showProgress(boolean show) {
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    public ListaViewModel getmViewModel() {
        return mViewModel;
    }
}
