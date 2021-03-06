package com.br.arapps.meurancho.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.br.arapps.meurancho.classes.Lista;

import java.util.List;

@Dao
public interface ListaDAO {

    //Insert
    @Insert
    void insert(Lista lista);

    //Delete
    @Delete
    void deleteObject(Lista lista);

    @Query("DELETE FROM Lista WHERE nomeLista = :nomeLista")
    void delete(String nomeLista);

    //Update
    @Query("UPDATE Lista SET nomeLista = :nomeLista WHERE nomeLista = :nomeListaOld")
    void update(String nomeLista, String nomeListaOld);

    //Select
    @Query("SELECT * FROM Lista")
    List<Lista> allListas();

    @Query("SELECT count(1) FROM ListaItem WHERE nomeLista = :nomeLista AND comprado = 'N'")
    int totalItensFaltantes(String nomeLista);
}
