package com.upc.campusflow.Repository;
import com.upc.campusflow.Model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    @Query("SELECT t FROM Tarea t WHERE t.estado = true AND t.estudiante.idEstudiante = :idEstudiante")
    List<Tarea> TareasActivasPorEstudiante(@Param("idEstudiante") Long idEstudiante);

    @Query("SELECT t FROM Tarea t WHERE t.prioridad = :prioridad ORDER BY t.fechaLimite ASC")
    List<Tarea> TareasPorPrioridad(@Param("prioridad") String prioridad);
}
