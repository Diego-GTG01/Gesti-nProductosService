package com.risosuit.DiegoGomezTagleGestionProductos.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;

public class Result<T> {

    public boolean correct;
    public T object;
    public ArrayList<T> objects;
    public String message;
    @JsonIgnore
    public Exception ex;

}
