package io.github.yasudakenji.aluguel_carroapi.dto;

import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import io.github.yasudakenji.aluguel_carroapi.entity.Categoria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


public record CarroDTO(String modelo,
                       LocalDate dataFabricacao,
                       Categoria categoria,
                       BigDecimal precoFixo,
                       UUID idLoja) {

    public Carro mapearParaCarro(){
        Carro carro = new Carro();
        carro.setModelo(this.modelo);
        carro.setDataFabricacao(this.dataFabricacao);
        carro.setCategoria(this.categoria);
        carro.setPrecoFixo(this.precoFixo);

        return carro;
    }
}
