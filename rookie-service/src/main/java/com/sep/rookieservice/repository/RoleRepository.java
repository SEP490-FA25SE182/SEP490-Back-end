package com.sep.rookieservice.repository;

import com.sep.rookieservice.entity.Role;
import com.sep.rookieservice.enums.IsActived;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    // Dùng khi register / loginWithGoogle để lấy role "customer" còn ACTIVE
    Optional<Role> findByRoleNameIgnoreCaseAndIsActived(String roleName, IsActived isActived);

    // Dùng để kiểm tra role của user khi login (phòng trường hợp role bị đổi trạng thái)
    Optional<Role> findByRoleIdAndIsActived(String roleId, IsActived isActived);
}
