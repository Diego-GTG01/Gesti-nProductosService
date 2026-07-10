package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;

public interface IAuditoriaProducto {

    Result<AuditoriaProducto> getAll();

    Result<AuditoriaProducto> add(AuditoriaProducto auditoria);

}
