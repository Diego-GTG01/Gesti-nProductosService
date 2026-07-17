package com.risosuit.DiegoGomezTagleGestionProductos.Repository;

import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>, JpaSpecificationExecutor<Producto>  {

    @EntityGraph(attributePaths = "usuario")
    List<Producto> findAll();

    @EntityGraph(attributePaths = "usuario")
    Optional<Producto> findById(Long id);

    @EntityGraph(attributePaths = "usuario")
    Optional<Producto> findByFolio(String folio);

    @EntityGraph(attributePaths = "usuario")
    Optional<Producto> findByClave(String clave);
    

}
