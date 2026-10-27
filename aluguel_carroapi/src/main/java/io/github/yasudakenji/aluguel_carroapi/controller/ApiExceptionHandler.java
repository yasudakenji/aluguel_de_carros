package io.github.yasudakenji.aluguel_carroapi.controller;

import io.github.yasudakenji.aluguel_carroapi.dto.ErroResposta;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErroResposta> tratarErroDeNegocio(ResponseStatusException erro) {
        int status = erro.getStatusCode().value();
        String mensagem = erro.getReason() == null ? "Requisição inválida" : erro.getReason();
        return ResponseEntity.status(status).body(new ErroResposta(status, mensagem));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> tratarEntradaInvalida(Exception erro) {
        return ResponseEntity.badRequest().body(new ErroResposta(400,
                "Dados inválidos. Confira o JSON e o formato dos identificadores."));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> tratarVinculoNoBanco(DataIntegrityViolationException erro) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErroResposta(409,
                "A operação viola um vínculo ou uma restrição do banco de dados."));
    }
}
