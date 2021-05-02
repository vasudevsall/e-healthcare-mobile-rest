package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.OrderModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    List<OrderModel> findAllByUser(UserModel userModel);
    List<OrderModel> findAllByUserAndDateAfter(UserModel userModel, LocalDateTime date);
    List<OrderModel> findAllByDateAfter(LocalDateTime date);
}
