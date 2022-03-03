package io.github.danielzyla.pdcaApp.service;

import io.github.danielzyla.pdcaApp.model.Role;
import io.github.danielzyla.pdcaApp.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public boolean isRole(String roleName) {
        return roleRepository.findByName(roleName).isPresent();
    }
}
