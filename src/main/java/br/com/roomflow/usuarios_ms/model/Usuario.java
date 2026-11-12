package br.com.roomflow.usuarios_ms.model;

import br.com.roomflow.usuarios_ms.exception.ValidacaoException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Entity
@Table(name="usuarios")
@NoArgsConstructor
@Getter
public class Usuario {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private boolean ativo;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[a-zA-Z]{2,}$");

    public Usuario(String nome, String email) {
        validarNome(nome);
        validarEmail(email);

        this.nome = nome;
        this.email = email;
        this.ativo = true;
    }

    public void atualizarDados(String nome, String email) {
        validarNome(nome);
        validarEmail(email);

        this.nome = nome;
        this.email = email;
    }

    public void anonimizar() {
        this.nome = "Usuario Removido";
        this.email = "usuario" + this.id + "@email.local";
        this.ativo = false;
    }

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException("Nome de usuário obrigatório!");
        }
    }

    private void validarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidacaoException("E-mail do usuário obrigatório!");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidacaoException("E-mail inválido!");
        }
    }
}
