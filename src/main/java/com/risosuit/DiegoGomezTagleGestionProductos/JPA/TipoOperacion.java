package com.risosuit.DiegoGomezTagleGestionProductos.JPA;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;


@Entity
@Table(name = "TIPOOPERACION")
public class TipoOperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="idtipooperacion")
    private int idTipoOperacion;
    @Column(name = "nombre")
    private String nombre;

    public int getIdTipoOperacion() {
        return idTipoOperacion;
    }

    public void setIdTipoOperacion(int idTipoOperacion) {
        this.idTipoOperacion = idTipoOperacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
    
}
