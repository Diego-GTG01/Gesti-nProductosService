package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;


public interface TipoOperacion {
    Result<TipoOperacion> getAll();

    Result<TipoOperacion> add(TipoOperacion tipoOperacion);

    Result<TipoOperacion> update(TipoOperacion tipoOperacion);
}
