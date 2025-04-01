package com.project.springboot.service;

import com.project.springboot.model.Producto;
import com.project.springboot.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @InjectMocks
    private ProductoService service;

    private Producto producto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        producto = Producto.builder()
                .id("1")
                .nombre("Producto Test")
                .precio(100.0)
                .build();
    }

    @Test
    void listarProductos() {
        // Arrange
        when(repository.findAll()).thenReturn(Flux.just(producto));

        // Act
        Flux<Producto> resultado = service.listarproductos();

        // Assert
        StepVerifier.create(resultado)
                .expectNext(producto)
                .verifyComplete();

        verify(repository, times(1)).findAll();
    }

    @Test
    void buscarPorId() {
        // Arrange
        when(repository.findById("1")).thenReturn(Mono.just(producto));

        // Act
        Mono<Producto> resultado = service.buscarPorId("1");

        // Assert
        StepVerifier.create(resultado)
                .expectNext(producto)
                .verifyComplete();

        verify(repository, times(1)).findById("1");
    }

    @Test
    void buscarPorIdNoEncontrado() {
        // Arrange
        when(repository.findById("999")).thenReturn(Mono.empty());

        // Act
        Mono<Producto> resultado = service.buscarPorId("999");

        // Assert
        StepVerifier.create(resultado)
                .verifyComplete();

        verify(repository, times(1)).findById("999");
    }

    @Test
    void guardar() {
        // Arrange
        Producto productoNuevo = Producto.builder()
                .nombre("Producto Nuevo")
                .precio(200.0)
                .build();

        Producto productoGuardado = Producto.builder()
                .id("2")
                .nombre("Producto Nuevo")
                .precio(200.0)
                .build();

        when(repository.save(any(Producto.class))).thenReturn(Mono.just(productoGuardado));

        // Act
        Mono<Producto> resultado = service.guardar(productoNuevo);

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(p ->
                        p.getId().equals("2") &&
                                p.getNombre().equals("Producto Nuevo") &&
                                p.getPrecio() == 200.0)
                .verifyComplete();

        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    void eliminar() {
        // Arrange
        when(repository.deleteById(anyString())).thenReturn(Mono.empty());

        // Act
        Mono<Void> resultado = service.eliminar("1");

        // Assert
        StepVerifier.create(resultado)
                .verifyComplete();

        verify(repository, times(1)).deleteById("1");
    }

    @Test
    void actualizar() {
        // Arrange
        Producto productoActualizado = Producto.builder()
                .id("1")
                .nombre("Producto Actualizado")
                .precio(150.0)
                .build();

        when(repository.findById("1")).thenReturn(Mono.just(producto));
        when(repository.save(any(Producto.class))).thenReturn(Mono.just(productoActualizado));

        // Act
        Mono<Producto> resultado = service.actualizar("1", productoActualizado);

        // Assert
        StepVerifier.create(resultado)
                .expectNextMatches(p ->
                        p.getId().equals("1") &&
                                p.getNombre().equals("Producto Actualizado") &&
                                p.getPrecio() == 150.0)
                .verifyComplete();

        verify(repository, times(1)).findById("1");
        verify(repository, times(1)).save(any(Producto.class));
    }

    @Test
    void actualizarNoEncontrado() {
        // Arrange
        Producto productoActualizado = Producto.builder()
                .nombre("Producto Actualizado")
                .precio(150.0)
                .build();

        when(repository.findById("999")).thenReturn(Mono.empty());

        // Act
        Mono<Producto> resultado = service.actualizar("999", productoActualizado);

        // Assert
        StepVerifier.create(resultado)
                .verifyComplete();

        verify(repository, times(1)).findById("999");
        verify(repository, never()).save(any(Producto.class));
    }
}