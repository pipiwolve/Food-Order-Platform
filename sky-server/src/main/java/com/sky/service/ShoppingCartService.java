package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {


    void addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 战术购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     *   清空购物车
     */
    void cleanShoppingCart();
}
