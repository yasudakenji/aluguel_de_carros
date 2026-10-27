package io.github.yasudakenji.aluguel_carroapi.dto;

import io.github.yasudakenji.aluguel_carroapi.entity.Loja;

import java.util.UUID;

public record LojaDTO(UUID id,
                      String endereco,
                      String cidade) {

    public Loja mapearParaLoja(){
        Loja loja = new Loja();
        loja.setCidade(this.cidade);
        loja.setEndereco(this.endereco);

        return loja;
    }

    public static LojaDTO de(Loja loja) {
        return new LojaDTO(loja.getId(), loja.getEndereco(), loja.getCidade());
    }
}
