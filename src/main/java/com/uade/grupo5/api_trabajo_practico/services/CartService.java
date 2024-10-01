package com.uade.grupo5.api_trabajo_practico.services;

import com.uade.grupo5.api_trabajo_practico.dto.ItemDTO;
import com.uade.grupo5.api_trabajo_practico.repositories.CartRepository;
import com.uade.grupo5.api_trabajo_practico.repositories.ProductRepository;
import com.uade.grupo5.api_trabajo_practico.repositories.entities.Buy;
import com.uade.grupo5.api_trabajo_practico.repositories.entities.Cart;
import com.uade.grupo5.api_trabajo_practico.repositories.entities.Item;
import com.uade.grupo5.api_trabajo_practico.repositories.entities.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BuyService buyService;

    public Cart createCart() {
        Cart cart = new Cart();
        return cart;
    }

    public Cart getCartById(Long cartId) throws Exception {
        return cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
    }

    @Transactional
    public Item addItemToCart(ItemDTO itemDTO, Long cartId) throws Exception {
        // Obtener el carrito por ID
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        // Obtener el producto por ID
        Product product = productRepository.findById(itemDTO.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Buscar el ítem en la lista de ítems del carrito
        Item existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst()
                .orElse(null);

        Item item;
        if (existingItem != null) {
            // Si ya existe, actualizamos la cantidad
            item = existingItem;
            item.setQuantity(item.getQuantity() + itemDTO.getQuantity());
        } else {
            // Si no existe, creamos un nuevo ítem
            item = new Item();
            item.setProduct(product);
            item.setQuantity(itemDTO.getQuantity());
            item.setCart(cart);

            // Agregamos el nuevo ítem al carrito
            cart.getItems().add(item);
        }

        // Guardar el carrito actualizado
        cartRepository.save(cart);

        return item;
    }

    public double getTotalPrice(Long cartId) throws Exception {
        Cart cart = getCartById(cartId);
        return cart.getTotalPrice();
    }

    @Transactional
    public void removeItemFromCart(Long cartId, Long productId) throws Exception {
        Cart cart = getCartById(cartId);
        cart.getItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    public void emptyCart(Long cartId) throws Exception {
        Cart cart = getCartById(cartId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    public void removeCart(Long cartId) throws Exception {
        cartRepository.deleteById(cartId);
    }

    public Buy checkout(Long cartId) throws Exception {
        Cart cart = getCartById(cartId);
        Buy buy = buyService.createBuy(cart);
        emptyCart(cartId);
        return buy;
    }
}
