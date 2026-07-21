package com.risosuit.DiegoGomezTagleGestionProductos.Repository;

import com.risosuit.DiegoGomezTagleGestionProductos.JPA.TipoOperacion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TipoOperacionRepository extends JpaRepository<TipoOperacion, Long>{
    
    Optional<TipoOperacion> findByNombre(String nombre);
    
}
