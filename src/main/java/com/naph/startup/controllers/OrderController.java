package com.naph.startup.controllers;

import com.naph.startup.config.ApiResponse;
import com.naph.startup.config.StripeResponse;
import com.naph.startup.dto.CheckoutItemDto;
import com.naph.startup.exceptions.AuthenticationFailException;
import com.naph.startup.exceptions.OrderNotFoundException;
import com.naph.startup.model.Order;
import com.naph.startup.model.User;
import com.naph.startup.service.AuthenticationService;
import com.naph.startup.service.OrderService;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeResponse> checkoutList(@RequestBody List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {

//      Create the stripe session
        Session session = orderService.createSession(checkoutItemDtoList);
        StripeResponse stripeResponse = new StripeResponse(session.getId());

//      Send the stripe session id in response
        return new ResponseEntity<StripeResponse>(stripeResponse, HttpStatus.OK);
    }

//  Add Order history

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> placeOrder(@RequestParam("token") String token, @RequestParam("sessionId") String sessionId) throws AuthenticationFailException {
        authenticationService.authenticate(token);

        User user = authenticationService.getUser(token);

        orderService.placeOrder(user, sessionId);
        return new ResponseEntity<>(new ApiResponse(true, "Order has been placed"), HttpStatus.CREATED);
    }

//   Fetch order history of the user
    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllOrders(@RequestParam("token") String token) throws AuthenticationFailException {
        // validate token
        authenticationService.authenticate(token);
        // retrieve user
        User user = authenticationService.getUser(token);
        // get orders
        List<Order> orderDtoList = orderService.listOrders(user);

        return new ResponseEntity<>(orderDtoList, HttpStatus.OK);
    }

//  Get specific order
    @GetMapping("/{id}")
        public ResponseEntity<Object> getOrderById(@PathVariable("id") Integer id, @RequestParam("token") String token)
                throws AuthenticationFailException, OrderNotFoundException {
            authenticationService.authenticate(token);
            User user = authenticationService.getUser(token);
            Order order = orderService.getOrder(id, user);

            return new ResponseEntity<>(order, HttpStatus.OK);

        }

    }
