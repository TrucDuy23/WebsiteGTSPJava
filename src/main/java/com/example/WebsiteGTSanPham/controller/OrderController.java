package com.example.WebsiteGTSanPham.controller;

import com.example.WebsiteGTSanPham.model.CartItem;
import com.example.WebsiteGTSanPham.model.Order;
import com.example.WebsiteGTSanPham.service.CartService;
import com.example.WebsiteGTSanPham.service.OrderService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
            @RequestParam String paymentMethod) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        Order order = orderService.createOrder(customerName, shippingAddress, phoneNumber, email, notes, paymentMethod, cartItems);
        return "redirect:/order/confirmation?orderId=" + order.getId();
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model, @RequestParam Long orderId) {
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            model.addAttribute("order", order);
            model.addAttribute("message", "Your order has been successfully placed.");
            return "/cart/order-confirmation";
        } else {
            model.addAttribute("message", "Order not found.");
            return "/cart/error";
        }
    }

    @GetMapping("/details")
    public String orderDetails(Model model, @RequestParam Long orderId, Authentication authentication) {
        Order order = orderService.getOrderById(orderId);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        if (order != null) {
            model.addAttribute("order", order);
            model.addAttribute("isAdmin", isAdmin);
            return "/cart/order-details";
        } else {
            model.addAttribute("message", "Order not found.");
            return "/cart/error";
        }
    }

    @GetMapping("/history")
    public String orderHistory(Model model, @RequestParam String customerName) {
        List<Order> orders = orderService.getOrdersByCustomer(customerName);
        model.addAttribute("orders", orders);
        return "cart/order-history";
    }
    @GetMapping("/historys")
    public String orderHistory(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "cart/historys";
    }

    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String status, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"));
        if (!isAdmin) {
            return "redirect:/403"; // Redirect to error page if not admin
        }
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/order/details?orderId=" + orderId;
    }
}
