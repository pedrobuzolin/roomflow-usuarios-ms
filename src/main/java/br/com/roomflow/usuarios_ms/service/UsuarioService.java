package br.com.roomflow.usuarios_ms.service;

import br.com.roomflow.usuarios_ms.dto.AtualizacaoUsuarioDto;
import br.com.roomflow.usuarios_ms.dto.CadastroUsuarioDto;
import br.com.roomflow.usuarios_ms.dto.UsuarioDto;
import br.com.roomflow.usuarios_ms.exception.ConflitoException;
import br.com.roomflow.usuarios_ms.exception.RecursoNaoEncontradoException;
import br.com.roomflow.usuarios_ms.model.Usuario;
import br.com.roomflow.usuarios_ms.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
    private final UsuarioRepository repository;

    @Transactional(readOnly = true)
    public List<UsuarioDto> buscarTodosUsuarios() {
        return repository.findAllByAtivoTrue(Sort.by(Sort.Direction.ASC, "nome"))
                .stream()
                .map(UsuarioDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDto buscarUsuarioPorId(Long id) {
        return new UsuarioDto(buscarUsuarioAtivoPorId(id));
    }

    public Long cadastrar(CadastroUsuarioDto dto) {
        boolean jaCadastrado = repository.existsByEmail(dto.email());

        if (jaCadastrado) {
            throw new ConflitoException("E-mail já cadastrado!");
        }

        Usuario usuario = repository.save(new Usuario(dto.nome(), dto.email()));
        return usuario.getId();
    }

    public void atualizar(Long id, AtualizacaoUsuarioDto dto) {
        Usuario usuario = buscarUsuarioAtivoPorId(id);

        boolean emailEmUso = repository.existsByEmailAndIdNot(dto.email(), id);
        if (emailEmUso) {
            throw new ConflitoException("E-mail já cadastrado!");
        }

        usuario.atualizarDados(dto.nome(), dto.email());
    }

    public void deletar(Long id) {
        Usuario usuario = buscarUsuarioAtivoPorId(id);
        usuario.anonimizar();
    }

    private Usuario buscarUsuarioAtivoPorId(Long id) {
        return repository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado!"));
    }
}
