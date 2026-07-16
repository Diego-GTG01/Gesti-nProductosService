package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Rol;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.RolRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RolDAOImplementation implements IRol {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public Result<Rol> getAll() {
        Result<Rol> result = new Result<>();
        try {
            result.objects = new ArrayList<>(rolRepository.findAll());
            result.correct = true;
            result.message = "Productos obtenidos con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

}
