package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {

    List<ProductModel> findAllByNameLike(String name);
}
