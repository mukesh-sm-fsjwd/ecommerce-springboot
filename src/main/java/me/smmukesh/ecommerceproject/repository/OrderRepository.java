package me.smmukesh.ecommerceproject.repository;

import me.smmukesh.ecommerceproject.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
}
