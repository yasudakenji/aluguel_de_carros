package io.github.yasudakenji.aluguel_carroapi.service;

import io.github.yasudakenji.aluguel_carroapi.dto.AluguelDTO;
import io.github.yasudakenji.aluguel_carroapi.entity.Aluguel;
import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import io.github.yasudakenji.aluguel_carroapi.entity.Cliente;
import io.github.yasudakenji.aluguel_carroapi.entity.StatusCarro;
import io.github.yasudakenji.aluguel_carroapi.repository.AluguelRepository;
import io.github.yasudakenji.aluguel_carroapi.repository.CarroRepository;
import io.github.yasudakenji.aluguel_carroapi.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AluguelService {

    private final AluguelRepository repository;
    private final CarroRepository carroRepository;
    private final ClienteRepository clienteRepository;

    public AluguelService(AluguelRepository repository, CarroRepository carroRepository,
                          ClienteRepository clienteRepository) {
        this.repository = repository;
        this.carroRepository = carroRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Aluguel alugar(AluguelDTO dto) {
        if (dto == null || dto.idCarro() == null || dto.tempo() == null || dto.tempo() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Carro e quantidade de dias positiva são obrigatórios");
        }
        String cpf = ClienteService.normalizarCpf(dto.cpfCliente());
        if (cpf.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF deve ter 11 dígitos");
        }

        Carro carro = carroRepository.findByIdParaAlugar(dto.idCarro()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));
        Cliente cliente = clienteRepository.findByCpf(cpf).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado para o CPF informado"));
        if (carro.getStatus() != StatusCarro.DISPONIVEL) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Carro indisponível para aluguel");
        }

        Aluguel aluguel = new Aluguel();
        aluguel.setCarro(carro);
        aluguel.setCliente(cliente);
        aluguel.setLoja(carro.getLoja());
        aluguel.setData(LocalDate.now());
        aluguel.setTempo(dto.tempo());
        aluguel.setPreco(carro.getPrecoFixo().multiply(BigDecimal.valueOf(dto.tempo())));
        carro.setStatus(StatusCarro.ALUGADO);
        return repository.save(aluguel);
    }

    public List<Aluguel> listar() {
        return repository.findAll();
    }

    public Optional<Aluguel> obterPorId(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public void devolver(UUID id) {
        Aluguel aluguel = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluguel não encontrado"));
        if (aluguel.getDataDevolucao() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Aluguel já devolvido");
        }
        Carro carro = carroRepository.findByIdParaAlugar(aluguel.getCarro().getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));
        aluguel.setDataDevolucao(LocalDate.now());
        carro.setStatus(StatusCarro.DISPONIVEL);
    }
}
