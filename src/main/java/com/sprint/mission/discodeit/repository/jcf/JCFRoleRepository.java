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
    public Set<Role> fileLoad() {
        return roles;
    }

    @Override
    public void fileDelete(UUID id) {
        roles.removeIf(role -> role.getId().equals(id));
    }
}
