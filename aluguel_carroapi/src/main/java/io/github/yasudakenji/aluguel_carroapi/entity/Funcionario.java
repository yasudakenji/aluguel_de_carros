package io.github.yasudakenji.aluguel_carroapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name= "funcionario")
@Getter
@Setter
public class Funcionario {

    @Id
    @Column(name = "id")
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn (name = "id_loja")
    private Loja loja;

    @Column(name ="nome")
    private String nome;
}
