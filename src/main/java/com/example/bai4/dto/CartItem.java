package com.example.bai4.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    private Long id;
    private String name;
    private String imageName;
    private double price;
    private int quantity;
}