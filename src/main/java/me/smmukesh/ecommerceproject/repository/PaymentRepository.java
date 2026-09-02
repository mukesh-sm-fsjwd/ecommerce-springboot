package me.smmukesh.ecommerceproject.repository;

import me.smmukesh.ecommerceproject.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
