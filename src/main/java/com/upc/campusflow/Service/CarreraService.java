package com.upc.campusflow.Service;


import com.upc.campusflow.DTO.CarreraDTO;
import com.upc.campusflow.Exception.RecursoNoEncontradoException;
import com.upc.campusflow.Model.Carrera;
import com.upc.campusflow.Model.Usuario;
import com.upc.campusflow.Repository.CarreraRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarreraService {
    final CarreraRepository carreraRepository;

    public CarreraService(CarreraRepository carreraRepository) {
        this.carreraRepository = carreraRepository;
    }

    //Listar
    public List<CarreraDTO> listar(){
        List<Carrera> carreras = carreraRepository.findAll().stream().filter(Carrera::isEstado).toList();;
        List<CarreraDTO> carreraDTOS = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        for (Carrera carrera: carreras) {
            CarreraDTO carreraDTO = modelMapper.map(carrera, CarreraDTO.class);
            carreraDTOS.add(carreraDTO);
        }
        return carreraDTOS;
    }

    //Guardar
    public CarreraDTO guardar(CarreraDTO carreraDTO){
        ModelMapper modelMapper = new ModelMapper();
        Carrera carrera = modelMapper.map(carreraDTO, Carrera.class);
        carrera = carreraRepository.save(carrera);
        carreraDTO = modelMapper.map(carrera, CarreraDTO.class);
        return carreraDTO;
    }

    // --- Modificar Carrera Existente ---
    public CarreraDTO modificar (Long id , CarreraDTO carreraDTO) {
        // Instancia de ModelMapper dentro del método
        ModelMapper modelMapper = new ModelMapper();

        Carrera existingCarrera = carreraRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Carrera no encontrada con ID: " + id));

        // **Actualización Manual de Campos (para evitar problemas con el ID)**
        existingCarrera.setNombre(carreraDTO.getNombre());
        existingCarrera.setMalla_curricular(carreraDTO.getMalla_curricular());
        existingCarrera.setEstado(carreraDTO.isEstado());

        Carrera updatedCarrera = carreraRepository.save(existingCarrera);
        return modelMapper.map(updatedCarrera, CarreraDTO.class);
    }

    //Eliminar
    public CarreraDTO eliminar (Long id) {
        ModelMapper modelMapper = new ModelMapper();
        Carrera entidad = carreraRepository.findById(id).orElseThrow(() -> new RuntimeException("Carrera no encontrada con ID: " + id));
        entidad.setEstado(false);
        entidad = carreraRepository.save(entidad);
        CarreraDTO dto = modelMapper.map(entidad, CarreraDTO.class);
        return dto;
    }
}