package io.github.yasudakenji.aluguel_carroapi.dto;

import java.util.UUID;

public record AluguelDTO(UUID idCarro, String cpfCliente, Integer tempo) {
}
