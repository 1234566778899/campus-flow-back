package com.upc.campusflow.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecursoDTO {
    private Long idRecurso;
    private String tipoArchivo;
    private String url;
    @NotNull(message = "La fecha de subida es obligatoria") // Optional: Ensures it's not null
    @PastOrPresent(message = "La fecha de subida no puede ser posterior a la fecha actual.")
    private Date fechaSubida;
    private Long id_tarea;
    private Long id_publicacion;
    private boolean Estado;
}
