package me.smmukesh.ecommerceproject.repository;

import me.smmukesh.ecommerceproject.model.AppRole;
import me.smmukesh.ecommerceproject.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
