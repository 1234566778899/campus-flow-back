package com.upc.campusflow.DTO;

import com.upc.campusflow.Model.Carrera;
import com.upc.campusflow.Model.EstudianteEstadistica;
import com.upc.campusflow.Model.Evento;
import com.upc.campusflow.Model.Usuario;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstudianteDTO {
    private long IdEstudiante;
    private int Ciclo;
    private Long idCarrera;
    private Long idUsuario;
    private boolean Estado;

}
