package io.github.yasudakenji.aluguel_carroapi.service;

import io.github.yasudakenji.aluguel_carroapi.dto.CarroDTO;
import io.github.yasudakenji.aluguel_carroapi.entity.Carro;
import io.github.yasudakenji.aluguel_carroapi.entity.Loja;
import io.github.yasudakenji.aluguel_carroapi.entity.StatusCarro;
import io.github.yasudakenji.aluguel_carroapi.repository.AluguelRepository;
import io.github.yasudakenji.aluguel_carroapi.repository.CarroRepository;
import io.github.yasudakenji.aluguel_carroapi.repository.LojaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarroService {

    private final CarroRepository carroRepository;
    private final LojaRepository lojaRepository;
    private final AluguelRepository aluguelRepository;

    public CarroService(CarroRepository carroRepository,
                        LojaRepository lojaRepository, AluguelRepository aluguelRepository) {
        this.carroRepository = carroRepository;
        this.lojaRepository = lojaRepository;
        this.aluguelRepository = aluguelRepository;
    }

    public Carro salvar(Carro carro, UUID idLoja) {
        validar(carro);
        carro.setLoja(obterLoja(idLoja));
        carro.setStatus(StatusCarro.DISPONIVEL);
        carro.setId(null);
        return carroRepository.save(carro);
    }

    public List<Carro> listar() {
        return carroRepository.findAll();
    }

    public Optional<Carro> obterPorId(UUID id) {
        return carroRepository.findById(id);
    }

    public Carro atualizar(UUID id, CarroDTO dto) {
        Carro carro = carroRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));
        if (carro.getStatus() == StatusCarro.ALUGADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível editar um carro alugado");
        }
        carro.setModelo(dto.modelo());
        carro.setCategoria(dto.categoria());
        carro.setDataFabricacao(dto.dataFabricacao());
        carro.setPrecoFixo(dto.precoFixo());
        validar(carro);
        carro.setLoja(obterLoja(dto.idLoja()));
        return carroRepository.save(carro);
    }

    public void deletar(UUID id) {
        Carro carro = carroRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Carro não encontrado"));
        if (carro.getStatus() == StatusCarro.ALUGADO || aluguelRepository.existsByCarro_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O carro possui um aluguel ou histórico de aluguéis e não pode ser excluído");
        }
        carroRepository.delete(carro);
    }

    private Loja obterLoja(UUID idLoja) {
        if (idLoja == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "idLoja é obrigatório");
        }
        return lojaRepository.findById(idLoja).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));
    }

    private void validar(Carro carro) {
        if (carro.getModelo() == null || carro.getModelo().isBlank()
                || carro.getCategoria() == null || carro.getDataFabricacao() == null
                || carro.getPrecoFixo() == null || carro.getPrecoFixo().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Modelo, categoria, data de fabricação e preço positivo são obrigatórios");
        }
        if (carro.getDataFabricacao().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data de fabricação não pode estar no futuro");
        }
        carro.setModelo(carro.getModelo().trim());
    }
}
