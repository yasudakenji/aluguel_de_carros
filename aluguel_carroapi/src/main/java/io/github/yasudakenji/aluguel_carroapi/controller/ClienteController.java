package io.github.yasudakenji.aluguel_carroapi.controller;

import io.github.yasudakenji.aluguel_carroapi.dto.ClienteDTO;
import io.github.yasudakenji.aluguel_carroapi.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cliente")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody ClienteDTO dto) {
        var clienteSalvo = service.salvar(dto.mapearParaCliente());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(clienteSalvo.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<ClienteDTO>> listar() {
        return ResponseEntity.ok(service.listar().stream().map(ClienteDTO::de).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<ClienteDTO> obterPorId(@PathVariable UUID id) {
        return service.obterPorId(id)
                .map(cliente -> ResponseEntity.ok(ClienteDTO.de(cliente)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> atualizar(@PathVariable UUID id, @RequestBody ClienteDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
