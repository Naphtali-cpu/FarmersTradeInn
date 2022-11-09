package com.naph.startup.controllers;

import com.naph.startup.config.ApiResponse;
import com.naph.startup.dto.AddToCartDto;
import com.naph.startup.dto.CartDto;
import com.naph.startup.exceptions.AuthenticationFailException;
import com.naph.startup.exceptions.CartItemNotExistException;
import com.naph.startup.exceptions.ProductNotExistException;
import com.naph.startup.model.Product;
import com.naph.startup.model.User;
import com.naph.startup.service.AuthenticationService;
import com.naph.startup.service.CartService;
import com.naph.startup.service.CategoryService;
import com.naph.startup.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    CartService cartService;

    @Autowired
    ProductService productService;

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addToCart(@RequestBody AddToCartDto addToCartDto, @RequestParam("token") String token) throws AuthenticationFailException, ProductNotExistException, AuthenticationFailException, ProductNotExistException {
//        We authenticate the logged-in user's token
        authenticationService.authenticate(token);

//        Get user's token/data
        User user = authenticationService.getUser(token);

//        Find product to add and add the item using the service
        Product product = productService.getProductById(addToCartDto.getProductId());
        cartService.addToCart(addToCartDto, product, user);

//        Return add to cart response
        return new ResponseEntity<>(new ApiResponse(true, "Added Product to Cart successfully"), HttpStatus.CREATED);
    }

//    Get all items added to cart

    @GetMapping("/")
    public ResponseEntity<CartDto> getCartItems(
            @RequestParam("token") String token
    ) throws AuthenticationFailException {
        authenticationService.authenticate(token);

        User user = authenticationService.getUser(token);

        CartDto cartDto = cartService.listCartItems(user);

        return new ResponseEntity<CartDto>(cartDto, HttpStatus.OK);
    }

//    Delete items in cart

    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<ApiResponse> deleteCartItem(
            @PathVariable("cartItemId") int cartItemId, @RequestParam("token") String token
    ) throws AuthenticationFailException, CartItemNotExistException {

        authenticationService.authenticate(token);
        User user = authenticationService.getUser(token);

        cartService.deleteCartItem(cartItemId, user);
        return new ResponseEntity<ApiResponse>(new ApiResponse(true, "Item deleted successfully"), HttpStatus.OK);
    }


}
