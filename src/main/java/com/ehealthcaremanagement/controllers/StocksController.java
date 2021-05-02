package com.ehealthcaremanagement.controllers;

import com.ehealthcaremanagement.models.repository.OrderModel;
import com.ehealthcaremanagement.models.repository.ProductModel;
import com.ehealthcaremanagement.models.repository.StockModel;
import com.ehealthcaremanagement.models.repository.UserModel;
import com.ehealthcaremanagement.repositories.OrderRepository;
import com.ehealthcaremanagement.repositories.ProductRepository;
import com.ehealthcaremanagement.repositories.StockRepository;
import com.ehealthcaremanagement.services.FindModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "/stocks")
public class StocksController {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FindModel findModel;

    private final Logger logger = LoggerFactory.getLogger(StocksController.class);

    public static final String user = "ROLE_USER";
    public static final String manager = "ROLE_MANAGE";
    public static final String doctor = "ROLE_DOC";

    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<ProductModel> getProducts(
            @RequestParam(name = "name") Optional<String> name
    ) {
        if(name.isEmpty())
            return productRepository.findAll();
        return productRepository.findAllByNameLike(name.get());
    }

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody ProductModel addProduct(@RequestBody ProductModel productModel) {
        if(productModel.getUse() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tentative weekly usage should be greater than 0");
        productModel.setWeeks(0);
        try{
            return productRepository.save(productModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot add product");
        }
    }

    @RequestMapping(value = "/item", method = RequestMethod.GET)
    public @ResponseBody List<StockModel> getStocksModel(
        @RequestParam(name = "product") Optional<Long> product,
        @RequestParam(name = "expire") Optional<Long> days,
        @RequestParam(name = "quantity") Optional<Long> quantity
    ) {
        if(product.isPresent()) {
            ProductModel productModel = findProductModel(product.get());
            if(days.isEmpty()) {
                if(quantity.isEmpty())
                    return stockRepository.findAllByProduct(productModel);
                else
                    return stockRepository.findAllByProductAndQuantityLessThanEqual(productModel, quantity.get());
            }
            else {
                if(quantity.isEmpty())
                    return stockRepository.findAllByProductAndExpireBefore(productModel, LocalDate.now().plusDays(days.get()));
                else
                    return stockRepository.findAllByProductAndExpireBeforeAndQuantityLessThanEqual(
                            productModel, LocalDate.now().plusDays(days.get()), quantity.get()
                    );
            }
        }
        if(days.isEmpty()) {
            if(quantity.isEmpty())
                return stockRepository.findAll(); // all empty
            else
                return stockRepository.findALlByQuantityLessThanEqual(quantity.get()); // only quantity present
        }
        else {
            if(quantity.isEmpty())
                return stockRepository.findAllByExpireBefore(LocalDate.now().plusDays(days.get())); // only days present
            else
                return stockRepository.findAllByExpireBeforeAndQuantityLessThanEqual(
                        LocalDate.now().plusDays(days.get()), quantity.get()
                ); // days and quantity present
        }
    }

    @RequestMapping(value = "/item", method = RequestMethod.POST)
    public @ResponseBody StockModel addToStock(@RequestBody StockModel stockModel) {
        ProductModel productModel = findProductModel(stockModel.getProduct().getId());
        stockModel.setProduct(productModel);

        try {
            return stockRepository.save(stockModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot add to stock");
        }
    }

    @RequestMapping(value = "/order", method = RequestMethod.GET)
    public @ResponseBody List<OrderModel> getOrders(
            @RequestParam(name = "days") Optional<Long> days,
            Principal principal
    ) {
        UserModel userModel = findModel.findUserModel(principal.getName());
        if(userModel.getRoles().equals(manager)) {
            if(days.isEmpty())
                return orderRepository.findAll();
            orderRepository.findAllByDateAfter(LocalDateTime.now().minusDays(days.get()));
        } else if(userModel.getRoles().equals(doctor)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not allowed to access");
        }
        if(days.isEmpty())
            return orderRepository.findAllByUser(userModel);
        return orderRepository.findAllByUserAndDateAfter(userModel, LocalDateTime.now().minusDays(days.get()));
    }

    @RequestMapping(value = "/order", method = RequestMethod.POST)
    public @ResponseBody OrderModel placeOrder(@RequestBody OrderModel orderModel, Principal principal) {
        UserModel userModel = findModel.findUserModel(principal.getName());
        ProductModel productModel = findProductModel(orderModel.getStock().getId());
        List<StockModel> stockModels = stockRepository.findAllByProductOrderByExpireAsc(productModel);
        if(stockModels.size() <= 0)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item : " + productModel.getName() + " not in stock");
        StockModel stockModel = stockModels.get(0);

        if(stockModel.getQuantity() < orderModel.getQuantity())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not enough quantity in stock");
        if(orderModel.getQuantity() <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order quantity cannot be less than 0");

        double price = stockModel.getPrice() * orderModel.getQuantity();
        price = price - (stockModel.getDiscount() * price)/100.0;
        orderModel.setPrice(price);
        orderModel.setUser(userModel);
        orderModel.setStock(stockModel);

        try {
            stockModel.setQuantity(stockModel.getQuantity() - orderModel.getQuantity());
            logger.info("Stocks: " + stockModel.getId() + " quantity reduced by :" + orderModel.getQuantity());
            return orderRepository.save(orderModel);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot place your order, please try again later");
        }
    }

    @RequestMapping(value = "/order", method = RequestMethod.DELETE)
    public @ResponseBody OrderModel cancelOrder(@RequestParam(name = "id") long id, Principal principal) {
        UserModel userModel = findModel.findUserModel(principal.getName());
        OrderModel orderModel = findOrderModel(id);
        if(!orderModel.getUser().getUsername().equals(userModel.getUsername()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized to cancel this order");

        orderRepository.delete(orderModel);
        StockModel stockModel = orderModel.getStock();
        stockModel.setQuantity(stockModel.getQuantity() + orderModel.getQuantity());
        stockRepository.save(stockModel);
        return orderModel;
    }
    // Helper Methods
    private ProductModel findProductModel(long id) {
        Optional<ProductModel> productModelOptional = productRepository.findById(id);
        if(productModelOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with product id: " + id);
        return productModelOptional.get();
    }

    private StockModel findStockModel(long id) {
        Optional<StockModel> stockModelOptional = stockRepository.findById(id);
        if(stockModelOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found in stock: " + id);
        return stockModelOptional.get();
    }

    private OrderModel findOrderModel(long id) {
        Optional<OrderModel> orderModelOptional = orderRepository.findById(id);
        if(orderModelOptional.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id);
        return orderModelOptional.get();
    }
}
