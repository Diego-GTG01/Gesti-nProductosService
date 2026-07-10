/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;


public interface TipoOperacion {
    Result<TipoOperacion> getAll();

    Result<TipoOperacion> add(TipoOperacion tipoOperacion);

    Result<TipoOperacion> update(TipoOperacion tipoOperacion);
}
