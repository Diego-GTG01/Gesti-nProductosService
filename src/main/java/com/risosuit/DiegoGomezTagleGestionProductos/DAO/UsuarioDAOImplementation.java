package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAOImplementation implements IUsuario {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Result<Usuario> getByUsername(String username) {
        Result<Usuario> result = new Result<Usuario>();
        try {
            TypedQuery<Usuario> query = entityManager.createQuery("FROM Usuario u WHERE u.username = :username", Usuario.class);
            query.setParameter("username", username);

            Usuario usuario = query.getSingleResultOrNull();

            if (usuario == null) {
                result.message = "Usuario no encontrado";

            } else {
                result.correct = true;
                result.message = "Usuario obtenidos con exito";
                result.object = usuario;
            }

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Usuario> getByEmail(String email) {
        Result<Usuario> result = new Result<Usuario>();
        try {
            TypedQuery<Usuario> query = entityManager.createQuery("FROM Usuario u WHERE u.email = :email", Usuario.class);
            query.setParameter("email", email);

            Usuario usuario = query.getSingleResultOrNull();

            if (usuario == null) {
                result.message = "Usuario no encontrado";

            } else {
                result.correct = true;
                result.message = "Usuario obtenidos con exito";
                result.object = usuario;
            }

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Usuario> add(Usuario usuarioJPA) {
        Result<Usuario> result = new Result<Usuario>();
        try {
            TypedQuery<Usuario> query = entityManager.createQuery(
                    "FROM Usuario u WHERE u.email = :email OR u.username = :username",
                    Usuario.class
            );
            query.setParameter("email", usuarioJPA.getEmail());
            query.setParameter("username", usuarioJPA.getUsername());

            Usuario usuarioExistente = query.getSingleResultOrNull();

            if (usuarioExistente != null) {
                result.correct = false;

                if (usuarioExistente.getEmail().equalsIgnoreCase(usuarioJPA.getEmail())) {
                    result.message = "El usuario con este email ya existe.";
                } else {
                    result.message = "El nombre de usuario ya está en uso.";
                }

            } else {
                entityManager.persist(usuarioJPA);
                result.correct = true;
                result.message = "Usuario registrado con éxito";
                result.object = usuarioJPA;
            }

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Usuario> update(Usuario usuarioJPA) {
        Result<Usuario> result = new Result<Usuario>();
        try {
            TypedQuery<Usuario> query = entityManager.createQuery(
                    "FROM Usuario u WHERE (u.email = :email OR u.username = :username) AND u.idUsuario != :idUsuario",
                    Usuario.class
            );
            query.setParameter("email", usuarioJPA.getEmail());
            query.setParameter("username", usuarioJPA.getUsername());
            query.setParameter("idUsuario", usuarioJPA.getIdUsuario());

            Usuario usuarioExistente = query.getSingleResultOrNull();

            if (usuarioExistente != null) {
                result.correct = false;
                if (usuarioExistente.getEmail().equalsIgnoreCase(usuarioJPA.getEmail())) {
                    result.message = "El email ya está registrado por otro usuario.";
                } else {
                    result.message = "El nombre de usuario ya está en uso.";
                }
            } else {
                Usuario usuarioActualizado = entityManager.merge(usuarioJPA);
                result.correct = true;
                result.message = "Usuario actualizado con éxito";
                result.object = usuarioActualizado;
            }

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    public Result<Usuario> delete(long idUsuario) {
        Result<Usuario> result = new Result<Usuario>();
        try {
            Usuario usuarioExistente = entityManager.find(Usuario.class, idUsuario);

            if (usuarioExistente == null) {
                result.correct = false;
                result.message = "El usuario no existe.";
            } else {
                entityManager.remove(usuarioExistente);
                result.correct = true;
                result.message = "Usuario eliminado con éxito";
                result.object = usuarioExistente;
            }

        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }
}
