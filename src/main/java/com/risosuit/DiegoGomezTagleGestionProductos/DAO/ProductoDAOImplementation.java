package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Departamento;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.AuditoriaProductoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.DepartamentoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.ProductoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.TipoOperacionRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class ProductoDAOImplementation implements IProducto {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private TipoOperacionRepository tipoOperacionRepository;

    @Autowired
    private AuditoriaProductoDAOImplementation auditoriaDAO;

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
            producto.setFechaActualizacion(LocalDateTime.now());
            producto.setFechaRegistro(LocalDateTime.now());
            producto.setFolio(generarFolio(producto));
            producto.setUsuario(usuario);
            producto = productoRepository.save(producto);
            auditoriaDAO.registrarAuditoria(producto, producto.getUsuario(), "ALTA", "SE REGISTRÓ CORRECTAMENTE EL PRODUCTO");
            result.object = producto;
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
            Producto productoAnterior = productoRepository.findById(producto.getIdProducto())
                    .orElse(null);
            if (productoAnterior == null) {
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
            Departamento departamento = departamentoRepository
                    .findById(producto.getDepartamento().getIdDepartamento())
                    .orElse(null);

            if (departamento == null) {
                result.correct = false;
                result.message = "El departamento no existe";
                return result;
            }

            StringBuilder descripcion = new StringBuilder("Se modificó el producto:");

            if (!Objects.equals(productoAnterior.getNombre(), producto.getNombre())) {
                descripcion.append("\n• Nombre: '")
                        .append(productoAnterior.getNombre())
                        .append("' → '")
                        .append(producto.getNombre())
                        .append("'");
            }

            if (!Objects.equals(productoAnterior.getClave(), producto.getClave())) {
                descripcion.append("\n• Clave: '")
                        .append(productoAnterior.getClave())
                        .append("' → '")
                        .append(producto.getClave())
                        .append("'");
            }

            if ((productoAnterior.getPrecio() != producto.getPrecio())) {
                descripcion.append("\n• Precio: ")
                        .append(productoAnterior.getPrecio())
                        .append(" → ")
                        .append(producto.getPrecio());
            }

            if (!Objects.equals(productoAnterior.getStatus(), producto.getStatus())) {
                descripcion.append("\n• Status: ")
                        .append(productoAnterior.getStatus())
                        .append(" → ")
                        .append(producto.getStatus());
            }

            if (!Objects.equals(
                    productoAnterior.getDepartamento().getIdDepartamento(),
                    departamento.getIdDepartamento())) {

                descripcion.append("\n• Departamento: '")
                        .append(productoAnterior.getDepartamento().getNombre())
                        .append("' → '")
                        .append(departamento.getNombre())
                        .append("'");
            }

            if (!Objects.equals(productoAnterior.getDescripcion(), producto.getDescripcion())) {
                descripcion.append("\n• Descripción: '")
                        .append(productoAnterior.getDescripcion())
                        .append("' → '")
                        .append(producto.getDescripcion())
                        .append("'");
            }

            producto.setDepartamento(departamento);

            producto.setFechaActualizacion(LocalDateTime.now());

            producto.setFechaRegistro(productoAnterior.getFechaRegistro());
            producto.setFolio(productoAnterior.getFolio());

            result.object = productoRepository.save(producto);

            auditoriaDAO.registrarAuditoria(
                    result.object,
                    usuario,
                    "MODIFICACION",
                    descripcion.toString()
            );
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
    public Result<Producto> delete(long idProducto, long idUsuario) {
        Result<Producto> result = new Result<>();
        try {
            Optional<Producto> optionalProducto = productoRepository.findById(idProducto);
            if (!optionalProducto.isPresent()) {
                result.correct = false;
                result.message = "El producto no existe";
                return result;
            }
            Producto producto = optionalProducto.get();
            String tipoOperacion;
            if (producto.getStatus() == 1) {
                producto.setStatus(0);
                tipoOperacion = "DESACTIVACION";
            } else {
                producto.setStatus(1);
                tipoOperacion = "ACTIVACION";
            }
            producto.setFechaActualizacion(LocalDateTime.now());
            productoRepository.save(producto);
            String descripcion = String.format(
                    "Producto %s. Id=%d, Folio=%s, Clave=%s, Nombre=%s, Descripción=%s, Precio=%s, Status=%d, FechaRegistro=%s, FechaActualizacion=%s",
                    tipoOperacion.equals("ACTIVACION") ? "activado" : "desactivado",
                    producto.getIdProducto(),
                    producto.getFolio(),
                    producto.getClave(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.getStatus(),
                    producto.getFechaRegistro(),
                    producto.getFechaActualizacion()
            );

            auditoriaDAO.registrarAuditoria(
                    producto,
                    producto.getUsuario(),
                    tipoOperacion,
                    descripcion
            );

            result.correct = true;
            result.object = producto;
            result.message = tipoOperacion.equals("ACTIVACION")
                    ? "Producto activado con éxito"
                    : "Producto desactivado con éxito";

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }

        return result;
    }

    @Override
    @Transactional
    public Result<Producto> updateImagen(Producto producto, MultipartFile imagen) {
        Result<Producto> result = new Result<>();
        try {
            if (imagen == null || imagen.isEmpty()) {
                result.correct = false;
                result.message = "La imagen proporcionada está vacía o no es válida";
                return result;
            }

            Optional<Producto> productoOptional = productoRepository.findById(producto.getIdProducto());
            if (productoOptional.isEmpty()) {
                result.correct = false;
                result.message = "El producto no existe";
                return result;
            }

            Producto productoJPA = productoOptional.get();
            productoJPA.setImagen(Base64.getEncoder().encodeToString(imagen.getBytes()));
            productoJPA.setFechaActualizacion(LocalDateTime.now());
            productoJPA.setUsuario(producto.getUsuario());

            result.object = productoRepository.save(productoJPA);
            auditoriaDAO.registrarAuditoria(
                    result.object,
                    producto.getUsuario(),
                    "MODIFICACION",
                    "Se cambio la imagen"
            );
            result.correct = true;
            result.message = "Producto actualizado con éxito";

        } catch (IOException e) {
            result.correct = false;
            result.message = "Error al procesar el archivo de imagen";
            result.ex = e;
        } catch (Exception e) {
            result.correct = false;
            result.message = "Ocurrió un error inesperado: " + e.getLocalizedMessage();
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
            cad += "-" + producto.getFechaRegistro().toString();
            return cad;
        }
    }

}
