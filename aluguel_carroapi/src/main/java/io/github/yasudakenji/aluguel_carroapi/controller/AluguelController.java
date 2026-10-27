package io.github.yasudakenji.aluguel_carroapi.controller;

import io.github.yasudakenji.aluguel_carroapi.dto.AluguelDTO;
import io.github.yasudakenji.aluguel_carroapi.dto.AluguelResumoDTO;
import io.github.yasudakenji.aluguel_carroapi.service.AluguelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/alugueis")
public class AluguelController {

    private final AluguelService service;

    public AluguelController(AluguelService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> alugar(@RequestBody AluguelDTO dto) {
        var aluguelSalvo = service.alugar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(aluguelSalvo.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<AluguelResumoDTO>> listar() {
        return ResponseEntity.ok(service.listar().stream().map(AluguelResumoDTO::de).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<AluguelResumoDTO> obterPorId(@PathVariable UUID id) {
        return service.obterPorId(id)
                .map(aluguel -> ResponseEntity.ok(AluguelResumoDTO.de(aluguel)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("{id}/devolucao")
    public ResponseEntity<Void> devolver(@PathVariable UUID id) {
        service.devolver(id);
        return ResponseEntity.noContent().build();
    }
}
