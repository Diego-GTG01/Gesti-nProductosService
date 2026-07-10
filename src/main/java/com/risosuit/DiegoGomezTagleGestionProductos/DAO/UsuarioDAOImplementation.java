package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAOImplementation implements IUsuario {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Result<Usuario> getByUsername(String username) {
        Result<Usuario> result = new Result<>();
        try {
            Optional<Usuario> usuario = usuarioRepository.findByUsername(username);
            if (usuario.isPresent()) {
                result.correct = true;
                result.message = "Usuario obtenido con éxito";
                result.object = usuario.get();
            } else {
                result.correct = false;
                result.message = "Usuario no encontrado";
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
        Result<Usuario> result = new Result<>();
        try {
            Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
            if (usuario.isPresent()) {
                result.correct = true;
                result.message = "Usuario obtenido con éxito";
                result.object = usuario.get();
            } else {
                result.correct = false;
                result.message = "Usuario no encontrado";
            }
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    @Transactional
    public Result<Usuario> add(Usuario usuario) {
        Result<Usuario> result = new Result<>();
        try {
            Optional<Usuario> existente
                    = usuarioRepository.findByEmailOrUsername(
                            usuario.getEmail(),
                            usuario.getUsername());
            if (existente.isPresent()) {
                result.correct = false;
                if (existente.get().getEmail().equalsIgnoreCase(usuario.getEmail())) {
                    result.message = "El usuario con este email ya existe.";
                } else {
                    result.message = "El nombre de usuario ya está en uso.";
                }
                return result;
            }
            result.object = usuarioRepository.save(usuario);
            result.correct = true;
            result.message = "Usuario registrado con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    @Transactional
    public Result<Usuario> update(Usuario usuario) {
        Result<Usuario> result = new Result<>();
        try {
            if (!usuarioRepository.existsById(usuario.getIdUsuario())) {
                result.correct = false;
                result.message = "El usuario no existe.";
                return result;
            }
            Optional<Usuario> existente
                    = usuarioRepository.findByEmailOrUsernameAndIdUsuarioNot(
                            usuario.getEmail(),
                            usuario.getUsername(),
                            usuario.getIdUsuario());
            if (existente.isPresent()) {
                result.correct = false;
                if (existente.get().getEmail().equalsIgnoreCase(usuario.getEmail())) {
                    result.message = "El email ya está registrado por otro usuario.";
                } else {
                    result.message = "El nombre de usuario ya está en uso.";
                }
                return result;
            }
            result.object = usuarioRepository.save(usuario);
            result.correct = true;
            result.message = "Usuario actualizado con éxito";
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }

    @Override
    @Transactional
    public Result<Usuario> delete(long idUsuario) {
        Result<Usuario> result = new Result<>();
        try {
            Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);
            if (usuario.isEmpty()) {
                result.correct = false;
                result.message = "El usuario no existe.";
                return result;
            }
            usuarioRepository.deleteById(idUsuario);
            result.correct = true;
            result.message = "Usuario eliminado con éxito";
            result.object = usuario.get();
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }
}
