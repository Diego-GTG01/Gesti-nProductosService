package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;

public interface IAuditoriaProducto {

    Result<AuditoriaProducto> getAll();

    Result<AuditoriaProducto> registrarAuditoria(Producto producto, Usuario usuario,
            String tipoOperacion, String descripcion);

}
