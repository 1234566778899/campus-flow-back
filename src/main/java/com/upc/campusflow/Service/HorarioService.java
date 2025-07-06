package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.HorarioDTO;
import com.upc.campusflow.Model.Horario;
import com.upc.campusflow.Model.Horario;
import com.upc.campusflow.Repository.HorarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HorarioService {
    final HorarioRepository horarioRepository;

    public HorarioService(HorarioRepository horarioRepository) {
        this.horarioRepository = horarioRepository;
    }

    //Listar
    public List<HorarioDTO> listar(){
        List<Horario> horarios = horarioRepository.findAll().stream().filter(Horario::isEstado).toList();;
        List<HorarioDTO> usuarioDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        for(Horario usuario : horarios){
            HorarioDTO usuarioDTO = modelMapper.map(usuario, HorarioDTO.class);
            usuarioDTOS.add(usuarioDTO);
        }
        return usuarioDTOS;
    }

    //Guardar
    public HorarioDTO guardar(HorarioDTO usuarioDTO){
        ModelMapper modelMapper = new ModelMapper();
        Horario usuario = modelMapper.map(usuarioDTO, Horario.class);
        usuario = horarioRepository.save(usuario);
        usuarioDTO = modelMapper.map(usuario, HorarioDTO.class);
        return usuarioDTO;
    }

    // Modificar
    public HorarioDTO modificar (Long id, HorarioDTO horarioDTO){ // Changed parameter name from usuarioDTO
        ModelMapper modelMapper = new ModelMapper();
        // 1. Fetch the existing entity. Make sure to use RecursoNoEncontradoException if you have it.
        Horario exits = horarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Horario no encontrado con ID: " + id)); // Changed error message from "Usuario" to "Horario"

        // 2. Store the original ID of the entity.
        //    This is crucial because ModelMapper might try to overwrite it if HorarioDTO has an 'idHorario' field.
        Long originalId = exits.getIdHorario(); // Assuming your Horario entity has a method getIdHorario() for its primary key.

        // 3. Map DTO data to the existing entity.
        modelMapper.map(horarioDTO, exits);

        // 4. Explicitly set the original ID back to the entity.
        //    This prevents Hibernate from thinking the identifier has been altered.
        exits.setIdHorario(originalId);

        // 5. Save the updated entity. Hibernate will recognize it's an update because 'exits' has an ID.
        Horario actualize = horarioRepository.save(exits);

        // 6. Map the updated entity back to DTO for the response.
        HorarioDTO dto = modelMapper.map(actualize, HorarioDTO.class);
        return dto;
    }

    //Eliminar
    public HorarioDTO eliminar (Long id){
        ModelMapper modelMapper = new ModelMapper();
        Horario entidad = horarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        entidad.setEstado(false);
        entidad = horarioRepository.save(entidad);
        HorarioDTO dto = modelMapper.map(entidad, HorarioDTO.class);
        return dto;
    }

}
