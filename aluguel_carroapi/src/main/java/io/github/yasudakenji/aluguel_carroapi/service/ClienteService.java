package io.github.yasudakenji.aluguel_carroapi.service;

import io.github.yasudakenji.aluguel_carroapi.entity.Cliente;
import io.github.yasudakenji.aluguel_carroapi.dto.ClienteDTO;
import io.github.yasudakenji.aluguel_carroapi.repository.AluguelRepository;
import io.github.yasudakenji.aluguel_carroapi.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final AluguelRepository aluguelRepository;

    public ClienteService(ClienteRepository repository, AluguelRepository aluguelRepository) {
        this.repository = repository;
        this.aluguelRepository = aluguelRepository;
    }

    public Cliente salvar(Cliente cliente) {
        validar(cliente);
        if (repository.existsByCpf(cliente.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        cliente.setId(null);
        return repository.save(cliente);
    }

    public List<Cliente> listar() {
        return repository.findAll();
    }

    public Optional<Cliente> obterPorId(UUID id) {
        return repository.findById(id);
    }

    public Cliente atualizar(UUID id, ClienteDTO dto) {
        Cliente cliente = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        cliente.setNome(dto.nome());
        cliente.setCpf(dto.cpf());
        cliente.setTelefone(dto.telefone());
        validar(cliente);
        if (repository.existsByCpfAndIdNot(cliente.getCpf(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        return repository.save(cliente);
    }

    public void deletar(UUID id) {
        Cliente cliente = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        if (aluguelRepository.existsByCliente_Id(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O cliente possui histórico de aluguéis e não pode ser excluído");
        }
        repository.delete(cliente);
    }

    public static String normalizarCpf(String cpf) {
        return cpf == null ? "" : cpf.replaceAll("\\D", "");
    }

    private void validar(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().isBlank()
                || cliente.getTelefone() == null || cliente.getTelefone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Nome e telefone são obrigatórios");
        }
        String cpf = normalizarCpf(cliente.getCpf());
        if (cpf.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF deve ter 11 dígitos");
        }
        cliente.setNome(cliente.getNome().trim());
        cliente.setTelefone(cliente.getTelefone().trim());
        cliente.setCpf(cpf);
    }
}
