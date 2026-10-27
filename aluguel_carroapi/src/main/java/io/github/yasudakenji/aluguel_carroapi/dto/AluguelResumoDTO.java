package io.github.yasudakenji.aluguel_carroapi.dto;

import io.github.yasudakenji.aluguel_carroapi.entity.Aluguel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record AluguelResumoDTO(UUID id, UUID idCarro, String modeloCarro,
                               UUID idCliente, String nomeCliente, String cpfCliente,
                               UUID idLoja, String cidadeLoja, LocalDate data,
                               Integer tempo, BigDecimal preco, LocalDate dataDevolucao) {

    public static AluguelResumoDTO de(Aluguel aluguel) {
        return new AluguelResumoDTO(aluguel.getId(), aluguel.getCarro().getId(),
                aluguel.getCarro().getModelo(), aluguel.getCliente().getId(),
                aluguel.getCliente().getNome(), aluguel.getCliente().getCpf(),
                aluguel.getLoja().getId(), aluguel.getLoja().getCidade(),
                aluguel.getData(), aluguel.getTempo(), aluguel.getPreco(),
                aluguel.getDataDevolucao());
    }
}
