package io.github.yasudakenji.aluguel_carroapi.repository;

import io.github.yasudakenji.aluguel_carroapi.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {
}
