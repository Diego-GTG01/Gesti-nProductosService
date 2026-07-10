package com.risosuit.DiegoGomezTagleGestionProductos.Repository;

import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByEmailOrUsername(String email, String username);

    Optional<Usuario> findByEmailOrUsernameAndIdUsuarioNot(String email, String username, long idUsuario);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

}
