package io.github.yasudakenji.aluguel_carroapi.controller;

import io.github.yasudakenji.aluguel_carroapi.dto.LojaDTO;
import io.github.yasudakenji.aluguel_carroapi.service.LojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/loja")
@RequiredArgsConstructor
public class LojaController {

    private final LojaService service;

    @GetMapping("{id}")
    public ResponseEntity<LojaDTO> obterDetalhes(@PathVariable UUID id) {
        return service.obterPorId(id)
                .map(loja -> ResponseEntity.ok(LojaDTO.de(loja)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody LojaDTO dto){
        var lojaSalva = service.salvar(dto.mapearParaLoja());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(lojaSalva.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id){
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<LojaDTO>> pesquisar(
            @RequestParam(value = "cidade", required = false) String cidade){
        List<LojaDTO> lista = service.pesquisa(cidade).stream()
                .map(LojaDTO::de).toList();

        return ResponseEntity.ok(lista);

    }

    @PutMapping("{id}")
    public ResponseEntity<Void> atualizar(
            @PathVariable UUID id, @RequestBody LojaDTO dto){
        service.atualizar(id, dto);
        return ResponseEntity.noContent().build();
    }


}
