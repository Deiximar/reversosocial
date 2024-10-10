package com.reversosocial.config.data;

import java.util.Optional;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.reversosocial.models.entity.EPermission;
import com.reversosocial.models.entity.ERole;
import com.reversosocial.models.entity.Permission;
import com.reversosocial.models.entity.Role;
import com.reversosocial.models.entity.Sector;
import com.reversosocial.repository.PermissionRepository;
import com.reversosocial.repository.RoleRepository;
import com.reversosocial.repository.SectorRepository;
import com.reversosocial.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class DataLoader {

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;

  @Bean
  CommandLineRunner initDatabaseRoles(UserRepository repository) {
    return args -> {
      Permission createPermission = permissionRepository.findByPermission(EPermission.CREATE)
          .orElseGet(() -> permissionRepository.save(Permission.builder().permission(EPermission.CREATE).build()));
      Permission readPermission = permissionRepository.findByPermission(EPermission.READ)
          .orElseGet(() -> permissionRepository.save(Permission.builder().permission(EPermission.READ).build()));
      Permission deletePermission = permissionRepository.findByPermission(EPermission.DELETE)
          .orElseGet(() -> permissionRepository.save(Permission.builder().permission(EPermission.DELETE).build()));
      Permission updatePermission = permissionRepository.findByPermission(EPermission.UPDATE)
          .orElseGet(() -> permissionRepository.save(Permission.builder().permission(EPermission.UPDATE).build()));
      Permission participatePermission = permissionRepository.findByPermission(EPermission.PARTICIPATE)
          .orElseGet(() -> permissionRepository.save(Permission.builder().permission(EPermission.PARTICIPATE).build()));

      Role adminRole = Role.builder()
          .role(ERole.ADMIN)
          .permissionList(Set.of(createPermission, readPermission, deletePermission, updatePermission))
          .build();

      Role femseniorAdminRole = Role.builder()
          .role(ERole.FEMSENIORADMIN)
          .permissionList(Set.of(createPermission, readPermission, deletePermission, updatePermission))
          .build();

      Role reversoAdminRole = Role.builder()
          .role(ERole.REVERSOADMIN)
          .permissionList(Set.of(createPermission, readPermission, deletePermission, updatePermission))
          .build();

      Role femseniorRole = Role.builder()
          .role(ERole.FEMSENIOR)
          .permissionList(
              Set.of(createPermission, readPermission, deletePermission, updatePermission, participatePermission))
          .build();

      Role userRole = Role.builder()
          .role(ERole.USER)
          .permissionList(Set.of(readPermission, participatePermission))
          .build();

      if (roleRepository.findByRole(ERole.ADMIN).isEmpty()) {
        roleRepository.save(adminRole);
      }

      if (roleRepository.findByRole(ERole.FEMSENIORADMIN).isEmpty()) {
        roleRepository.save(femseniorAdminRole);
      }

      if (roleRepository.findByRole(ERole.REVERSOADMIN).isEmpty()) {
        roleRepository.save(reversoAdminRole);
      }

      if (roleRepository.findByRole(ERole.USER).isEmpty()) {
        roleRepository.save(userRole);
      }

      if (roleRepository.findByRole(ERole.FEMSENIOR).isEmpty()) {
        roleRepository.save(femseniorRole);
      }
    };
  }

  @Bean
  CommandLineRunner initDatabaseSector(SectorRepository repository) {
    return args -> {
      insertSectorIfNotExists(repository, "Tecnología");
      insertSectorIfNotExists(repository, "Administración y Finanzas");
      insertSectorIfNotExists(repository, "Dirección y Ejecución");
      insertSectorIfNotExists(repository, "Psicología");
      insertSectorIfNotExists(repository, "Terapias Alternativas y Desarrollo Personal");
      insertSectorIfNotExists(repository, "Atención y Cuidados");
      insertSectorIfNotExists(repository, "Atención al Cliente y Servicios");
      insertSectorIfNotExists(repository, "Hostelería y Turismo");
      insertSectorIfNotExists(repository, "Artes y Creatividades");
      insertSectorIfNotExists(repository, "Servicios a la Comunidad");
    };
  }

  private void insertSectorIfNotExists(SectorRepository repository, String sectorName) {
    Optional<Sector> sector = repository.findBySector(sectorName);
    if (!sector.isPresent()) {
      Sector newSector = new Sector();
      newSector.setSector(sectorName);
      repository.save(newSector);
    }
  }

}
