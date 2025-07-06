package com.upc.campusflow.Repository;

import com.upc.campusflow.Model.Nota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    // Consulta para las notas de un determinado Curso
    @Query("SELECT n FROM Nota n WHERE n.asignatura.idAsignatura = :idAsignatura AND n.Estado = true")
    List<Nota> findNotasByAsignaturaId(Long idAsignatura);

    //Consulta de notas de estudiantes en un intervalo
    @Query("SELECT n FROM Nota n WHERE n.estudiante.idEstudiante = :idEstudiante AND n.Puntaje >= :puntajeMinimo AND n.Puntaje <= :puntajeMaximo AND n.Estado = true")
    List<Nota> findByEstudianteIdAndPuntajeBetween(
            @Param("idEstudiante") Long idEstudiante,
            @Param("puntajeMinimo") double puntajeMinimo,
            @Param("puntajeMaximo") double puntajeMaximo
    );

    // ¡NUEVO MÉTODO CLAVE para obtener todas las notas de un estudiante!
    @Query("SELECT n FROM Nota n WHERE n.estudiante.idEstudiante = :idEstudiante AND n.Estado = true")
    List<Nota> findByEstudianteId(@Param("idEstudiante") Long idEstudiante);
}
