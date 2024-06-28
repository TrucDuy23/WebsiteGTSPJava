package com.example.WebsiteGTSanPham.repository;

import com.example.WebsiteGTSanPham.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}