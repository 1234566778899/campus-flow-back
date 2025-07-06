package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.GrupoForoDTO;
import com.upc.campusflow.Exception.DuplicateEntryException;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.Asignatura;
import com.upc.campusflow.Model.GrupoForo;
import com.upc.campusflow.Model.Nota;
import com.upc.campusflow.Repository.GrupoForoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GrupoForoService {

    final GrupoForoRepository grupoForoRepository;

    public GrupoForoService(GrupoForoRepository grupoForoRepository) {
        this.grupoForoRepository = grupoForoRepository;
    }

    //Listar
    public List<GrupoForoDTO> listar(){
        List<GrupoForo> grupoForos = grupoForoRepository.findAll().stream().filter(GrupoForo::isEstado).toList();
        List<GrupoForoDTO> grupoForoDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (GrupoForo grupoForo : grupoForos) {
            GrupoForoDTO grupoForoDTO = modelMapper.map(grupoForo, GrupoForoDTO.class);
            if(grupoForo.getAsignatura() != null){
                grupoForoDTO.setId_Asigneatura(grupoForo.getAsignatura().getIdAsignatura());
            }
            grupoForoDTOS.add(grupoForoDTO);
        }
        return grupoForoDTOS;
    }

    public GrupoForoDTO guardar(GrupoForoDTO grupoForoDTO){
        ModelMapper modelMapper = new ModelMapper();
        GrupoForo grupoForo = modelMapper.map(grupoForoDTO, GrupoForo.class);

        if(grupoForoDTO.getId_Asigneatura() != null){
            Asignatura asignatura = new Asignatura();
            asignatura.setIdAsignatura(grupoForoDTO.getId_Asigneatura());
            grupoForo.setAsignatura(asignatura);
        }

        try {
            grupoForo = grupoForoRepository.save(grupoForo);
        } catch (DataIntegrityViolationException ex) {
            // Captura la excepción de Spring por violación de integridad de datos.
            // Nos basamos en el mensaje de error de PostgreSQL que indica una llave duplicada.
            // La parte "Detail: Ya existe la llave (id_asigneatura)=(X)." es clave aquí.
            if (ex.getMessage() != null && ex.getMessage().contains("llave duplicada viola restricción de unicidad")) {
                throw new DuplicateEntryException("No se puede registrar. Ya existe un grupo foro para esta asignatura.");
            } else {
                // Si es otro tipo de DataIntegrityViolationException, la relanzamos.
                throw ex;
            }
        }

        grupoForoDTO = modelMapper.map(grupoForo, GrupoForoDTO.class);
        return grupoForoDTO;
    }



    // --- Modificar Grupo de Foro Existente ---
    public GrupoForoDTO modificar (Long id, GrupoForoDTO grupoForoDTO){
        ModelMapper modelMapper = new ModelMapper();
        // 1. Buscar la entidad existente por su ID
        GrupoForo exits = grupoForoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Foro no encontrado con ID: " + id));

        // 2. Guardar el ID original de la entidad
        //    Esto es CRUCIAL para evitar el error "identifier of an instance was altered".
        //    ModelMapper podría intentar sobrescribir el ID de 'exits' con el ID del DTO (si lo tiene).
        Long originalId = exits.getIdGrupoForo(); // Asegúrate que getIdGrupoForo() es el getter del ID de tu entidad

        // 3. Mapear los datos del DTO a la entidad existente
        modelMapper.map(grupoForoDTO, exits);

        // 4. Re-establecer el ID original en la entidad
        //    Esto garantiza que el ID de la entidad gestionada por Hibernate no cambie.
        exits.setIdGrupoForo(originalId);

        // 5. Actualizar la relación con Asignatura si el ID de asignatura está presente en el DTO
        if(grupoForoDTO.getId_Asigneatura() != null){
            Asignatura asignatura = new Asignatura();
            asignatura.setIdAsignatura(grupoForoDTO.getId_Asigneatura());
            exits.setAsignatura(asignatura);
        }

        try {
            // 6. Guardar la entidad modificada (Hibernate ahora sabe que es una actualización)
            GrupoForo actualizado = grupoForoRepository.save(exits);
            GrupoForoDTO dto =  modelMapper.map(actualizado, GrupoForoDTO.class);
            return dto;
        } catch (DataIntegrityViolationException ex) {
            // Manejar la excepción de duplicidad si la modificación causa un conflicto
            if (ex.getMessage() != null && ex.getMessage().contains("llave duplicada viola restricción de unicidad")) {
                throw new DuplicateEntryException("No se puede modificar. Ya existe un grupo foro con esa asignatura o se intenta duplicar.");
            } else {
                // Si es otro tipo de DataIntegrityViolationException, la relanzamos.
                throw ex;
            }
        }
    }

    //Eliminar
    public GrupoForoDTO eliminar (Long id){
        ModelMapper modelMapper = new ModelMapper();
        GrupoForo entidad = grupoForoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado con ID: " + id));
        entidad.setEstado(false);
        entidad =  grupoForoRepository.save(entidad);
        GrupoForoDTO dto =  modelMapper.map(entidad, GrupoForoDTO.class);
        return dto;
    }

}