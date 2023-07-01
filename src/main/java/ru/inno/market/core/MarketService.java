package ru.inno.market.core;

import ru.inno.market.model.*;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class MarketService {

    private int orderCounter;
    private Map<Integer, Order> orders;
    private final Catalog catalog = new Catalog();

    public MarketService() {
        orderCounter = 0;
        orders = new HashMap<>();
    }

    public Item getCatalogItemById(int id) {
        return catalog.getItemById(id);
    }

    public int getCatalogCountForItem(Item item) {
        return catalog.getCountForItem(item);
    }

    public int createOrderFor(Client client) {
        int id = orderCounter++;
        Order order = new Order(id, client);
        orders.put(id, order);

        return order.getId();
    }

    public void addItemToOrder(Item item, int orderId) {
        try {
            catalog.takeItemById(item.getId());
            orders.get(orderId).addItem(item);
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

    }

    public void addItemToOrder(int itemId, int orderId) {
        try {
            catalog.takeItemById(itemId);
            orders.get(orderId).addItem(catalog.getItemById(itemId));
        } catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
        }

    }

    public double applyDiscountForOrder(int orderId, PromoCodes codes) {
        Order order = orders.get(orderId);
        order.applyDiscount(codes.getDiscount());
        return order.getTotalPrice();
    }

    public Order getOrderInfo(int id) {
        return orders.get(id);
    }

    public int getCartSize(int orderId){
        return orders.get(orderId).getCartSize();
    }

    public int getOrderCountForItem(int orderId, Item item){
        return orders.get(orderId).getCountForItem(item);
    }
}

