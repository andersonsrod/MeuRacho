package com.br.arapps.meurancho.classes;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Unidade implements Serializable {
    private String sigla;
    private String Unidade;

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public String getUnidade() {
        return Unidade;
    }

    public void setUnidade(String unidade) {
        Unidade = unidade;
    }

    @NonNull
    @Override
    public String toString() {
        return getUnidade();
    }
}
