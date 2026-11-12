package br.com.roomflow.usuarios_ms.service;

import br.com.roomflow.usuarios_ms.dto.AtualizacaoUsuarioDto;
import br.com.roomflow.usuarios_ms.dto.CadastroUsuarioDto;
import br.com.roomflow.usuarios_ms.dto.UsuarioDto;
import br.com.roomflow.usuarios_ms.exception.ConflitoException;
import br.com.roomflow.usuarios_ms.exception.RecursoNaoEncontradoException;
import br.com.roomflow.usuarios_ms.model.Usuario;
import br.com.roomflow.usuarios_ms.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    private UsuarioService service;

    @BeforeEach
    void configurar() {
        service = new UsuarioService(repository);
    }

    @DisplayName("Quando cadastrar usuario")
    @Nested
    class CadastrarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {
            @DisplayName("Dado um usuario com todos os dados")
            @Test
            void teste1() {
                CadastroUsuarioDto dto = new CadastroUsuarioDto("Teste", "teste@email.com");
                Usuario usuarioSalvo = criarUsuario(1L, "Teste", "teste@email.com");
                when(repository.existsByEmail("teste@email.com")).thenReturn(false);
                when(repository.save(any(Usuario.class))).thenReturn(usuarioSalvo);

                Long id = service.cadastrar(dto);

                assertEquals(1L, id);
                verify(repository, times(1)).save(any(Usuario.class));
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Dado um email ja cadastrado")
            @Test
            void teste1() {
                CadastroUsuarioDto dto = new CadastroUsuarioDto("Teste", "teste@email.com");
                when(repository.existsByEmail("teste@email.com")).thenReturn(true);

                assertThrows(ConflitoException.class, () -> service.cadastrar(dto));
                verify(repository, never()).save(any());
            }
        }
    }

    @DisplayName("Quando atualizar usuario")
    @Nested
    class AtualizarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {

            @DisplayName("Dado um usuario com dados válidos")
            @Test
            void teste1() {
                Usuario usuario = criarUsuario(1L, "Teste", "teste@email.com");
                when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));

                AtualizacaoUsuarioDto dto = new AtualizacaoUsuarioDto("Teste 1", "teste1@email.com");
                when(repository.existsByEmailAndIdNot("teste1@email.com", 1L)).thenReturn(false);

                service.atualizar(1L, dto);

                assertEquals("Teste 1", usuario.getNome());
                assertEquals("teste1@email.com", usuario.getEmail());
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Dado um email ja cadastrado")
            @Test
            void teste1() {
                Usuario usuario = criarUsuario(1L, "Teste", "teste@email.com");
                when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));

                AtualizacaoUsuarioDto dto = new AtualizacaoUsuarioDto("Teste 1", "teste1@email.com");
                when(repository.existsByEmailAndIdNot("teste1@email.com", 1L)).thenReturn(true);

                assertThrows(ConflitoException.class, () -> service.atualizar(1L, dto));
                assertEquals("Teste", usuario.getNome());
                assertEquals("teste@email.com", usuario.getEmail());
            }

            @DisplayName("Dado um usuario inexistente")
            @Test
            void teste2() {
                AtualizacaoUsuarioDto dto = new AtualizacaoUsuarioDto("Teste 1", "teste1@email.com");
                when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

                assertThrows(RecursoNaoEncontradoException.class, () -> service.atualizar(1L, dto));
            }
        }
    }

    @DisplayName("Quando buscar usuarios")
    @Nested
    class BuscarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {

            @DisplayName("Ao buscar um usuario com ID existente")
            @Test
            void teste1() {
                Usuario usuario = criarUsuario(1L, "Teste", "teste@email.com");
                when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));

                UsuarioDto dto = service.buscarUsuarioPorId(1L);

                assertEquals("Teste", dto.nome());
                assertEquals("teste@email.com", dto.email());
            }

            @DisplayName("Ao trazer lista de usuarios ativos")
            @Test
            void teste2() {
                Usuario usuario = criarUsuario(1L, "Teste", "teste@email.com");
                Usuario usuario2 = criarUsuario(2L, "Usuario", "usuario@email.com");
                when(repository.findAllByAtivoTrue(Sort.by(Sort.Direction.ASC,"nome"))).thenReturn(List.of(usuario, usuario2));

                List<UsuarioDto> listaUsuarios = service.buscarTodosUsuarios();

                assertEquals(2, listaUsuarios.size());
                assertEquals("Teste", listaUsuarios.get(0).nome());
                assertEquals("teste@email.com", listaUsuarios.get(0).email());
                assertEquals("Usuario", listaUsuarios.get(1).nome());
                assertEquals("usuario@email.com", listaUsuarios.get(1).email());
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Ao tentar buscar usuario inativo")
            @Test
            void teste1() {
                when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

                assertThrows(RecursoNaoEncontradoException.class, () -> service.buscarUsuarioPorId(1L));
            }
        }
    }

    @DisplayName("Quando anonimizar usuario")
    @Nested
    class AnonimizarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {
            @DisplayName("Dado um usuario com dados válidos e ativo")
            @Test
            void teste1() {
                Usuario usuario = criarUsuario(1L, "Teste", "teste@email.com");
                when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(usuario));

                service.deletar(1L);

                assertFalse(usuario.isAtivo());
                assertEquals("Usuario Removido", usuario.getNome());
                assertEquals("usuario1@email.local", usuario.getEmail());
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Dado um usuario inexistente")
            @Test
            void teste1() {
                when(repository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.empty());

                assertThrows(RecursoNaoEncontradoException.class, () -> service.deletar(1L));
            }
        }
    }

    private Usuario criarUsuario(Long id, String nome, String email) {
        Usuario usuario = new Usuario(nome, email);
        setField(usuario, "id", id);

        return usuario;
    }
}