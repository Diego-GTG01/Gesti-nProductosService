
package com.risosuit.DiegoGomezTagleGestionProductos.Repository;

import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Rol;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long>{
    Optional<Rol> findByNombre(String nombre);
}
