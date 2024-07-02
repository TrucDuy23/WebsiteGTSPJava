package com.example.WebsiteGTSanPham.model;

import jakarta.persistence.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private String description;
    @Min(0)
    private int quantity;
    private String imageProduct;

    private Boolean isDelete = false;
    private Boolean isActive = true;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

}
