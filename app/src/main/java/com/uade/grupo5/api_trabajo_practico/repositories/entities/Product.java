package com.uade.grupo5.api_trabajo_practico.repositories.entities;

import java.util.List;

import com.uade.grupo5.api_trabajo_practico.dto.ProductDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private double price;
    private List<String> images;
    private String additionalInfo;
    private int stock;
    private String category;
    private boolean featured;

    public ProductDTO toDTO() {
        return new ProductDTO(
                this.id,
                this.title,
                this.description,
                this.price,
                this.images,
                this.additionalInfo,
                this.stock,
                this.category,
                this.featured);
    }

}
