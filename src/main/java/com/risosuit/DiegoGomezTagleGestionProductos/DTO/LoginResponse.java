/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.risosuit.DiegoGomezTagleGestionProductos.DTO;

public class LoginResponse {

    private String token;
    private String username;
    private long idUsuario;

    public LoginResponse(String token, String username, long idUsuario) {
        this.token = token;
        this.username = username;
        this.idUsuario = idUsuario;
    }
    
    

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }
    

    public LoginResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
