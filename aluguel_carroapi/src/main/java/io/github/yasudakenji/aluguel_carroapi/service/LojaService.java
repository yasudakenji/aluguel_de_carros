package io.github.yasudakenji.aluguel_carroapi.service;

import io.github.yasudakenji.aluguel_carroapi.dto.LojaDTO;
import io.github.yasudakenji.aluguel_carroapi.entity.Loja;
import io.github.yasudakenji.aluguel_carroapi.repository.AluguelRepository;
import io.github.yasudakenji.aluguel_carroapi.repository.CarroRepository;
import io.github.yasudakenji.aluguel_carroapi.repository.LojaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LojaService {

    private final LojaRepository repository;
    private final CarroRepository carroRepository;
    private final AluguelRepository aluguelRepository;

    public LojaService(LojaRepository repository, CarroRepository carroRepository,
                       AluguelRepository aluguelRepository) {
        this.repository = repository;
        this.carroRepository = carroRepository;
        this.aluguelRepository = aluguelRepository;
    }

    public Loja salvar(Loja loja) {
        validar(loja);
        loja.setId(null);
        return repository.save(loja);
    }

    public List<Loja> listar() {
        return repository.findAll();
    }

    public Optional<Loja> obterPorId(UUID id) {
        return repository.findById(id);
    }

    public List<Loja> pesquisa(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            return listar();
        }
        return repository.findByCidadeIgnoreCase(cidade.trim());
    }

    public Loja atualizar(UUID id, LojaDTO dto) {
        Loja loja = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));
        loja.setCidade(dto.cidade());
        loja.setEndereco(dto.endereco());
        validar(loja);
        return repository.save(loja);
    }

    public void deletar(UUID id) {
        Loja loja = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));
        if (carroRepository.existsByLoja_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Só é possível excluir a loja quando não houver carros vinculados");
        }
        if (aluguelRepository.existsByLoja_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A loja possui histórico de aluguéis e não pode ser excluída");
        }
        repository.delete(loja);
    }

    private void validar(Loja loja) {
        if (loja.getCidade() == null || loja.getCidade().isBlank()
                || loja.getEndereco() == null || loja.getEndereco().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cidade e endereço são obrigatórios");
        }
        loja.setCidade(loja.getCidade().trim());
        loja.setEndereco(loja.getEndereco().trim());
    }
}
