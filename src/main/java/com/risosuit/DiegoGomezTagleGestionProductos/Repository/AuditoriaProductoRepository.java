package com.risosuit.DiegoGomezTagleGestionProductos.Repository;

import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditoriaProductoRepository extends JpaRepository<AuditoriaProducto, Long> {

    @EntityGraph(attributePaths = "usuario")
    @Override
    List<AuditoriaProducto> findAll();

    @EntityGraph(attributePaths = "usuario")
    @Override
    Optional<AuditoriaProducto> findById(Long id);
}
