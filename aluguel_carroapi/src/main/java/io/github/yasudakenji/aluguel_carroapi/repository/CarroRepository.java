package io.github.yasudakenji.aluguel_carroapi.repository;

import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CarroRepository extends JpaRepository <Carro, UUID> {
}

