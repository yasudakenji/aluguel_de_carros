package io.github.yasudakenji.aluguel_carroapi.repository;

import io.github.yasudakenji.aluguel_carroapi.entity.Cliente;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
public class Teste {

    @Autowired
    ClienteRepository repository;

    @Test
    public void salvarTest() {
        Cliente cliente = new Cliente();
        cliente.setCpf("46345678900");
        cliente.setNome("Maria");
        cliente.setTelefone("12378678");

        var clienteSalvo = repository.save(cliente);
        System.out.println("Cliente salvo:" + clienteSalvo);

    }

    @Test
    public void atualizarTest(){
        var id = UUID.fromString("a00a4a77-87d9-4633-a7f3-bd17a2fa0f38");

        Optional<Cliente> possivelCliente = repository.findById(id);

        if(possivelCliente.isPresent()){

            Cliente clienteEncontrado = possivelCliente.get();
            System.out.println("Dados do Cliente:");
            System.out.println(possivelCliente.get());

            clienteEncontrado.setNome("Joao");

            repository.save(clienteEncontrado);
        }
    }

}
