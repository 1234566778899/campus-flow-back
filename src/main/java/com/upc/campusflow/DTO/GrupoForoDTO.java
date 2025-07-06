package com.upc.campusflow.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrupoForoDTO {
    private Long idGrupoForo;
    private String Titulo;
    private String Descripcion;
    private String Campo;
    private Date FechaCreacion;
    private Long id_Asigneatura;
    private boolean Estado;

}
