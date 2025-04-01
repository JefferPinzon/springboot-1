package com.project.springboot.controller;

import com.project.springboot.model.Producto;
import com.project.springboot.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest
@AutoConfigureWebTestClient
public class ProductoIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductoRepository productoRepository;

    private Producto testProducto;

    @BeforeEach
    public void setup() {
        // Limpiar la base de datos para empezar con un estado conocido
        productoRepository.deleteAll().block();

        // Crear un producto de prueba
        testProducto = Producto.builder()
                .nombre("Producto de Prueba")
                .precio(99.99)
                .build();
    }

    @Test
    @WithMockUser(username = "usuario", password = "clave123", roles = "USER")
    public void testCrearProducto() {
        webTestClient.post()
                .uri("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(testProducto), Producto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto producto = response.getResponseBody();
                    assert producto != null;
                    assert producto.getId() != null;
                    assert producto.getNombre().equals("Producto de Prueba");
                    assert producto.getPrecio() == 99.99;
                });
    }

    @Test
    @WithMockUser(username = "usuario", password = "clave123", roles = "USER")
    public void testObtenerProductoPorId() {
        // Primero creamos un producto para luego buscarlo
        Producto productoCreado = webTestClient.post()
                .uri("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(testProducto), Producto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .returnResult()
                .getResponseBody();

        assert productoCreado != null;

        // Ahora buscamos el producto por su ID
        webTestClient.get()
                .uri("/api/productos/{id}", productoCreado.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto producto = response.getResponseBody();
                    assert producto != null;
                    assert producto.getId().equals(productoCreado.getId());
                    assert producto.getNombre().equals("Producto de Prueba");
                    assert producto.getPrecio() == 99.99;
                });
    }

    @Test
    @WithMockUser(username = "usuario", password = "clave123", roles = "USER")
    public void testObtenerProductoInexistente() {
        webTestClient.get()
                .uri("/api/productos/999")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "usuario", password = "clave123", roles = "USER")
    public void testActualizarProducto() {
        // Primero creamos un producto
        Producto productoCreado = webTestClient.post()
                .uri("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(testProducto), Producto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .returnResult()
                .getResponseBody();

        assert productoCreado != null;

        // Modificamos el producto
        Producto productoModificado = Producto.builder()
                .id(productoCreado.getId())
                .nombre("Producto Actualizado")
                .precio(199.99)
                .build();

        // Actualizamos el producto
        webTestClient.put()
                .uri("/api/productos/{id}", productoCreado.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(productoModificado), Producto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .consumeWith(response -> {
                    Producto producto = response.getResponseBody();
                    assert producto != null;
                    assert producto.getId().equals(productoCreado.getId());
                    assert producto.getNombre().equals("Producto Actualizado");
                    assert producto.getPrecio() == 199.99;
                });
    }

    @Test
    @WithMockUser(username = "usuario", password = "clave123", roles = "USER")
    public void testEliminarProducto() {
        // Primero creamos un producto
        Producto productoCreado = webTestClient.post()
                .uri("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(testProducto), Producto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Producto.class)
                .returnResult()
                .getResponseBody();

        assert productoCreado != null;

        // Eliminamos el producto
        webTestClient.delete()
                .uri("/api/productos/{id}", productoCreado.getId())
                .exchange()
                .expectStatus().isOk();

        // Verificamos que el producto ha sido eliminado
        webTestClient.get()
                .uri("/api/productos/{id}", productoCreado.getId())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser(username = "usuario", password = "clave123", roles = "USER")
    public void testListarProductos() {
        // Creamos varios productos
        webTestClient.post()
                .uri("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(testProducto), Producto.class)
                .exchange()
                .expectStatus().isOk();

        Producto otroProducto = Producto.builder()
                .nombre("Otro Producto")
                .precio(50.0)
                .build();

        webTestClient.post()
                .uri("/api/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(otroProducto), Producto.class)
                .exchange()
                .expectStatus().isOk();

        // Verificamos que se listan todos los productos
        webTestClient.get()
                .uri("/api/productos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Producto.class)
                .hasSize(2);
    }

    @Test
    @WithMockUser(username = "usuario", password = "clave123", roles = "USER")
    public void testMensajeInternacionalizacion() {
        webTestClient.get()
                .uri("/api/productos/mensaje?lang=en")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Welcome to the product API!");

        webTestClient.get()
                .uri("/api/productos/mensaje?lang=fr")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Bienvenue dans l'API des produits !");
    }
}