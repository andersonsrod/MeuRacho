package com.br.arapps.meurancho.classes;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"nomeLista", "nomeItem"})
public class ListaItem {
    @NonNull
    @ColumnInfo(name = "nomeLista")
    public String nomeLista;

    @NonNull
    @ColumnInfo(name = "nomeItem")
    public String nomeItem;

    @ColumnInfo(name = "qtd")
    public double qtd;

    @ColumnInfo(name = "unidade")
    public String unidade;

    @ColumnInfo(name = "comprado")
    public String comprado;
}
