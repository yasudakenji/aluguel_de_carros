package io.github.yasudakenji.aluguel_carroapi.repository;

import io.github.yasudakenji.aluguel_carroapi.entity.Loja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LojaRepository extends JpaRepository<Loja, UUID> {
}
