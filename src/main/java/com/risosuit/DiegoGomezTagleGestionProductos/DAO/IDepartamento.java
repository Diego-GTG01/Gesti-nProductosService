package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Departamento;


public interface IDepartamento {

    Result<Departamento> getAll();

    Result<Departamento> add(Departamento departamento);

    Result<Departamento> update(Departamento departamento);

}
