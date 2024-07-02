package com.example.WebsiteGTSanPham.service;

import com.example.WebsiteGTSanPham.model.CartItem;
import com.example.WebsiteGTSanPham.model.Product;
import com.example.WebsiteGTSanPham.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();
    @Autowired
    private ProductRepository productRepository;
    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        // Check if the product already exists in the cart
        Optional<CartItem> existingItemOptional = cartItems.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItemOptional.isPresent()) {
            // Product already exists in the cart, update the quantity
            CartItem existingItem = existingItemOptional.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            // Product not found in the cart, add a new item
            cartItems.add(new CartItem(product, quantity));
        }
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }
    public void clearCart() {
        cartItems.clear();
    }
}