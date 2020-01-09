package com.br.arapps.meurancho.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.br.arapps.meurancho.classes.ListaItem;

import java.util.List;

@Dao
public interface ListaItemDAO {
    //Insert
    @Insert
    void insert(ListaItem listaItem);

    @Query("INSERT INTO ListaItem SELECT :nomeListaNova, nomeItem, qtd, unidade, 'N' FROM ListaItem WHERE nomeLista = :nomeListaCopia")
    void importar(String nomeListaNova, String nomeListaCopia);

    //Delete
    @Query("DELETE FROM ListaItem WHERE nomeLista = :nomeLista AND nomeItem = :nomeItem")
    void delete(String nomeLista, String nomeItem);

    @Query("DELETE FROM ListaItem WHERE nomeLista = :nomeLista")
    void deleteAll(String nomeLista);

    //Update
    @Query("UPDATE ListaItem SET nomeItem =:nomeItem, qtd = :qtd, unidade = :unidade WHERE nomeLista = :nomeLista AND nomeItem = :nomeItemOld")
    void update(String nomeItem, double qtd, String unidade, String nomeLista, String nomeItemOld);

    @Query("UPDATE ListaItem SET comprado = :comprado WHERE nomeLista = :nomeLista AND nomeItem = :nomeItem")
    void setComprado(String comprado, String nomeLista, String nomeItem);

    @Query("UPDATE ListaItem SET nomeLista = :nomeLista WHERE nomeLista = :nomeListaOld")
    void atualizaChave(String nomeLista, String nomeListaOld);

    //Select
    @Query("SELECT * FROM ListaItem WHERE nomeLista = :nomeLista")
    List<ListaItem> allItens(String nomeLista);

    @Query("SELECT * FROM ListaItem WHERE nomeLista = :nomeLista AND upper(nomeItem) LIKE :nomeItem")
    List<ListaItem> itensFiltro(String nomeLista, String nomeItem);

    @Query("SELECT * FROM ListaItem WHERE nomeLista = :nomeLista AND nomeItem = :nomeItem")
    ListaItem getItem(String nomeLista, String nomeItem);
}
