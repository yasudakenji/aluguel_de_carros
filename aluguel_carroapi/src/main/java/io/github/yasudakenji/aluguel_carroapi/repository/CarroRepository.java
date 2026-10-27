package io.github.yasudakenji.aluguel_carroapi.repository;

import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

public interface CarroRepository extends JpaRepository <Carro, UUID> {
    boolean existsByLoja_Id(UUID idLoja);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Carro c where c.id = :id")
    Optional<Carro> findByIdParaAlugar(UUID id);
}

