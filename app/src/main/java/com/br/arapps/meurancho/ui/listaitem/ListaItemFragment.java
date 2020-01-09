package com.br.arapps.meurancho.ui.listaitem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;

import com.br.arapps.meurancho.R;
import com.br.arapps.meurancho.global.AppGlobal;
import com.br.arapps.meurancho.ui.lista.ListaFragment;
import com.br.arapps.meurancho.ui.novoitem.NovoItemFragment;

import java.util.List;

public class ListaItemFragment extends Fragment implements ListaItemViewModel.ListaItemListener,
        ListaItemAdapter.ListaItemAdapterListener {

    private ListaItemViewModel mViewModel;
    private Toolbar toolbar;
    private AppCompatActivity activity;
    private RecyclerView rvLista;
    private NovoItemFragment novoItemFragment;
    private ProgressBar progressBar;

    private String nomeLista;

    public static ListaItemFragment newInstance() {
        return new ListaItemFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.activity = (AppCompatActivity) getActivity();

        return inflater.inflate(R.layout.lista_item_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ListaItemViewModel.class);
        mViewModel.init(this, nomeLista);

        mViewModel.getData().observe(this, new Observer<List<ContentValues>>() {
            @Override
            public void onChanged(List<ContentValues> contentValues) {
                rvLista.setAdapter(new ListaItemAdapter(ListaItemFragment.this, getActivity(), contentValues));

                if (contentValues.size() > 0)
                    getView().findViewById(R.id.tvMsgOps).setVisibility(View.GONE);
                else
                    getView().findViewById(R.id.tvMsgOps).setVisibility(View.VISIBLE);
            }
        });

        getActivity().setTitle(this.nomeLista);
    }

    @Override
    public void loadObjectsView() {
        this.toolbar = getView().findViewById(R.id.toolbar);
        this.toolbar.inflateMenu(R.menu.menu_lista_item);
        this.toolbar.setTitle(nomeLista);

        this.rvLista = getView().findViewById(R.id.rvLista);
        this.rvLista.setLayoutManager(new LinearLayoutManager(getActivity()));

        this.progressBar = getView().findViewById(R.id.progressBar);
    }

    @Override
    public void loadObjectEvents() {
        SearchView searchView = (SearchView) toolbar.getMenu().findItem(R.id.menu_buscar).getActionView();
        searchView.setQueryHint(String.format("%s...", getString(R.string.text_buscar)));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                View view = ListaItemFragment.this.getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) ListaItemFragment.this.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mViewModel.refresh(String.format("%s%s%s", "%", newText, "%"));
                return true;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mViewModel.refresh("");
                return false;
            }
        });

        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListaFragment) getFragmentManager().getFragments().get(0)).getmViewModel().refresh();
                getFragmentManager().popBackStackImmediate();
            }
        });

        this.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_novo:
                        novoItemFragment = NovoItemFragment.newInstance();
                        novoItemFragment.setNomeLista(nomeLista);
                        novoItemFragment.setListaItemFragment(ListaItemFragment.this);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_to_top, R.anim.slide_top_to_bottom);
                        fragmentTransaction.replace(R.id.container, novoItemFragment, "novoItemFragment").addToBackStack("").commit();

                        break;

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
                                                if (c.getAsBoolean("selecionado"))
                                                    AppGlobal.getDatabase().listaItemDao().delete(c.getAsString("nomeLista"), c.getAsString("nomeItem"));
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
                                            mViewModel.refresh("");
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
                    default:
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void onClickItem(String nomeItem) {
        novoItemFragment = NovoItemFragment.newInstance();
        novoItemFragment.setNomeLista(nomeLista);
        novoItemFragment.setNomeItemArg(nomeItem);
        novoItemFragment.setListaItemFragment(ListaItemFragment.this);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_bottom_to_top, R.anim.slide_top_to_bottom);
        fragmentTransaction.replace(R.id.container, novoItemFragment, "novoItemFragment").addToBackStack("").commit();
    }

    @Override
    public void showProgress(boolean show) {
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
    }

    public ListaItemViewModel getmViewModel() {
        return mViewModel;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }
}
