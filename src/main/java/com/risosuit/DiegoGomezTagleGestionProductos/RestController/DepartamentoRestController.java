package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.risosuit.DiegoGomezTagleGestionProductos.DAO.DepartamentoDAOImplementation;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Departamento;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping; // Añadida para la actualización
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/departamento")
public class DepartamentoRestController {

    @Autowired
    private DepartamentoDAOImplementation departamentoDAO;

    @Autowired
    private ProductoRepository productoRepository;

    @GetMapping
    public ResponseEntity<Result<Departamento>> getAll() {
        Result<Departamento> result = new Result<Departamento>();
        try {
            result = departamentoDAO.getAll();
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

    @PostMapping
    public ResponseEntity<Result<Departamento>> add(@RequestBody Departamento departamento) {
        Result<Departamento> result = new Result<Departamento>(); // Especificado el tipo genérico por buena práctica
        try {
            result = departamentoDAO.add(departamento);
            if (result.correct) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getMessage();
            result.ex = e;
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @PutMapping
    public ResponseEntity<Result<Departamento>> update(@RequestBody Departamento departamento) {
        Result<Departamento> result = new Result<Departamento>();
        try {
            result = departamentoDAO.update(departamento);
            if (result.correct) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getMessage();
            result.ex = e;
            return ResponseEntity.internalServerError().body(result);
        }
    }

    @DeleteMapping
    public ResponseEntity<Result<Departamento>> delete(@RequestParam("idProducto") long idProducto) {
        Result<Departamento> result = new Result<Departamento>();
        try {
            result = departamentoDAO.delete(idProducto);
            if (result.correct) {
                return ResponseEntity.ok(result);
            }
            return ResponseEntity.badRequest().body(result);
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getMessage();
            result.ex = e;
            return ResponseEntity.internalServerError().body(result);
        }
    }
    
    

}
