package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.risosuit.DiegoGomezTagleGestionProductos.DAO.ProductoDAOImplementation;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/producto")
public class ProductoRestController {

    @Autowired
    private ProductoDAOImplementation productoDAO;

    @GetMapping
    public ResponseEntity<Result<Producto>> getAll() {
        Result<Producto> result = new Result<Producto>();

        try {
            result = productoDAO.getAll();
            if (result.correct) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
            return ResponseEntity.internalServerError().body(result);
        }
    }

}
