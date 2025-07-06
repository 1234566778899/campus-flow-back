package com.upc.campusflow.Service;

import com.upc.campusflow.DTO.UsuarioDTO;
import com.upc.campusflow.Model.Rol;
import com.upc.campusflow.Model.Usuario;
import com.upc.campusflow.Repository.RolRepository;
import com.upc.campusflow.Repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolService {
    final RolRepository rolRepository;
    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    //Guardar
    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }


    public Rol buscarRolPorId(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
    }

    // Crear rol si no existe
    public Rol obtenerOCrearRol(String nombre) {
        List<Rol> roles = rolRepository.findByNombre(nombre);
        if (roles.isEmpty()) {
            Rol nuevoRol = new Rol();
            nuevoRol.setNombre(nombre);
            return rolRepository.save(nuevoRol);
        }
        return roles.get(0);
    }

}
