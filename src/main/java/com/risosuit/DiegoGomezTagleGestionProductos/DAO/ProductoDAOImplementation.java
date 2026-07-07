package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoDAOImplementation implements IProducto {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Result<Producto> getAll() {

        Result<Producto> result = new Result<Producto>();
        try {
            TypedQuery<Producto> query = entityManager.createQuery("FROM Producto p JOIN FETCH p.usuario", Producto.class);
            List<Producto> productos = query.getResultList();

            result.correct = true;
            result.message = "Productos obtenidos con exito";
            result.objects = new ArrayList<>(productos);

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Producto> add(Producto producto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Result<Producto> update(Producto producto) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Result delete() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private TypedQuery<Producto> entityManager(String from_Producto, Class<Producto> aClass) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
