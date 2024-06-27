package com.example.WebsiteGTSanPham.repository;

import com.example.WebsiteGTSanPham.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}