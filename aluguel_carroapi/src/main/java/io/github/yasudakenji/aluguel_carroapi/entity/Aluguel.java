package io.github.yasudakenji.aluguel_carroapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "aluguel")
@Getter
@Setter
public class Aluguel {

    @Id
    @Column(name = "id")
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_loja")
    private Loja loja;

    @ManyToOne
    @JoinColumn(name = "id_carro")
    private Carro carro;

    @ManyToOne
    @JoinColumn(name = "id_funcionario")
    private Funcionario funcionario;

    @ManyToOne
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @Column(name = "dia_alugado")
    private LocalDate data;

    @Column(name = "tempo")
    private Integer tempo;

    @Column(name = "preco", precision = 10, scale = 2)
    private BigDecimal preco;

}
