// controller/ProductoController.java
package com.project.springboot.controller;

import com.project.springboot.model.Producto;
import com.project.springboot.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService service;
    private final MessageSource messageSource;

    @GetMapping
    public List<Producto> listarProductos() { // Cambiado a List<Producto>
        return service.listarProductos(); // Cambiado a List<Producto>
    }

    @PostMapping
    public Producto crear(@RequestBody Producto producto) {
        return service.guardar(producto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> buscarPorId(@PathVariable Long id) { // Cambiado a Long
        Optional<Producto> producto = service.buscarPorId(id); // Cambiado a Long
        return producto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, // Cambiado a Long
                                               @RequestBody Producto producto) {
        Optional<Producto> productoActualizado = service.actualizar(id, producto); // Cambiado a Long
        return productoActualizado.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) { // Cambiado a Long
        service.eliminar(id); // Cambiado a Long
        return ResponseEntity.noContent().build(); // Devuelve un 204 No Content
    }

    @GetMapping("/mensaje")
    public String mensaje(@RequestParam(name = "lang", defaultValue = "es") String lang) {
        Locale locale = Locale.forLanguageTag(lang);
        return messageSource.getMessage("mensaje.bienvenida", null, locale);
    }
}