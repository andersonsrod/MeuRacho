package com.br.arapps.meurancho.global;

import android.app.Application;

import androidx.room.Room;

import com.br.arapps.meurancho.database.AppDatabase;

public class MeuRanchoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppGlobal.setAppContext(getApplicationContext());

        //Cria a base de dados
        Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "MeuRanchoDB").build();
    }
}
