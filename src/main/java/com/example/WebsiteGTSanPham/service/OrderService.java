package com.example.WebsiteGTSanPham.service;


import com.example.WebsiteGTSanPham.model.CartItem;
import com.example.WebsiteGTSanPham.model.Order;
import com.example.WebsiteGTSanPham.model.OrderDetail;
import com.example.WebsiteGTSanPham.model.Product;
import com.example.WebsiteGTSanPham.repository.OrderDetailRepository;
import com.example.WebsiteGTSanPham.repository.OrderRepository;
import com.example.WebsiteGTSanPham.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService; // Assuming you have a CartService
    @Autowired
    private ProductRepository productRepository;
    @Transactional
    public Order createOrder(String customerName,String shippingAddress, String phoneNumber, String email, String notes, String paymentMethod, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("Đặt hàng thành công"); // Initial status
        order = orderRepository.save(order);
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.getProduct().getId()));
            if (product.getQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Requested quantity " + item.getQuantity() + " exceeds available stock: " + product.getQuantity());
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            orderDetailRepository.save(detail);
        }
// Optionally clear the cart after order placement
        cartService.clearCart();
        return order;
    }

}
