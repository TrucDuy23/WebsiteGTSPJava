package com.example.WebsiteGTSanPham.controller;


import com.example.WebsiteGTSanPham.model.CartItem;
import com.example.WebsiteGTSanPham.service.CartService;
import com.example.WebsiteGTSanPham.service.OrderService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }
    @PostMapping("/submit")
    public String submitOrder(
            @RequestParam String customerName,
            @RequestParam String shippingAddress,
            @RequestParam String phoneNumber,
            @RequestParam String email,
            @RequestParam String notes,
            @RequestParam String paymentMethod
    ) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        orderService.createOrder(customerName, shippingAddress, phoneNumber, email, notes, paymentMethod, cartItems);
        return "redirect:/order/confirmation";
    }
    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}