package com.br.arapps.meurancho.ui.novoitem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
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
import com.br.arapps.meurancho.classes.ListaItem;
import com.br.arapps.meurancho.classes.Unidade;
import com.br.arapps.meurancho.global.AppGlobal;
import com.br.arapps.meurancho.ui.listaitem.ListaItemFragment;

import java.util.List;

public class NovoItemFragment extends Fragment implements NovoItemViewModel.NovoItemListener {

    private NovoItemViewModel mViewModel;
    private ListaItemFragment listaItemFragment;
    private AppCompatActivity activity;
    private Toolbar toolbar;
    private EditText edtNomeItem;
    private EditText edtQuantidade;
    private AppCompatSpinner spinnerUnidade;

    private String nomeLista;
    private String nomeItemArg;

    public static NovoItemFragment newInstance() {
        return new NovoItemFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.activity = (AppCompatActivity) getActivity();

        return inflater.inflate(R.layout.novo_item_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = ViewModelProviders.of(this).get(NovoItemViewModel.class);
        mViewModel.init(this);
    }

    @Override
    public void loadObjectsView() {
        this.toolbar = getView().findViewById(R.id.toolbar);
        this.toolbar.inflateMenu(R.menu.menu_gravar);
        this.toolbar.setTitle("Novo Item");

        this.edtNomeItem = getView().findViewById(R.id.edtNomeItem);
        this.edtQuantidade = getView().findViewById(R.id.edtQuantidade);

        this.spinnerUnidade = getView().findViewById(R.id.spinnerUnidade);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, AppGlobal.getArrayUnidade());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidade.setAdapter(adapter);

        if (nomeItemArg != null){
            new AsyncTask<Void, String, ListaItem>(){
                @Override
                protected ListaItem doInBackground(Void... voids) {
                    try{
                        return AppGlobal.getDatabase().listaItemDao().getItem(nomeLista,nomeItemArg);
                    }catch (SQLiteException e) {
                        publishProgress(e.getMessage());
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(ListaItem listaItem) {
                    super.onPostExecute(listaItem);

                    if (listaItem != null){
                        edtNomeItem.setText(listaItem.nomeItem);
                        edtQuantidade.setText(String.valueOf(listaItem.qtd));

                        int position = 0;
                        List<Unidade> unidade = AppGlobal.getArrayUnidade();
                        for (Unidade u: unidade){
                            if (u.getSigla().equals(listaItem.unidade))
                                spinnerUnidade.setSelection(position, true);
                            position++;
                        }

                        edtNomeItem.requestFocus();
                    }
                }
            }.execute();
        }else
            limpaCampos();
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

    public void salvar(){
        final String nomeItem = edtNomeItem.getText().toString();
        if (nomeItem == null || nomeItem.trim().equals("")){
            AppGlobal.messageInfo(getActivity(), getString(R.string.text_msg_informe_nome_item));
            return;
        }

        final double qtd;
        String sQtd = edtQuantidade.getText().toString();
        if (sQtd == null || sQtd.trim().equals(""))
            qtd = 0;
        else
            qtd = Double.parseDouble(sQtd);

        if (qtd == 0){
            AppGlobal.messageInfo(getActivity(), getString(R.string.text_msg_informe_qtd_item));
            return;
        }

        final String unidade = AppGlobal.getArrayUnidade().get(spinnerUnidade.getSelectedItemPosition()).getSigla();

        DialogInterface.OnClickListener evtSim = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new AsyncTask<Void, String, Boolean>(){
                    @Override
                    protected Boolean doInBackground(Void... voids) {
                        ListaItem listaItem = new ListaItem();
                        listaItem.nomeLista = nomeLista;
                        listaItem.nomeItem = nomeItem;
                        listaItem.qtd = qtd;
                        listaItem.unidade = unidade;
                        listaItem.comprado = "N";

                        try {
                            if (nomeItemArg != null)
                                AppGlobal.getDatabase().listaItemDao().update(nomeItem, qtd, unidade, nomeLista, nomeItemArg);
                            else
                                AppGlobal.getDatabase().listaItemDao().insert(listaItem);

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
                            if (nomeItemArg != null) {
                                getFragmentManager().popBackStackImmediate();
                                listaItemFragment.getmViewModel().refresh("");
                            }else
                                limpaCampos();
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

        AppGlobal.messageDialog(getActivity(), getString(R.string.text_msg_salvar_item), evtSim, evtNao);
    }

    private void limpaCampos(){
        edtNomeItem.setText("");
        edtQuantidade.setText("1");
        spinnerUnidade.setSelection(0, true);

        edtNomeItem.requestFocus();
    }

    public void setListaItemFragment(ListaItemFragment listaItemFragment) {
        this.listaItemFragment = listaItemFragment;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public void setNomeItemArg(String nomeItemArg) {
        this.nomeItemArg = nomeItemArg;
    }
}
