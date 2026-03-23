package com.example.bai4.service;

import com.example.bai4.dto.CartItem;
import com.example.bai4.entity.Account;
import com.example.bai4.entity.Order;
import com.example.bai4.entity.OrderDetail;
import com.example.bai4.entity.Product;
import com.example.bai4.repo.AccountRepository;
import com.example.bai4.repo.OrderRepository;
import com.example.bai4.repo.ProductRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    private List<CartItem> items;

    @PostConstruct
    public void init() {
        items = new ArrayList<>();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void addToCart(Product product) {
        if (product == null) {
            return;
        }

        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
        } else {
            CartItem newItem = new CartItem();
            newItem.setId(product.getId());
            newItem.setName(product.getName());
            newItem.setImageName(product.getImageName());
            newItem.setPrice(product.getPrice());
            newItem.setQuantity(1);
            items.add(newItem);
        }
    }

    public void updateQuantity(Long productId, int quantity) {
        if (quantity < 1) {
            quantity = 1;
        }

        for (CartItem item : items) {
            if (item.getId().equals(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
    }

    public void removeFromCart(Long productId) {
        items.removeIf(item -> item.getId().equals(productId));
    }

    public void clear() {
        items.clear();
    }

    public double getTotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }

    public int getCartCount() {
        int count = 0;
        for (CartItem item : items) {
            count += item.getQuantity();
        }
        return count;
    }

    public Order checkout(String loginName) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setPaid(false);
        order.setTotalAmount(getTotal());

        if (loginName != null && !loginName.isBlank()) {
            Optional<Account> optionalAccount = accountRepository.findByLoginName(loginName);
            optionalAccount.ifPresent(order::setAccount);
        }

        List<OrderDetail> orderDetails = new ArrayList<>();

        for (CartItem item : items) {
            Product product = productRepository.findById(item.getId()).orElse(null);
            if (product == null) {
                continue;
            }

            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(product);
            detail.setPrice(item.getPrice());
            detail.setQuantity(item.getQuantity());

            orderDetails.add(detail);
        }

        order.setOrderDetails(orderDetails);

        Order savedOrder = orderRepository.save(order);

        clear();

        return savedOrder;
    }
}