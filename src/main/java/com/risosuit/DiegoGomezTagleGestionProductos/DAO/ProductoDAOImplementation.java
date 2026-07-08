package com.risosuit.DiegoGomezTagleGestionProductos.DAO;

import com.risosuit.DiegoGomezTagleGestionProductos.DTO.Result;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Producto;
import com.risosuit.DiegoGomezTagleGestionProductos.JPA.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProductoDAOImplementation implements IProducto {
    
    @Autowired
    private EntityManager entityManager;
    
    @Override
    public Result<Producto> getAll() {
        
        Result<Producto> result = new Result<Producto>();
        try {
            TypedQuery<Producto> query = entityManager.createQuery("FROM Producto p JOIN FETCH p.usuario", Producto.class);
            List<Producto> productos = query.getResultList();
            
            result.correct = true;
            result.message = "Productos obtenidos con exito";
            result.objects = new ArrayList<>(productos);
            
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }
    
    @Override
    public Result<Producto> getById(long idProducto) {
        Result<Producto> result = new Result<Producto>();
        try {
            TypedQuery<Producto> query = entityManager.createQuery("FROM Producto p JOIN FETCH p.usuario WHERE p.idProducto = :idProducto", Producto.class);
            query.setParameter("idProducto", idProducto);
            Producto producto = query.getSingleResult();
            
            result.correct = true;
            result.message = "Productos obtenidos con exito";
            result.object = producto;
            
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }
    
    @Override
    public Result<Producto> getByFolio(String folio) {
        Result<Producto> result = new Result<Producto>();
        try {
            TypedQuery<Producto> query = entityManager.createQuery("FROM Producto p JOIN FETCH p.usuario WHERE p.folio = :folio", Producto.class);
            query.setParameter("folio", folio);
            Producto producto = query.getSingleResult();
            
            result.correct = true;
            result.message = "Productos obtenidos con exito";
            result.object = producto;
            
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }
    
    @Override
    public Result<Producto> getByClave(String clave) {
        Result<Producto> result = new Result<Producto>();
        try {
            TypedQuery<Producto> query = entityManager.createQuery("FROM Producto p JOIN FETCH p.usuario WHERE p.clave = :clave", Producto.class);
            query.setParameter("idProducto", clave);
            Producto producto = query.getSingleResult();
            
            result.correct = true;
            result.message = "Productos obtenidos con exito";
            result.object = producto;
            
        } catch (Exception e) {
            result.correct = false;
            result.message = e.getLocalizedMessage();
            result.ex = e;
        }
        return result;
    }
    
    @Override
    @Transactional
    public Result<Producto> add(Producto producto) {
        Result<Producto> result = new Result<Producto>();
        try {
            if (producto == null) {
                result.correct = false;
                result.message = "El producto no puede ser nulo";
                return result;
            }
            if (producto.getUsuario() == null || producto.getUsuario().getIdUsuario() == 0) {
                result.correct = false;
                result.message = "El producto debe tener un usuario asignado con un ID válido";
                return result;
            }
            Usuario usuario = entityManager.find(Usuario.class, producto.getUsuario().getIdUsuario());
            if (usuario == null) {
                result.correct = false;
                result.message = "El usuario especificado no existe en la base de datos";
                return result;
            }
            producto.setUsuario(usuario);
            entityManager.persist(producto);
            result.correct = true;
            result.message = "Producto guardado con éxito";
            result.object = producto;
            
        } catch (jakarta.persistence.PersistenceException e) {
            result.correct = false;
            result.message = "Error de persistencia en la base de datos: " + e.getLocalizedMessage();
            result.ex = e;
        } catch (Exception e) {
            result.correct = false;
            result.message = "Error inesperado: " + e.getLocalizedMessage();
            result.ex = e;
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public Result<Producto> update(Producto producto) {
        Result<Producto> result = new Result<Producto>();
        try {
            if (producto == null) {
                result.correct = false;
                result.message = "El producto no puede ser nulo";
                return result;
            }
            
            Producto productoJPA = entityManager.find(Producto.class, producto.getIdProducto());
            if (productoJPA == null) {
                result.correct = false;
                result.message = "El producto con el ID especificado no existe en la base de datos";
                return result;
            }
            
            if (producto.getUsuario() == null || producto.getUsuario().getIdUsuario() == 0) {
                result.correct = false;
                result.message = "El producto debe tener un usuario asignado con un ID válido";
                return result;
            }
            
            Usuario usuario = entityManager.find(Usuario.class, producto.getUsuario().getIdUsuario());
            if (usuario == null) {
                result.correct = false;
                result.message = "El usuario especificado no existe en la base de datos";
                return result;
            }
            
            producto.setUsuario(usuario);
            
            Producto productoActualizado = entityManager.merge(producto);
            
            result.correct = true;
            result.message = "Producto actualizado con éxito"; // Mensaje más preciso para un update
            result.object = productoActualizado;
            
        } catch (jakarta.persistence.PersistenceException e) {
            result.correct = false;
            result.message = "Error de persistencia en la base de datos: " + e.getLocalizedMessage();
            result.ex = e;
        } catch (Exception e) {
            result.correct = false;
            result.message = "Error inesperado: " + e.getLocalizedMessage();
            result.ex = e;
        }
        
        return result;
    }
    
    @Override
    @Transactional
    public Result delete(long idProducto) {
        Result<Producto> result = new Result<Producto>();
        try {
            
            Producto productoJPA = entityManager.find(Producto.class, idProducto);
            if (productoJPA == null) {
                result.correct = false;
                result.message = "El producto con el ID especificado no existe en la base de datos";
                return result;
            }
            
            entityManager.remove(productoJPA);
            
            result.correct = true;
            result.message = "Producto actualizado con éxito"; // Mensaje más preciso para un update
            
        } catch (jakarta.persistence.PersistenceException e) {
            result.correct = false;
            result.message = "Error de persistencia en la base de datos: " + e.getLocalizedMessage();
            result.ex = e;
        } catch (Exception e) {
            result.correct = false;
            result.message = "Error inesperado: " + e.getLocalizedMessage();
            result.ex = e;
        }
        
        return result;
    }
    
}
