package com.upc.campusflow.Repository;

import com.upc.campusflow.Model.Evento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    /**
     * Consulta: eventos por profesor
     */
    @Query("SELECT e FROM Evento e WHERE e.profesor.idProfesor = :idProfesor")
    List<Evento> findEventosByProfesorId(@Param("idProfesor") Long idProfesor);



    /**
     * Próximos 5 eventos presentes para un estudiante,
     * usando la propiedad exacta de la entidad (IdEstudiante).
     */

    @Query(
            "SELECT ev " +
                    "FROM Evento ev JOIN ev.estudiantes e " +
                    "WHERE e.idEstudiante = :idEstudiante " +
                    "AND ev.FechaInicio >= :fechaActual " +
                    "ORDER BY ev.FechaInicio ASC"
    )
    List<Evento> findNext5EventosByEstudiante(
            @Param("idEstudiante") Long idEstudiante,
            @Param("fechaActual") LocalDate fechaActual,
            Pageable pageable
    );

    /**
     * Top 3 eventos con mayor número de estudiantes inscritos.
     */
    @Query(
            "SELECT ev FROM Evento ev LEFT JOIN ev.estudiantes e " +
                    "WHERE ev.Estado = true " +
                    "GROUP BY ev " +
                    "ORDER BY COUNT(e) DESC"
    )
    List<Evento> findTop3EventosByParticipacion(Pageable pageable);

    /**
     * Nuevo método: Obtener eventos por ID de Carrera (a través de los profesores).
     */
    @Query("SELECT e FROM Evento e WHERE e.profesor.carrera.idCarrera = :idCarrera AND e.Estado = true AND e.FechaFin >= CURRENT_DATE")
    List<Evento> findByProfesorCarreraIdAndEstadoTrueAndUpcoming(@Param("idCarrera") Long idCarrera);

    /**
     * Nuevo método: Verificar si un estudiante ya está unido a un evento.
     */
    @Query("SELECT COUNT(e) > 0 FROM Evento e JOIN e.estudiantes es WHERE e.IdEvento = :idEvento AND es.idEstudiante = :idEstudiante")
    boolean existsByIdEventoAndEstudiantesIdEstudiante(@Param("idEvento") Long idEvento, @Param("idEstudiante") Long idEstudiante);
}