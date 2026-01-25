package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.repository.RoleRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JCFRoleRepository implements RoleRepository {
    private static JCFRoleRepository instance = null;
    public static JCFRoleRepository getInstance(){
        if(instance == null){
            instance = new JCFRoleRepository();
        }
        return instance;
    }
    private JCFRoleRepository(){}

    private Set<Role> roles = new HashSet<>();

    @Override
    public void fileSave(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public Set<Role> fileLoadAll() {
        return roles;
    }

    @Override
    public Role fileLoad(UUID id) {
        return roles.stream()
                .filter(role -> role.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Role not found: id = " + id));
    }

    @Override
    public void fileDelete(UUID id) {
        roles.removeIf(role -> role.getId().equals(id));
    }
}
