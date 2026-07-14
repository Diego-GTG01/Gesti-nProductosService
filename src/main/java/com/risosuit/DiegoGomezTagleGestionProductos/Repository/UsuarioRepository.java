package com.risosuit.DiegoGomezTagleGestionProductos.Repository;

import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByUsername(String username);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByEmail(String email);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByEmailOrUsername(String email, String username);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByEmailOrUsernameAndIdUsuarioNot(String email, String username, long idUsuario);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}
