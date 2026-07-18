package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Departamento;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.DepartamentoRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.ProductoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DepartamentoDAOImplementation implements IDepartamento {

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private ProductoDAOImplementation productoDAO;

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Result<Departamento> getAll() {
        Result<Departamento> result = new Result<>();
        try {
            result.objects = new ArrayList<>(departamentoRepository.findAll());
            result.correct = true;
            result.message = "Departamentos obtenidos con éxito"; // Corregido "Productos" por "Departamentos"
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Departamento> add(Departamento departamento) {
        Result<Departamento> result = new Result<>();
        try {
            Departamento departamentoGuardado = departamentoRepository.save(departamento);

            result.object = departamentoGuardado;

            result.correct = true;
            result.message = "Departamento agregado con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Departamento> update(Departamento departamento) {
        Result<Departamento> result = new Result<>();
        try {
            Departamento departamentoActualizado = departamentoRepository.save(departamento);

            result.object = (departamentoActualizado);
            result.correct = true;
            result.message = "Departamento actualizado con éxito";
            List<Producto> productos = productoRepository.findByDepartamento_IdDepartamento(departamentoActualizado.getIdDepartamento());
            for (Producto producto : productos) {
                producto.setDepartamento(departamentoActualizado);
                productoDAO.update(producto);
            }
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Departamento> delete(long idDepartamento) {
        Result<Departamento> result = new Result<>();
        try {
            Optional<Departamento> departamento = departamentoRepository.findById(idDepartamento);
            Departamento departamentoJPA = departamento.get();
            if (departamentoJPA.getStatus() == 1) {
                departamentoJPA.setStatus(0);
            } else {
                departamentoJPA.setStatus(1);
            }
            departamentoRepository.save(departamentoJPA);
            result.object = departamentoJPA;
            result.correct = true;
            result.message = "Departamento actualizado con éxito";

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

}
