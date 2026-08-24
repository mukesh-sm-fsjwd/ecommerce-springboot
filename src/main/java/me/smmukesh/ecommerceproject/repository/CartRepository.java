package me.smmukesh.ecommerceproject.repository;

import me.smmukesh.ecommerceproject.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    @Query("SELECT c FROM Cart c WHERE c.user.email = ?1")
    Optional<Cart> findCartByEmail(String email);
}
