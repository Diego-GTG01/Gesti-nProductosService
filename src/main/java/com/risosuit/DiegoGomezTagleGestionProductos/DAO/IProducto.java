package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;

public interface IProducto {

    Result<Producto> getAll();

    Result<Producto> getById(long idProducto);

    Result<Producto> getByFolio(String folio);

    Result<Producto> getByClave(String clave);

    Result<Producto> add(Producto producto);

    Result<Producto> update(Producto producto);

    Result delete();

}
