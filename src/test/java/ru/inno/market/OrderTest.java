package ru.inno.market;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.inno.market.core.Catalog;
import ru.inno.market.model.Client;
import ru.inno.market.model.Item;
import ru.inno.market.model.Order;

import static org.junit.jupiter.api.Assertions.*;

public class OrderTest {
    Order order;
    final int testOrderId = 1;
    final Client client = new Client(11, "Dima");
    Catalog catalog = new Catalog();


    @BeforeEach
    public void setUp() {
        order = new Order(testOrderId, client);
    }

    @DisplayName("заказ возвращает свой ид")
    @Test
    public void shouldGetId() {
        assertEquals(testOrderId, order.getId());
    }

    @DisplayName("заказ возаращает клиента")
    @Test
    public void shouldGetClient() {
        assertEquals(client, order.getClient());
    }

    @DisplayName("у нового заказа корзина пуста")
    @Test
    public void shouldHave0SizeCartForNewOrder() {
        assertEquals(0, order.getCartSize());
    }

    @DisplayName("заказ не принимает пустой товар")
    @Test
    public void shouldNotAddNullItem() {
        assertThrows(NullPointerException.class, () -> order.addItem(null));
    }

    @DisplayName("новый заказ не имеет признака примененной скидки")
    @Test
    public void shouldDiscountIsFalseForNewOrder() {
        assertFalse(order.isDiscountApplied());
    }

    @DisplayName("в заказ добавляется товар и можно получить количество товара")
    @Test
    public void shouldAddItemAndGetCountForItem() {
        Item item = catalog.getItemById(1);
        order.addItem(item);
        order.addItem(item);
        order.addItem(item);
        assertEquals(3, order.getCountForItem(item));

    }

    @DisplayName("в заказ добавляется товар и можно получить размер корзины")
    @Test
    public void shouldAddItemsAndGetCartSize() {
        order.addItem(catalog.getItemById(1));
        order.addItem(catalog.getItemById(2));
        assertEquals(2, order.getCartSize());

    }

    @DisplayName("можно получить итоговую сумму заказа")
    @Test
    public void shouldCalcTotalPrice() {
        Item item1 = catalog.getItemById(1);
        Item item2 = catalog.getItemById(2);
        order.addItem(item1);
        order.addItem(item1);
        order.addItem(item2);
        double total = 2 * item1.getPrice() + item2.getPrice();
        assertEquals(total, order.getTotalPrice());
    }

    @DisplayName("к заказу без скидки применима скидка")
    @Test
    public void shouldApplyDiscount() {
        Item item = catalog.getItemById(1);
        order.addItem(item);
        double total = order.getTotalPrice();
        order.applyDiscount(0.1);
        assertEquals(0.9 * total, order.getTotalPrice());
        assertTrue(order.isDiscountApplied());
    }
}
