package br.com.roomflow.usuarios_ms.controller;

import br.com.roomflow.usuarios_ms.dto.AtualizacaoUsuarioDto;
import br.com.roomflow.usuarios_ms.dto.CadastroUsuarioDto;
import br.com.roomflow.usuarios_ms.dto.UsuarioDto;
import br.com.roomflow.usuarios_ms.exception.ConflitoException;
import br.com.roomflow.usuarios_ms.exception.RecursoNaoEncontradoException;
import br.com.roomflow.usuarios_ms.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {
    @MockitoBean
    private UsuarioService usuarioService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("Quando listar os usuarios")
    @Nested
    class ListarUsuarios {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {

            @DisplayName("Ao listar todos os usuarios")
            @Test
            void teste1() throws Exception {
                when(usuarioService.buscarTodosUsuarios()).thenReturn(List.of());

                MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/usuarios")
                ).andReturn().getResponse();

                assertEquals(200, response.getStatus());
            }

            @DisplayName("Ao buscar usuario por ID existente")
            @Test
            void teste2() throws Exception {
                UsuarioDto usuarioDto = new UsuarioDto(1L, "Teste", "teste@email.com");
                when(usuarioService.buscarUsuarioPorId(1L)).thenReturn(usuarioDto);

                MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/usuarios/{id}", 1)
                ).andReturn().getResponse();

                assertEquals(200, response.getStatus());
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Ao buscar usuario com ID inexistente")
            @Test
            void teste1() throws Exception {
                Long id = 1L;
                when(usuarioService.buscarUsuarioPorId(id)).thenThrow(new RecursoNaoEncontradoException("Usuario não encontrado!"));

                MockHttpServletResponse response = mockMvc.perform(
                        get("/api/v1/usuarios/{id}", id)
                ).andReturn().getResponse();

                assertEquals(404, response.getStatus());
            }
        }
    }

    @DisplayName("Quando cadastrar usuarios")
    @Nested
    class CadastrarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {

            @DisplayName("Dado um usuario com todos os campos")
            @Test
            void teste1() throws Exception {
                CadastroUsuarioDto dto = new CadastroUsuarioDto("Teste", "teste@email.com");
                when(usuarioService.cadastrar(dto)).thenReturn(1L);

                MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                ).andReturn().getResponse();

                assertEquals(201, response.getStatus());
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Dado um usuario com dados invalidos")
            @Test
            void teste1() throws Exception {
                CadastroUsuarioDto dtoInvalido = new CadastroUsuarioDto("", "teste-email");

                MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dtoInvalido))
                ).andReturn().getResponse();

                assertEquals(400, response.getStatus());
            }

            @DisplayName("Dado um usuario com email já cadastrado")
            @Test
            void teste2() throws Exception {
                CadastroUsuarioDto dto = new CadastroUsuarioDto("Teste", "teste@email.com");
                when(usuarioService.cadastrar(dto)).thenThrow(new ConflitoException("E-mail já cadastrado!"));

                MockHttpServletResponse response = mockMvc.perform(
                        post("/api/v1/usuarios")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                ).andReturn().getResponse();

                assertEquals(409, response.getStatus());
            }
        }
    }

    @DisplayName("Quando atualizar um usuario")
    @Nested
    class AtualizarUsuario {

        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {

            @DisplayName("Ao atualizar um usuario com todos os campos")
            @Test
            void teste1() throws Exception {
                Long id = 1L;
                AtualizacaoUsuarioDto dto = new AtualizacaoUsuarioDto("Teste", "teste@email.com");

                MockHttpServletResponse response = mockMvc.perform(
                        put("/api/v1/usuarios/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                ).andReturn().getResponse();

                assertEquals(200, response.getStatus());
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Ao atualizar um usuario com dados invalidos")
            @Test
            void teste1() throws Exception {
                Long id = 1L;
                AtualizacaoUsuarioDto dto = new AtualizacaoUsuarioDto("", "teste-email");

                MockHttpServletResponse response = mockMvc.perform(
                        put("/api/v1/usuarios/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                ).andReturn().getResponse();

                assertEquals(400, response.getStatus());
            }

            @DisplayName("Ao tentar atualizar um usuario inexistente")
            @Test
            void teste2() throws Exception {
                Long id = 1L;
                AtualizacaoUsuarioDto dto = new AtualizacaoUsuarioDto("Teste", "teste@email.com");
                doThrow(new RecursoNaoEncontradoException("Usuário não encontrado!"))
                        .when(usuarioService).atualizar(id, dto);

                MockHttpServletResponse response = mockMvc.perform(
                        put("/api/v1/usuarios/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto))
                ).andReturn().getResponse();

                assertEquals(404, response.getStatus());
            }
        }
    }

    @DisplayName("Quando anonimizar um usuario")
    @Nested
    class AnonimizarUsuario {
        @DisplayName("Então deve executar com sucesso")
        @Nested
        class Sucesso {

            @DisplayName("Ao anonimizar um usuario com ID válido")
            @Test
            void teste1() throws Exception {
                Long id = 1L;

                MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/usuarios/{id}", id)
                ).andReturn().getResponse();

                assertEquals(204, response.getStatus());
            }
        }

        @DisplayName("Então deve retornar erro")
        @Nested
        class Falha {

            @DisplayName("Ao tentar anonimizar usuario com ID inexistente")
            @Test
            void teste1() throws Exception {
                Long id = 1L;
                doThrow(new RecursoNaoEncontradoException("Usuário não encontrado!"))
                        .when(usuarioService).deletar(id);

                MockHttpServletResponse response = mockMvc.perform(
                        delete("/api/v1/usuarios/{id}", id)
                ).andReturn().getResponse();

                assertEquals(404, response.getStatus());
            }
        }
    }
}