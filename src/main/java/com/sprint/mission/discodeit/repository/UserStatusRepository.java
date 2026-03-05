package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserStatusRepository extends JpaRepository<UserStatus, UUID> {

  Optional<UserStatus> findByUser_Id(UUID userId);

  void deleteByUser_Id(UUID userId);


  //UserStatus save(UserStatus userStatus);
  //Optional<UserStatus> findById(UUID id);
  //List<UserStatus> findAll();
  //boolean existsById(UUID id);
  //void deleteById(UUID id);
}
