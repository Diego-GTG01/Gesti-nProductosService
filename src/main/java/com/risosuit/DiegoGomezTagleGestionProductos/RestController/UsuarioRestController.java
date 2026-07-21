package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.risosuit.DiegoGomezTagleGestionProductos.DAO.UsuarioDAOImplementation;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.UsuarioDTO;

import com.risosuit.DiegoGomezTagleGestionProductos.JPA.AuditoriaProducto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.UsuarioRepository;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/user")
public class UsuarioRestController {

    @Autowired
    private UsuarioDAOImplementation usuarioDAO;

    @GetMapping
    public ResponseEntity<Result<UsuarioDTO>> getAll() {
        Result<Usuario> result = new Result<Usuario>();
        Result<UsuarioDTO> resultDTO = new Result<UsuarioDTO>();
        resultDTO.objects = new ArrayList<>();
        try {
            result = usuarioDAO.getAll();
            for (Usuario usuario : result.objects) {
                resultDTO.objects.add(ToDTO(usuario));
            }
            if (result.correct) {
                return ResponseEntity.ok(resultDTO);
            } else {
                return ResponseEntity.badRequest().body(resultDTO);
            }
        } catch (Exception e) {
            resultDTO.correct = false;
            resultDTO.message = e.getLocalizedMessage();
            resultDTO.ex = e;
            return ResponseEntity.internalServerError().body(resultDTO);
        }
    }

    @GetMapping(params = "idUsuario")
    public ResponseEntity<Result<UsuarioDTO>> getByIdUsuario(
            @RequestParam("idUsuario") long idUsuario) {
        Result<Usuario> result = new Result<Usuario>();
        Result<UsuarioDTO> resultDTO = new Result<UsuarioDTO>();
        resultDTO.objects = new ArrayList<>();
        try {
            result = usuarioDAO.getByIdUsuario(idUsuario);
            resultDTO.object = ToDTO(result.object);
            resultDTO.correct= result.correct;
            resultDTO.message= result.message;
            
                    
            if (resultDTO.correct) {
                return ResponseEntity.ok(resultDTO);
            } else {
                return ResponseEntity.badRequest().body(resultDTO);
            }
        } catch (Exception e) {
            resultDTO.correct = false;
            resultDTO.message = e.getLocalizedMessage();
            resultDTO.ex = e;
            return ResponseEntity.internalServerError().body(resultDTO);
        }
    }

    @PostMapping
    public ResponseEntity<Result<UsuarioDTO>> add(@RequestBody Usuario usuario) {
        Result<Usuario> result = new Result<Usuario>();
        Result<UsuarioDTO> resultDTO = new Result<UsuarioDTO>();
        resultDTO.objects = new ArrayList<>();
        try {
            result = usuarioDAO.add(usuario);

            if (result.correct) {
                resultDTO.object = ToDTO(result.object);
                resultDTO.correct = true;
                resultDTO.message = result.message;
                return ResponseEntity.ok(resultDTO);
            } else {
                resultDTO.correct = false;
                resultDTO.message = result.message;
                return ResponseEntity.badRequest().body(resultDTO);
            }
        } catch (Exception e) {
            resultDTO.correct = false;
            resultDTO.message = e.getLocalizedMessage();
            resultDTO.ex = e;
            return ResponseEntity.internalServerError().body(resultDTO);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<UsuarioDTO>> update(@PathVariable int id, @RequestBody Usuario usuario) {
        Result<Usuario> result = new Result<Usuario>();
        Result<UsuarioDTO> resultDTO = new Result<UsuarioDTO>();
        resultDTO.objects = new ArrayList<>();
        try {
            usuario.setIdUsuario(id);
            result = usuarioDAO.update(usuario);

            if (result.correct) {
                resultDTO.object = ToDTO(result.object);
                resultDTO.correct = true;
                resultDTO.message = result.message;
                return ResponseEntity.ok(resultDTO);
            } else {
                resultDTO.correct = false;
                resultDTO.message = result.message;
                return ResponseEntity.badRequest().body(resultDTO);
            }
        } catch (Exception e) {
            resultDTO.correct = false;
            resultDTO.message = e.getLocalizedMessage();
            resultDTO.ex = e;
            return ResponseEntity.internalServerError().body(resultDTO);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<UsuarioDTO>> delete(@PathVariable int id) {
        Result<Usuario> result = new Result<Usuario>();
        Result<UsuarioDTO> resultDTO = new Result<UsuarioDTO>();
        resultDTO.objects = new ArrayList<>();
        try {
            result = usuarioDAO.delete(id);

            if (result.correct) {
                resultDTO.correct = true;
                resultDTO.message = result.message;
                return ResponseEntity.ok(resultDTO);
            } else {
                resultDTO.correct = false;
                resultDTO.message = result.message;
                return ResponseEntity.badRequest().body(resultDTO);
            }
        } catch (Exception e) {
            resultDTO.correct = false;
            resultDTO.message = e.getLocalizedMessage();
            resultDTO.ex = e;
            return ResponseEntity.internalServerError().body(resultDTO);
        }
    }

    public static UsuarioDTO ToDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        } else {
            return new UsuarioDTO(usuario.getIdUsuario(),
                    usuario.getNombre(),
                    usuario.getApellidoPaterno(),
                    usuario.getApellidoMaterno(),
                    usuario.getUsername(),
                    usuario.getEmail(),
                    usuario.getCelular(),
                    usuario.getTelefono(),
                    usuario.getStatus(),
                    usuario.getRol());

        }
    }
}
