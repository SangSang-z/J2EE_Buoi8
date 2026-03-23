package com.example.bai4.controller;

import com.example.bai4.entity.Order;
import com.example.bai4.entity.Product;
import com.example.bai4.service.CartService;
import com.example.bai4.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final ProductService productService;

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getCartCount();
    }

    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getItems());
        model.addAttribute("total", cartService.getTotal());
        return "cart/list";
    }

    @PostMapping("/add/{id}")
    public String addToCart(@PathVariable Long id) {
        Product product = productService.getById(id);
        cartService.addToCart(product);
        return "redirect:/products";
    }

    @PostMapping("/update")
    public String updateCart(@RequestParam("productId") Long productId,
                             @RequestParam("quantity") int quantity) {
        cartService.updateQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeFromCart(@PathVariable Long id) {
        cartService.removeFromCart(id);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clear();
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Authentication authentication, Model model) {
        String loginName = authentication != null ? authentication.getName() : null;

        Order order = cartService.checkout(loginName);

        if (order == null) {
            return "redirect:/cart";
        }

        model.addAttribute("order", order);
        return "cart/success";
    }
}