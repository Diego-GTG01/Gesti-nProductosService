package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.TipoOperacion;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.AuditoriaProductoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.TipoOperacionRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AuditoriaProductoDAOImplementation implements IAuditoriaProducto {

    @Autowired
    private AuditoriaProductoRepository auditoriaRepository;

    @Autowired
    private TipoOperacionRepository TipoOperacionRepository;

    @Override
    public Result<AuditoriaProducto> getAll() {
        Result<AuditoriaProducto> result = new Result<>();
        try {
            result.objects = new ArrayList<>(auditoriaRepository.findAll());

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
    @Transactional
    public Result<AuditoriaProducto> registrarAuditoria(
            Producto producto,
            Usuario usuario,
            String tipoOperacion,
            String descripcion) {

        Result<AuditoriaProducto> result = new Result<>();
        try {
            if (producto == null) {
                result.correct = false;
                result.message = "El producto no puede ser nulo";
                return result;
            }

            if (usuario == null) {
                result.correct = false;
                result.message = "El usuario no puede ser nulo";
                return result;
            }

            TipoOperacion operacion = TipoOperacionRepository
                    .findByNombre(tipoOperacion).get();

            if (operacion == null) {
                result.correct = false;
                result.message = "El tipo de operación no existe";
                return result;
            }

            AuditoriaProducto auditoria = new AuditoriaProducto();

            auditoria.setProducto(producto);
            auditoria.setUsuario(usuario);
            auditoria.setTipoOperacion(operacion);
            auditoria.setDescripcionCambio(descripcion);
            auditoria.setFechaOperacion(LocalDateTime.now());

            result.object = auditoriaRepository.save(auditoria);
            result.correct = true;
            result.message = "Auditoría registrada correctamente";

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }

        return result;
    }

}
