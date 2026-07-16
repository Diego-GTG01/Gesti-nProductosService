package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.risosuit.DiegoGomezTagleGestionProductos.DAO.ProductoDAOImplementation;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping(params = "idProducto")
    public ResponseEntity<Result<Producto>> getById(@RequestParam("idProducto") long idProducto) {
        Result<Producto> result = new Result<Producto>();
        try {
            result = productoDAO.getById(idProducto);
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

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Result<Producto>> add(
            @RequestPart("producto") String productoJson,
            @RequestPart("imagen") MultipartFile imagen) {
        Result<Producto> result = new Result<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            Producto producto = mapper.readValue(productoJson, Producto.class);
            if (!imagen.isEmpty()) {
                producto.setImagen(Base64.getEncoder().encodeToString(imagen.getBytes()));
            }
            result = productoDAO.add(producto);
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

    @PutMapping()
    public ResponseEntity<Result<Producto>> update(
            @RequestBody Producto producto) {
        Result<Producto> result = new Result<>();
        try {
            result = productoDAO.update(producto);
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

    @PatchMapping()
    public ResponseEntity<Result<Producto>> updateImagen(
            @RequestPart("producto") Producto productoJson,
            @RequestPart("imagen") MultipartFile imagen) {
        Result<Producto> result = new Result<>();
        try {

            result = productoDAO.updateImagen(productoJson, imagen);
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
    public ResponseEntity<Result> delete(@RequestParam("idProducto") long idProducto,
            @RequestParam("idUsuario") long idUsuario) {
        Result<Producto> result = new Result<>();
        try {

            result = productoDAO.delete(idProducto, idUsuario
            );
            
            if (result.correct) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getMessage();
            result.ex = e;
            return ResponseEntity.internalServerError().body(result);
        }
    }

}
