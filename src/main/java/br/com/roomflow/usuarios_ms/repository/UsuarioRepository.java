package br.com.roomflow.usuarios_ms.repository;

import br.com.roomflow.usuarios_ms.model.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    Optional<Usuario> findByIdAndAtivoTrue(Long id);

    List<Usuario> findAllByAtivoTrue(Sort sort);
}
