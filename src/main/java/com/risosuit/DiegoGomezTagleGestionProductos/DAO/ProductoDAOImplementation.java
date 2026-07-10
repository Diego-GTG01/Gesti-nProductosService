package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Departamento;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.DepartamentoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.ProductoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoDAOImplementation implements IProducto {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Override
    public Result<Producto> getAll() {
        Result<Producto> result = new Result<>();
        try {
            result.objects = new ArrayList<>(productoRepository.findAll());
            result.correct = true;
            result.message = "Productos obtenidos con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Producto> getById(long idProducto) {
        Result<Producto> result = new Result<>();
        try {
            Optional<Producto> producto = productoRepository.findById(idProducto);
            if (producto.isPresent()) {
                result.correct = true;
                result.message = "Producto obtenido con éxito";
                result.object = producto.get();
            } else {
                result.correct = false;
                result.message = "Producto no encontrado";
            }
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Producto> getByFolio(String folio) {
        Result<Producto> result = new Result<>();
        try {
            Optional<Producto> producto = productoRepository.findByFolio(folio);
            if (producto.isPresent()) {
                result.correct = true;
                result.message = "Producto obtenido con éxito";
                result.object = producto.get();

            } else {
                result.correct = false;
                result.message = "Producto no encontrado";
            }
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Producto> getByClave(String clave) {
        Result<Producto> result = new Result<>();
        try {
            Optional<Producto> producto = productoRepository.findByClave(clave);
            if (producto.isPresent()) {
                result.correct = true;
                result.message = "Producto obtenido con éxito";
                result.object = producto.get();
            } else {
                result.correct = false;
                result.message = "Producto no encontrado";
            }
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    @Transactional
    public Result<Producto> add(Producto producto) {
        Result<Producto> result = new Result<>();
        try {
            if (producto == null) {
                result.correct = false;
                result.message = "El producto no puede ser nulo";
                return result;
            }
            if (producto.getUsuario() == null) {
                result.correct = false;
                result.message = "Debe seleccionar un usuario";
                return result;
            }
            Usuario usuario = usuarioRepository.findById(producto.getUsuario().getIdUsuario())
                    .orElse(null);
            Departamento departamento = departamentoRepository.findById(producto.getDepartamento().getIdDepartamento()).orElse(null);
            if (usuario == null) {
                result.correct = false;
                result.message = "El usuario no existe";
                return result;
            }
            if (departamento == null) {
                result.correct = false;
                result.message = "El usuario no existe";
                return result;
            }
            producto.setDepartamento(departamento);
            producto.setFolio(generarFolio(producto));
            producto.setUsuario(usuario);
            producto.setFechaActualizacion(LocalDateTime.now());
            producto.setFechaRegistro(LocalDateTime.now());
            result.object = productoRepository.save(producto);
            result.correct = true;
            result.message = "Producto guardado con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    @Transactional
    public Result<Producto> update(Producto producto) {
        Result<Producto> result = new Result<>();
        try {
            if (!productoRepository.existsById(producto.getIdProducto())) {
                result.correct = false;
                result.message = "El producto no existe";
                return result;
            }
            Usuario usuario = usuarioRepository.findById(producto.getUsuario().getIdUsuario())
                    .orElse(null);
            if (usuario == null) {
                result.correct = false;
                result.message = "El usuario no existe";
                return result;
            }
            producto.setUsuario(usuario);
            result.object = productoRepository.save(producto);
            result.correct = true;
            result.message = "Producto actualizado con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    @Transactional
    public Result delete(long idProducto) {
        Result<Producto> result = new Result<>();
        try {
            if (!productoRepository.existsById(idProducto)) {
                result.correct = false;
                result.message = "El producto no existe";
                return result;
            }
            productoRepository.deleteById(idProducto);
            result.correct = true;
            result.message = "Producto eliminado con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    private String generarFolio(Producto producto) {
        if (producto == null) {
            return null;
        } else {
            String cad = "";
            cad += producto.getDepartamento().getPrefijo();
            cad += "-" + LocalDateTime.now().toString();
            return cad;
        }
    }

}
