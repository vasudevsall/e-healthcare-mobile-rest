package com.ehealthcaremanagement.repositories;

import com.ehealthcaremanagement.models.repository.ProductModel;
import com.ehealthcaremanagement.models.repository.StockModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockRepository extends JpaRepository<StockModel, Long> {
    List<StockModel> findAllByProduct(ProductModel productModel);
    List<StockModel> findAllByProductOrderByExpireAsc(ProductModel productModel);
    List<StockModel> findAllByProductAndExpireBefore(ProductModel productModel, LocalDate expire);
    List<StockModel> findAllByProductAndQuantityLessThanEqual(ProductModel productModel, long quantity);
    List<StockModel> findAllByProductAndExpireBeforeAndQuantityLessThanEqual(
            ProductModel productModel, LocalDate expire, long quantity
    );
    List<StockModel> findALlByQuantityLessThanEqual(long quantity);
    List<StockModel> findAllByExpireBefore(LocalDate expire);
    List<StockModel> findAllByExpireBeforeAndQuantityLessThanEqual(LocalDate expire, long quantity);
}
