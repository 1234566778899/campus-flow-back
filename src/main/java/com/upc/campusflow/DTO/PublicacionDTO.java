package com.upc.campusflow.DTO;

// No longer need to import com.upc.campusflow.Model.GrupoForo in the DTO
// import com.upc.campusflow.Model.GrupoForo; // <--- REMOVE THIS IMPORT!

import com.fasterxml.jackson.annotation.JsonProperty; // Keep this if you use it for JSON field naming
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
// No other unrelated imports like java.util.List if not directly used in DTO

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicacionDTO {
    private Long IdPublicacion;
    private String Contenido;

    @NotNull(message = "La fecha de publicación es obligatoria")
    @PastOrPresent(message = "La fecha de publicación no puede ser posterior a la fecha actual.")
    private LocalDate Fecha;

    private String label;

    @NotNull(message = "El ID de GrupoForo es obligatorio.") // Ensures the ID is provided in requests
    @JsonProperty("idGrupoForo") // Optional: Use if your JSON request body has "idGrupoForo"
    private Long idGrupoForo; // <--- This is the crucial correction: it MUST be Long

    private boolean Estado;
}