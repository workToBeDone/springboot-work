package com.techlearning.controller;

import com.techlearning.domains.Order;
import com.techlearning.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository repository;

    public OrderController(OrderRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/allOrders")
    public List<Order> findAll() {
        return repository.findAll();
    }

    @PostMapping("/createOrder")
    public ResponseEntity<Order> create(@RequestBody Order order) {
        return new ResponseEntity<>(repository.save(order), org.springframework.http.HttpStatus.CREATED);
    }
}