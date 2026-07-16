package com.risosuit.DiegoGomezTagleGestionProductos.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class Result<T> {

    public boolean correct;
    public T object;
    public List<T> objects;
    public String message;
    @JsonIgnore
    public Exception ex;

}
