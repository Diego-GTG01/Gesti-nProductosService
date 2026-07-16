package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.risosuit.DiegoGomezTagleGestionProductos.DAO.RolDAOImplementation;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Rol;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.RolRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/role")
public class RolRestController {

    @Autowired
    private RolDAOImplementation rolDAO;

    @GetMapping
    public ResponseEntity<Result<Rol>> getAll() {
        Result<Rol> result = new Result<Rol>();
        try {
            result = rolDAO.getAll();
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
