
package com.risosuit.DiegoGomezTagleGestionProductos.Services;

import com.risosuit.DiegoGomezTagleGestionProductos.Repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository repository;
    
    

}
