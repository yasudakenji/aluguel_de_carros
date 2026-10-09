package io.github.yasudakenji.aluguel_carroapi.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name= "cliente")
@Getter
@Setter
@ToString
public class Cliente {

    @Id
    @Column(name = "id")
    @GeneratedValue (strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cpf")
    private String cpf;

    @Column(name = "telefone")
    private String telefone;
}
