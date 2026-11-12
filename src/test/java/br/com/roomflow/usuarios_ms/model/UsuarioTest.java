package br.com.roomflow.usuarios_ms.model;

import br.com.roomflow.usuarios_ms.exception.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

class UsuarioTest {

    @DisplayName("Quando criar um usuario")
    @Nested
    class CriarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {

            @DisplayName("Dado um usuario com dados validos")
            @Test
            void teste1() {
                Usuario usuario = new Usuario("Teste", "teste@email.com");

                assertEquals("Teste", usuario.getNome());
                assertEquals("teste@email.com", usuario.getEmail());
                assertTrue(usuario.isAtivo());
            }

            @DisplayName("Dado um usuario com email com dois niveis de dominio")
            @Test
            void teste2() {
                assertDoesNotThrow(() -> new Usuario("Teste", "teste@email.com.br"));
            }
        }

        @DisplayName("Então deve lançar exceção")
        @Nested
        class Falha {
            @DisplayName("Dado um usuario com nome em branco")
            @Test
            void teste1() {
                assertThrows(ValidacaoException.class, () -> new Usuario("", "teste@email.com"));
            }

            @DisplayName("Dado um usuario com email em branco")
            @Test
            void teste2() {
                assertThrows(ValidacaoException.class, () -> new Usuario("Teste", ""));
            }

            @DisplayName("Dado um usuario com email invalido")
            @Test
            void teste3() {
                assertThrows(ValidacaoException.class, () -> new Usuario("Teste", "email-invalido"));
            }

            @DisplayName("Dado um usuario com nome null")
            @Test
            void teste4() {
                assertThrows(ValidacaoException.class, () -> new Usuario(null, "teste@email.com"));
            }

            @DisplayName("Dado um usuario com email null")
            @Test
            void teste5() {
                assertThrows(ValidacaoException.class, () -> new Usuario("Teste", null));
            }
        }
    }

    @DisplayName("Quando atualizar um usuario")
    @Nested
    class AtualizarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {
            @DisplayName("Dado um usuario com dados válidos")
            @Test
            void teste1() {
                Usuario usuario = criarUsuario();

                usuario.atualizarDados("Teste teste", "teste.teste@email.com");

                assertEquals("Teste teste", usuario.getNome());
                assertEquals("teste.teste@email.com", usuario.getEmail());
            }
        }

        @DisplayName("Então deve lançar exceção")
        @Nested
        class Falha {

            @DisplayName("Dado um usuario com nome em branco")
            @Test
            void teste1() {
                Usuario usuario = criarUsuario();

                assertThrows(ValidacaoException.class, () -> usuario.atualizarDados("", "teste@email.com"));
                assertEquals("Teste", usuario.getNome());
            }

            @DisplayName("Dado um usuario com email em branco")
            @Test
            void teste2() {
                Usuario usuario = criarUsuario();

                assertThrows(ValidacaoException.class, () -> usuario.atualizarDados("Teste", ""));
                assertEquals("teste@email.com", usuario.getEmail());
            }

            @DisplayName("Dado um usuario com email invalido")
            @Test
            void teste3() {
                Usuario usuario = criarUsuario();

                assertThrows(ValidacaoException.class, () -> usuario.atualizarDados("Teste", "email-invalido"));
                assertEquals("teste@email.com", usuario.getEmail());
            }

            @DisplayName("Dado um usuario com nome null")
            @Test
            void teste4() {
                Usuario usuario = criarUsuario();

                assertThrows(ValidacaoException.class, () -> usuario.atualizarDados(null, "teste@email.com"));
                assertEquals("Teste", usuario.getNome());
            }

            @DisplayName("Dado um usuario com email null")
            @Test
            void teste5() {
                Usuario usuario = criarUsuario();

                assertThrows(ValidacaoException.class, () -> usuario.atualizarDados("Teste", null));
                assertEquals("teste@email.com", usuario.getEmail());
            }
        }
    }

    @DisplayName("Quando anonimizar um usuario")
    @Nested
    class AnonimizarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {
            @DisplayName("Dado um usuario com dados válidos")
            @Test
            void teste1() {
                Usuario usuario = criarUsuario();

                usuario.anonimizar();

                assertEquals("Usuario Removido", usuario.getNome());
                assertFalse(usuario.isAtivo());
                assertEquals("usuario1@email.local", usuario.getEmail());
            }
        }
    }

    private Usuario criarUsuario() {
       Usuario usuario = new Usuario("Teste", "teste@email.com");
       setField(usuario, "id", 1L);
       return usuario;
    }
}