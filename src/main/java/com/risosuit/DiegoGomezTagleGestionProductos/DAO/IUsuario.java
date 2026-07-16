package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;

public interface IUsuario {
    
    Result<Usuario> getAll();

    Result<Usuario> getByUsername(String username);

    Result<Usuario> getByEmail(String username);

    Result<Usuario> add(Usuario usuarioJPA);

    Result<Usuario> update(Usuario usuarioJPA);

    Result<Usuario> delete(long idUsuario);

}
