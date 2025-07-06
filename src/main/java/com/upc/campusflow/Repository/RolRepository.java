package com.upc.campusflow.Repository;

import com.upc.campusflow.Model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional; // Importa Optional

public interface RolRepository extends JpaRepository<Rol, Long> {
    // Método existente que devuelve una lista (para compatibilidad con otros servicios)
    List<Rol> findByNombre(String nombre);

    // Nuevo método que devuelve Optional<Rol>, usando la propiedad 'nombre' de la entidad Rol.
    // 'findFirstByNombre' es una convención estándar de Spring Data JPA para obtener el primer resultado
    // envuelto en un Optional.
    Optional<Rol> findFirstByNombre(String nombre); // <-- ¡CAMBIO CRÍTICO AQUÍ!
}
