package com.example.bai4.service;

import com.example.bai4.entity.Product;
import com.example.bai4.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> searchByName(String q, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(q, pageable);
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm"));
    }

    public Product save(Product p) {
        return productRepository.save(p);
    }

    public void delete(Product p) {
        productRepository.delete(p);
    }
}