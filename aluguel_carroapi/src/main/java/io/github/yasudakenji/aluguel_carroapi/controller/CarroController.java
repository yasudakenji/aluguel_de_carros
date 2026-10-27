package io.github.yasudakenji.aluguel_carroapi.controller;

import io.github.yasudakenji.aluguel_carroapi.dto.CarroDTO;
import io.github.yasudakenji.aluguel_carroapi.dto.CarroResumoDTO;
import io.github.yasudakenji.aluguel_carroapi.service.CarroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/carros")
public class CarroController {

    private final CarroService service;

    public CarroController(CarroService service){
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody CarroDTO dto) {
        var carroSalvo = service.salvar(
                dto.mapearParaCarro(),
                dto.idLoja()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(carroSalvo.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<CarroResumoDTO>> listar() {
        return ResponseEntity.ok(service.listar().stream().map(CarroResumoDTO::de).toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<CarroResumoDTO> obterPorId(@PathVariable UUID id) {
        return service.obterPorId(id)
                .map(carro -> ResponseEntity.ok(CarroResumoDTO.de(carro)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> atualizar(@PathVariable UUID id, @RequestBody CarroDTO dto) {
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
