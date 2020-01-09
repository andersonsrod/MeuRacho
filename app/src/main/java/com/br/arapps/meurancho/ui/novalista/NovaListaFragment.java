package com.br.arapps.meurancho.ui.novalista;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.br.arapps.meurancho.R;
import com.br.arapps.meurancho.classes.Lista;
import com.br.arapps.meurancho.global.AppGlobal;
import com.br.arapps.meurancho.ui.lista.ListaFragment;

import java.util.List;

public class NovaListaFragment extends Fragment implements NovaListaViewModel.NovaListaListener {

    private NovaListaViewModel mViewModel;

    private AppCompatActivity activity;
    private ListaFragment listaFragment;
    private Toolbar toolbar;
    private EditText edtNomeLista;
    private AppCompatSpinner spinnerImportar;

    private String nomeListaArg;

    public static NovaListaFragment newInstance() {
        return new NovaListaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        this.activity = (AppCompatActivity) getActivity();

        return inflater.inflate(R.layout.nova_lista_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NovaListaViewModel.class);
        mViewModel.init(this);
    }

    public void setListaFragment(ListaFragment listaFragment) {
        this.listaFragment = listaFragment;
    }

    public void salvar(){
        final String nomeLista = edtNomeLista.getText().toString();
        if (nomeLista == null || nomeLista.trim().equals("")){
            AppGlobal.messageInfo(getActivity(), getString(R.string.text_msg_informe_nome_lista));
            return;
        }

        DialogInterface.OnClickListener evtSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int position = spinnerImportar.getSelectedItemPosition();

                new AsyncTask<Void, String, Boolean>(){
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        Lista lista = new Lista();
                        lista.nomeLista = nomeLista;

                        try {
                            if (nomeListaArg != null) {
                                AppGlobal.getDatabase().listaItemDao().atualizaChave(nomeLista, nomeListaArg);
                                AppGlobal.getDatabase().listaDao().update(nomeLista, nomeListaArg);
                            }else {
                                AppGlobal.getDatabase().listaDao().insert(lista);

                                if (position > 0) {
                                    List<Lista> listas = AppGlobal.getDatabase().listaDao().allListas();
                                    AppGlobal.getDatabase().listaItemDao().importar(nomeLista, listas.get(position-1).nomeLista);
                                }
                            }
                            return true;
                        }catch (SQLiteConstraintException e){
                            publishProgress(e.getMessage());
                            return false;
                        }
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        super.onProgressUpdate(values);
                        AppGlobal.messageInfo(getActivity(), values[0]);
                    }

                    @Override
                    protected void onPostExecute(Boolean success) {
                        super.onPostExecute(success);

                        if (success) {
                            getFragmentManager().popBackStackImmediate();
                            listaFragment.getmViewModel().refresh();
                        }
                    }
                }.execute();
            }
        };

        DialogInterface.OnClickListener evtNao = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        };

        AppGlobal.messageDialog(getActivity(), getString(R.string.text_msg_salvar_lista), evtSim, evtNao);
    }

    @Override
    public void loadObjectsView() {
        this.toolbar = getView().findViewById(R.id.toolbar);
        this.toolbar.inflateMenu(R.menu.menu_gravar);

        this.edtNomeLista = getView().findViewById(R.id.edtNomeLista);
        this.spinnerImportar = getView().findViewById(R.id.spinnerImportar);

        if (nomeListaArg != null) {
            this.toolbar.setTitle("Editar Lista");
            edtNomeLista.setText(nomeListaArg);
            getView().findViewById(R.id.llImportar).setVisibility(View.GONE);
        }else
            this.toolbar.setTitle("Nova Lista");

        edtNomeLista.requestFocus();
    }

    @Override
    public void loadObjectsEvents() {
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        this.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_salvar:
                        salvar();
                        break;
                    default:
                        break;
                }

                return false;
            }
        });
    }

    @Override
    public void loadImportar() {
        new AsyncTask<Void, Void, List<Lista>>(){
            @Override
            protected List<Lista> doInBackground(Void... voids) {
                List<Lista> listas = AppGlobal.getDatabase().listaDao().allListas();

                Lista l = new Lista();
                l.nomeLista = "Nenhum";

                listas.add(0, l);

                return listas;
            }

            @Override
            protected void onPostExecute(List<Lista> listas) {
                super.onPostExecute(listas);

                if (listas.size() > 0){
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, listas);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerImportar.setAdapter(adapter);
                }else
                    getView().findViewById(R.id.llImportar).setVisibility(View.GONE);
            }
        }.execute();
    }

    public void setNomeListaArg(String nomeListaArg) {
        this.nomeListaArg = nomeListaArg;
    }
}
