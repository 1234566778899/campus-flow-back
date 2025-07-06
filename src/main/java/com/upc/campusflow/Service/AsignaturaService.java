package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.AsignaturaDTO;
import com.upc.campusflow.DTO.NotaDTO;
import com.upc.campusflow.Model.Asignatura;
import com.upc.campusflow.Model.Carrera;
import com.upc.campusflow.Model.Horario;
import com.upc.campusflow.Model.Nota;
import com.upc.campusflow.Repository.AsignaturaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AsignaturaService {
    final AsignaturaRepository asignaturaRepository;
//RELACION CON CARRERA Y HORARIO
    public AsignaturaService(AsignaturaRepository asignaturaRepository) {
        this.asignaturaRepository = asignaturaRepository;
    }
    //listar
    public List<AsignaturaDTO> Listar(){
        List<Asignatura> asignaturas = asignaturaRepository.findAll()
                .stream()
                .filter(Asignatura::isEstado) // Filtrar solo los activos
                .toList();
        List<AsignaturaDTO> asignaturaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Asignatura asignatura : asignaturas) {
            AsignaturaDTO asignaturaDTO = modelMapper.map(asignatura, AsignaturaDTO.class);
            if(asignatura.getCarrera() != null ){
                asignaturaDTO.setId_carrera(asignatura.getCarrera().getIdCarrera());
            }
            asignaturaDTOS.add(asignaturaDTO);
        }
        return asignaturaDTOS;
    }
    //guardar

    public AsignaturaDTO guardar(AsignaturaDTO asignaturaDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Asignatura asignatura = modelMapper.map(asignaturaDTO, Asignatura.class);
        if (asignaturaDTO.getId_carrera() != null) {
            Carrera carrera = new Carrera();
            Horario horario = new Horario();
            carrera.setIdCarrera(asignaturaDTO.getId_carrera());
            asignatura.setCarrera(carrera);
        }
        asignatura = asignaturaRepository.save(asignatura);
        asignaturaDTO = modelMapper.map(asignatura, AsignaturaDTO.class);
        return asignaturaDTO;
    }
    //Modificar
    public AsignaturaDTO modificar(Long id, AsignaturaDTO asignaturaDTO) {
        ModelMapper modelMapper = new ModelMapper();
        Asignatura existente = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada con ID: " + id));
        modelMapper.map(asignaturaDTO, existente); // Sobrescribe los datos existentes

        if (asignaturaDTO.getId_carrera() != null) {
            Carrera carrera = new Carrera();
            Horario horario = new Horario();
            carrera.setIdCarrera(asignaturaDTO.getId_carrera());
            existente.setCarrera(carrera);
        }

        Asignatura actualizado = asignaturaRepository.save(existente);
        AsignaturaDTO dto = modelMapper.map(actualizado, AsignaturaDTO.class);
        return dto;
    }

    //Eliminar lógico
    public AsignaturaDTO eliminar(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Asignatura entidad = asignaturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignatura no encontrada con ID: " + id));
        entidad.setEstado(false);
        entidad =  asignaturaRepository.save(entidad);
        AsignaturaDTO dto = modelMapper.map(entidad, AsignaturaDTO.class);
        return  dto;
    }

    public List<AsignaturaDTO> obtenerTop3AsignaturasConMayorPromedio() {
        List<Asignatura> top3Asignaturas = asignaturaRepository.obtenerTop3AsignaturasPorPromedio();
        List<AsignaturaDTO> asignaturaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        // Convertir las asignaturas a DTOs
        for (Asignatura asignatura : top3Asignaturas) {
            AsignaturaDTO asignaturaDTO = modelMapper.map(asignatura, AsignaturaDTO.class);

            // Asegurarse de que la asignatura tenga carrera y horario, y asignar sus IDs
            if (asignatura.getCarrera() != null ) {
                asignaturaDTO.setId_carrera(asignatura.getCarrera().getIdCarrera());
            }

            asignaturaDTOS.add(asignaturaDTO);
        }

        return asignaturaDTOS;
    }

    //Obtener asignaturas por ciclo académico y ID de carrera.
    public List<AsignaturaDTO> obtenerAsignaturasPorCicloYCarrera(int cicloAcademico, Long idCarrera) {
        List<Asignatura> asignaturas = asignaturaRepository.findByCicloAcademicoAndCarreraId(cicloAcademico, idCarrera);
        List<AsignaturaDTO> asignaturaDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        for (Asignatura asignatura : asignaturas) {
            AsignaturaDTO asignaturaDTO = modelMapper.map(asignatura, AsignaturaDTO.class);
            if (asignatura.getCarrera() != null) {
                asignaturaDTO.setId_carrera(asignatura.getCarrera().getIdCarrera());
            }
            asignaturaDTOS.add(asignaturaDTO);
        }
        return asignaturaDTOS;
    }
}
