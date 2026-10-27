package io.github.yasudakenji.aluguel_carroapi.dto;

import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import io.github.yasudakenji.aluguel_carroapi.entity.Categoria;
import io.github.yasudakenji.aluguel_carroapi.entity.StatusCarro;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CarroResumoDTO(UUID id, String modelo, LocalDate dataFabricacao,
                             Categoria categoria, StatusCarro statusCarro,
                             BigDecimal precoFixo, UUID idLoja, String cidadeLoja) {

    public static CarroResumoDTO de(Carro carro) {
        return new CarroResumoDTO(carro.getId(), carro.getModelo(), carro.getDataFabricacao(),
                carro.getCategoria(), carro.getStatus(), carro.getPrecoFixo(),
                carro.getLoja().getId(), carro.getLoja().getCidade());
    }
}
