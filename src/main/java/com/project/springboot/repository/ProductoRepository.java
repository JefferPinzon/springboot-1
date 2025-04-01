// repository/ProductoRepository.java
package com.project.springboot.repository;

import com.project.springboot.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
}