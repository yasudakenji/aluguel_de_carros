package io.github.yasudakenji.aluguel_carroapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "carro")
@Getter
@Setter
public class Carro {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID )
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_loja")
    private Loja loja;

    @Column(name = "modelo")
    private String modelo;

    @Column (name = "data_fabricacao")
    private LocalDate dataFabricacao;

    @Enumerated(EnumType.STRING)
    @Column(name ="categoria")
    private Categoria categoria;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusCarro status = StatusCarro.DISPONIVEL;

    @Column(name = "preco_fixo")
    private BigDecimal precoFixo;

}
