package com.example.WebsiteGTSanPham.repository;

import com.example.WebsiteGTSanPham.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}