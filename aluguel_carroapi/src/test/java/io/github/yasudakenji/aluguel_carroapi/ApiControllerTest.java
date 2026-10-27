package io.github.yasudakenji.aluguel_carroapi;

import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import io.github.yasudakenji.aluguel_carroapi.entity.Categoria;
import io.github.yasudakenji.aluguel_carroapi.entity.Cliente;
import io.github.yasudakenji.aluguel_carroapi.entity.Loja;
import io.github.yasudakenji.aluguel_carroapi.service.CarroService;
import io.github.yasudakenji.aluguel_carroapi.service.ClienteService;
import io.github.yasudakenji.aluguel_carroapi.service.LojaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ApiControllerTest {

    @Autowired MockMvc mvc;
    @Autowired LojaService lojaService;
    @Autowired CarroService carroService;
    @Autowired ClienteService clienteService;

    @Test
    void postLojaRetornaLocationQueOGetConsegueConsultar() throws Exception {
        String json = """
                {"cidade":"Curitiba","endereco":"Rua das Flores, 100"}
                """;
        String location = mvc.perform(post("/loja")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/loja/")))
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidade").value("Curitiba"));
    }

    @Test
    void postCarroVinculaLojaEGetRetornaResumo() throws Exception {
        Loja loja = new Loja();
        loja.setCidade("Curitiba");
        loja.setEndereco("Rua das Flores, 100");
        loja = lojaService.salvar(loja);

        String json = """
                {"modelo":"Corolla","categoria":"SEDAN","dataFabricacao":"2020-01-01",
                 "precoFixo":150.00,"idLoja":"%s"}
                """.formatted(loja.getId());
        String location = mvc.perform(post("/carros")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cidadeLoja").value("Curitiba"))
                .andExpect(jsonPath("$.statusCarro").value("DISPONIVEL"));
    }

    @Test
    void uuidInvalidoRecebeErro400() throws Exception {
        mvc.perform(get("/loja/id-invalido")).andExpect(status().isBadRequest());
    }

    @Test
    void aluguelPeloCpfBloqueiaCarroAteADevolucao() throws Exception {
        Loja loja = new Loja();
        loja.setCidade("Curitiba");
        loja.setEndereco("Rua das Flores, 100");
        loja = lojaService.salvar(loja);

        Carro carro = new Carro();
        carro.setModelo("Corolla");
        carro.setCategoria(Categoria.SEDAN);
        carro.setDataFabricacao(LocalDate.of(2020, 1, 1));
        carro.setPrecoFixo(new BigDecimal("150.00"));
        carro = carroService.salvar(carro, loja.getId());

        Cliente cliente = new Cliente();
        cliente.setNome("Maria Silva");
        cliente.setCpf("12345678901");
        cliente.setTelefone("41999999999");
        clienteService.salvar(cliente);

        String json = """
                {"idCarro":"%s","cpfCliente":"12345678901","tempo":2}
                """.formatted(carro.getId());
        String location = mvc.perform(post("/alugueis")
                        .contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getHeader("Location");

        mvc.perform(get(URI.create(location).getPath()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modeloCarro").value("Corolla"));
        mvc.perform(get("/carros/{id}", carro.getId()))
                .andExpect(jsonPath("$.statusCarro").value("ALUGADO"));
        mvc.perform(post("/alugueis").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Carro indisponível para aluguel"));

        mvc.perform(post(URI.create(location).getPath() + "/devolucao"))
                .andExpect(status().isNoContent());
        mvc.perform(get("/carros/{id}", carro.getId()))
                .andExpect(jsonPath("$.statusCarro").value("DISPONIVEL"));
    }
}
