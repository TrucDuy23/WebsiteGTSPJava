package com.example.WebsiteGTSanPham.repository;

import com.example.WebsiteGTSanPham.model.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}