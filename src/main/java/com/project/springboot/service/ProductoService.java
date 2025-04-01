// service/ProductoService.java
package com.project.springboot.service;

import com.project.springboot.model.Producto;
import com.project.springboot.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoService {
    private final ProductoRepository repository;

    public List<Producto> listarProductos() {
        return repository.findAll(); // Devuelve una lista de productos
    }

    public Producto guardar(Producto producto) {
        return repository.save(producto); // Devuelve el producto guardado
    }

    public Optional<Producto> buscarPorId(Long id) { // Cambiado a Long
        return repository.findById(id); // Devuelve un Optional<Producto>
    }

    public void eliminar(Long id) { // Cambiado a Long
        repository.deleteById(id); // No devuelve nada
    }

    public Optional<Producto> actualizar(Long id, Producto producto) { // Cambiado a Long
        return repository.findById(id)
                .map(p -> {
                    p.setNombre(producto.getNombre());
                    p.setPrecio(producto.getPrecio());
                    return repository.save(p);
                });
    }
}