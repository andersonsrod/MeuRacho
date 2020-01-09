package com.br.arapps.meurancho.global;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;
import androidx.room.Room;

import com.br.arapps.meurancho.R;
import com.br.arapps.meurancho.classes.Unidade;
import com.br.arapps.meurancho.database.AppDatabase;

import java.util.ArrayList;
import java.util.List;

public final class AppGlobal {

    private static Context appContext;

    /**Retorna uma instância do banco de dados DataTestDB*/
    public static AppDatabase getDatabase(){
        return Room.databaseBuilder(appContext, AppDatabase.class, "MeuRanchoDB").build();
    }

    public static  void setAppContext(Context context) {
        appContext = context;
    }

    public static void messageInfo(Context context, String message){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(message);
        dialog.setPositiveButton(R.string.text_ok, null);
        dialog.show();
    }

    public static void messageDialog(Context context, String message, DialogInterface.OnClickListener onClickYes, DialogInterface.OnClickListener onClickNo){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(message);
        dialog.setPositiveButton(R.string.text_sim, onClickYes);

        if (onClickNo != null)
            dialog.setNegativeButton(R.string.text_nao, onClickNo);
        else
            dialog.setNegativeButton(R.string.text_nao, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

        dialog.show();
    }

    public static List<Unidade> getArrayUnidade(){
        List<Unidade> list = new ArrayList<>();

        Unidade u = new Unidade();
        u.setSigla("UN");
        u.setUnidade("Unidade");
        list.add(u);

        u = new Unidade();
        u.setSigla("KG");
        u.setUnidade("Kilograma");
        list.add(u);

        u = new Unidade();
        u.setSigla("SC");
        u.setUnidade("Saco");
        list.add(u);

        u = new Unidade();
        u.setSigla("LT");
        u.setUnidade("Litro");
        list.add(u);

        u = new Unidade();
        u.setSigla("CX");
        u.setUnidade("Caixa");
        list.add(u);

        u = new Unidade();
        u.setSigla("PCT");
        u.setUnidade("Pacote");
        list.add(u);

        u = new Unidade();
        u.setSigla("BDJ");
        u.setUnidade("Bandeja");
        list.add(u);

        return list;
    }
}
