package io.github.yasudakenji.aluguel_carroapi;

import io.github.yasudakenji.aluguel_carroapi.dto.AluguelDTO;
import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import io.github.yasudakenji.aluguel_carroapi.entity.Categoria;
import io.github.yasudakenji.aluguel_carroapi.entity.Cliente;
import io.github.yasudakenji.aluguel_carroapi.entity.Loja;
import io.github.yasudakenji.aluguel_carroapi.entity.StatusCarro;
import io.github.yasudakenji.aluguel_carroapi.repository.LojaRepository;
import io.github.yasudakenji.aluguel_carroapi.service.AluguelService;
import io.github.yasudakenji.aluguel_carroapi.service.CarroService;
import io.github.yasudakenji.aluguel_carroapi.service.ClienteService;
import io.github.yasudakenji.aluguel_carroapi.service.LojaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FluxoAluguelTest {

    @Autowired LojaService lojaService;
    @Autowired CarroService carroService;
    @Autowired ClienteService clienteService;
    @Autowired AluguelService aluguelService;
    @Autowired LojaRepository lojaRepository;

    @Test
    void alugarBloqueiaSegundoAluguelEADevolucaoLiberaOCarro() {
        Loja loja = criarLoja();
        Carro carro = criarCarro(loja);
        Cliente cliente = new Cliente();
        cliente.setNome("Maria Silva");
        cliente.setCpf("123.456.789-01");
        cliente.setTelefone("41999999999");
        clienteService.salvar(cliente);

        var aluguel = aluguelService.alugar(new AluguelDTO(carro.getId(), "12345678901", 3));
        assertEquals(new BigDecimal("450.00"), aluguel.getPreco());
        assertEquals(StatusCarro.ALUGADO, carro.getStatus());
        assertEquals(loja.getId(), aluguel.getLoja().getId());

        ResponseStatusException conflito = assertThrows(ResponseStatusException.class,
                () -> aluguelService.alugar(new AluguelDTO(carro.getId(), "12345678901", 1)));
        assertEquals(409, conflito.getStatusCode().value());

        aluguelService.devolver(aluguel.getId());
        assertEquals(StatusCarro.DISPONIVEL, carro.getStatus());
        assertNotNull(aluguel.getDataDevolucao());
    }

    @Test
    void lojaComCarroNaoPodeSerExcluida() {
        Loja loja = criarLoja();
        criarCarro(loja);

        ResponseStatusException conflito = assertThrows(ResponseStatusException.class,
                () -> lojaService.deletar(loja.getId()));
        assertEquals(409, conflito.getStatusCode().value());
        assertTrue(lojaRepository.existsById(loja.getId()));
    }

    @Test
    void clienteComCpfRepetidoNaoPodeSerCadastrado() {
        Cliente primeiro = new Cliente();
        primeiro.setNome("Ana");
        primeiro.setCpf("123.456.789-01");
        primeiro.setTelefone("41911111111");
        clienteService.salvar(primeiro);

        Cliente segundo = new Cliente();
        segundo.setNome("Outra Ana");
        segundo.setCpf("12345678901");
        segundo.setTelefone("41922222222");
        ResponseStatusException conflito = assertThrows(ResponseStatusException.class,
                () -> clienteService.salvar(segundo));
        assertEquals(409, conflito.getStatusCode().value());
    }

    private Loja criarLoja() {
        Loja loja = new Loja();
        loja.setCidade("Curitiba");
        loja.setEndereco("Rua das Flores, 100");
        return lojaService.salvar(loja);
    }

    private Carro criarCarro(Loja loja) {
        Carro carro = new Carro();
        carro.setModelo("Corolla");
        carro.setCategoria(Categoria.SEDAN);
        carro.setDataFabricacao(LocalDate.of(2020, 1, 1));
        carro.setPrecoFixo(new BigDecimal("150.00"));
        return carroService.salvar(carro, loja.getId());
    }
}
