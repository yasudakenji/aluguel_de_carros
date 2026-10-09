package io.github.yasudakenji.aluguel_carroapi.repository;

import io.github.yasudakenji.aluguel_carroapi.entity.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FuncionarioRepository extends JpaRepository<Funcionario, UUID> {
}
