package com.upc.campusflow.DTO;

import com.upc.campusflow.Model.Estudiante;
import com.upc.campusflow.Model.Profesor;
import com.upc.campusflow.Model.Rol;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDTO {
    private Long idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String username;
    private String password;
    private Long rolId;
    private boolean Estado;
}
