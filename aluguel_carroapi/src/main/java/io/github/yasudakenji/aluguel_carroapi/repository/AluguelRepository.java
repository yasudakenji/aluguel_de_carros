package io.github.yasudakenji.aluguel_carroapi.repository;

import io.github.yasudakenji.aluguel_carroapi.entity.Aluguel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AluguelRepository extends JpaRepository<Aluguel, UUID> {
    boolean existsByLoja_Id(UUID idLoja);
    boolean existsByCarro_Id(UUID idCarro);
    boolean existsByCliente_Id(UUID idCliente);
}
