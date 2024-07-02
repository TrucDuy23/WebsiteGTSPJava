package com.example.WebsiteGTSanPham.controller;

import com.example.WebsiteGTSanPham.config.Config;
import com.example.WebsiteGTSanPham.model.CartItem;
import com.example.WebsiteGTSanPham.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@RequestMapping("payments")
public class    PaymentController {
    @Autowired
    private CartService cartService;
    @GetMapping("/vnpay")
    public Object CreatePayment(HttpServletRequest req, HttpServletResponse resp /*,@RequestBody  */) throws IOException {
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long sum = 0;
        List<CartItem> cartItems = cartService.getCartItems();
        for (CartItem item : cartItems ){
            sum += item.getQuantity() * item.getProduct().getPrice();
        }
        Map<String, String> params = new HashMap<>();

        long amount = sum * 1000;
        String bankCode = params.get(" ");

        String vnp_TxnRef = Config.getRandomNumber(8);
        String vnp_IpAddr = Config.getIpAddress(req);
        String vnp_TmnCode = Config.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

        String locate = params.get("language");
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", Config.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Iterator<String> itr = fieldNames.iterator(); itr.hasNext(); ) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = Config.vnp_PayUrl + "?" + queryUrl;

        return "redirect:"+paymentUrl;
        //return Map.of("url", paymentUrl);
        /*PaymentrestDTO paymentrestDTO = new PaymentrestDTO();
        paymentrestDTO.setStatus("Ok");
        paymentrestDTO.setMessage("Successfully");
        paymentrestDTO.setURL(paymentUrl);
        return ResponseEntity.status(HttpStatus.OK).body(paymentrestDTO);*/
    }

    @GetMapping("/thanhtoan")
    public String paymentReturn(HttpServletRequest req, @RequestParam Map<String, String> allParams) {
        Map<String, String> vnp_Params = new HashMap<>();
        for (Map.Entry<String, String> param : allParams.entrySet()) {
            vnp_Params.put(param.getKey(), param.getValue());
        }

        String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash");
        vnp_Params.remove("vnp_SecureHashType");

        // Sort parameters by key
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append('=');
                try {
                    hashData.append(URLDecoder.decode(fieldValue, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                hashData.append('&');
            }
        }

        // Remove the trailing '&' character
        if (hashData.length() > 0) {
            hashData.setLength(hashData.length() - 1);
        }

        // Log the hash data for debugging
        System.out.println("Hash data: " + hashData.toString());

        String secureHash = Config.hmacSHA512(Config.secretKey, hashData.toString());

        // Log the generated secure hash for debugging
        System.out.println("Generated secure hash: " + secureHash);
        System.out.println("Received secure hash: " + vnp_SecureHash);

        if (!secureHash.equals(vnp_SecureHash)) {
            String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");
            if ("00".equals(vnp_ResponseCode)) {
                // Payment success
                req.setAttribute("message", "Payment Successful!");
            } else {
                // Payment failed
                req.setAttribute("message", "Payment Failed! Error code: " + vnp_ResponseCode);
            }
        } else {
            req.setAttribute("message", "Invalid signature!");
        }

        return "payments/success";
    }


}
