package io.github.yasudakenji.aluguel_carroapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.UUID;

@Entity
@Table(name= "loja")
@Data
public class Loja {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "cidade")
    private String cidade;
}
