package com.risosuit.DiegoGomezTagleGestionProductos.RestController;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.LoginRequest;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.LoginResponse;
import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import com.risosuit.DiegoGomezTagleGestionProductos.Repository.UsuarioRepository;
import com.risosuit.DiegoGomezTagleGestionProductos.Services.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthRestController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UsuarioRepository UsuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(@RequestBody LoginRequest request) {

        Result<LoginResponse> result = new Result<>();

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            Usuario usuario = UsuarioRepository
                    .findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String token = jwtService.generateToken(
                    usuario.getUsername(),
                    usuario.getIdUsuario()
            );

            result.correct = true;
            result.message = "Usuario logueado";
            result.object = new LoginResponse(token, usuario.getUsername(), usuario.getIdUsuario());

            return ResponseEntity.ok(result);

        } catch (BadCredentialsException ex) {

            result.correct = false;
            result.message = "Usuario o password incorrectos";
            result.ex = ex;

            return ResponseEntity.status(401).body(result);
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(false);
        }

        String token = authorizationHeader.substring(7);
        try {
            String username = jwtService.extractUsername(token);
            boolean isValid = jwtService.isTokenValid(token, username);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {
            return ResponseEntity.ok(false);
        }
    }
}
