package io.github.yasudakenji.aluguel_carroapi.dto;

import io.github.yasudakenji.aluguel_carroapi.entity.Cliente;

import java.util.UUID;

public record ClienteDTO (UUID id, String nome, String cpf, String telefone) {

    public Cliente mapearParaCliente(){
        Cliente cliente = new Cliente();
        cliente.setNome(this.nome);
        cliente.setCpf(this.cpf);
        cliente.setTelefone(this.telefone);

        return cliente;
    }

    public static ClienteDTO de(Cliente cliente) {
        return new ClienteDTO(cliente.getId(), cliente.getNome(), cliente.getCpf(), cliente.getTelefone());
    }
}
