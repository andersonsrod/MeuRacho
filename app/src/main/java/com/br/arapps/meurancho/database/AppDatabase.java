package com.br.arapps.meurancho.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.br.arapps.meurancho.classes.Lista;
import com.br.arapps.meurancho.classes.ListaItem;
import com.br.arapps.meurancho.dao.ListaDAO;
import com.br.arapps.meurancho.dao.ListaItemDAO;

@Database(entities = {Lista.class, ListaItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ListaDAO listaDao();

    public abstract ListaItemDAO listaItemDao();
}
