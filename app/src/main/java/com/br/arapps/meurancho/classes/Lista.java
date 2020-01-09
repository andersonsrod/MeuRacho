package com.br.arapps.meurancho.classes;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Lista implements Serializable {
    @NonNull
    @PrimaryKey
    public String nomeLista;

    @NonNull
    @Override
    public String toString() {
        return nomeLista;
    }
}
