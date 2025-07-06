package com.upc.campusflow.Repository;

import com.upc.campusflow.Model.Estudiante;
import com.upc.campusflow.Model.Evento;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante,Long> {
    //Estudiantes con al menos una nota menor a 11
    @Query("SELECT DISTINCT e FROM Estudiante e JOIN e.notas n " +
            "WHERE n.Puntaje < 11")
    List<Estudiante> findEstudiantesConNotaMenorA11();

    /**
     * Ordena estudiantes por promedio de todas sus notas (de mayor a menor)
     * y permite paginar para tomar, por ejemplo, el top N.
     */



    @Query("""
      SELECT e
      FROM Estudiante e
      LEFT JOIN e.notas n
      GROUP BY e.idEstudiante
      ORDER BY AVG(n.Puntaje) DESC
    """)
    List<Estudiante> findAllOrderByAverageNotaDesc(Pageable pageable);

    //Obtener todos los estudiantes que han participado en eventos entre dos fechas
    @Query("SELECT DISTINCT e " +
            "FROM Estudiante e " +
            "JOIN e.eventos ev " +
            "WHERE ev.FechaInicio " +
            "BETWEEN :fechaInicio " +
            "AND :fechaFin " +
            "AND ev.Estado = true")
    List<Estudiante> obtenerEstudiantesPorRangoDeFechas(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);


    List<Estudiante> findByUsuariosIdUsuario(Long idUsuario);
}


