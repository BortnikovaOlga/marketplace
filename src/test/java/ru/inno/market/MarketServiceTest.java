package ru.inno.market;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.inno.market.core.MarketService;
import ru.inno.market.model.Client;
import ru.inno.market.model.Item;
import ru.inno.market.model.Order;
import ru.inno.market.model.PromoCodes;

import static org.junit.jupiter.api.Assertions.*;


public class MarketServiceTest {
    MarketService service;
    Client client = new Client(1, "Dima");

    @BeforeEach
    public void setUp() {
        service = new MarketService();
    }


    @DisplayName("Не должен создаваться заказ с пустым клиентом")
    @Test
    public void shouldNotCreateOrderWithNullClient() {
        assertThrows(NullPointerException.class, () -> service.createOrderFor(null));
    }


    @DisplayName("Создается заказ для клиента")
    @Test
    public void shouldCreateOrderForClient() {
        int orderId = service.createOrderFor(client);
        Order order = service.getOrderInfo(orderId);
        assertEquals(orderId, order.getId());
        assertEquals(client, order.getClient());
    }

    @DisplayName("Создаются два разных заказа для одного клиента")
    @Test
    public void shouldHaveNotEqualsOrderId() {
        assertNotEquals(service.createOrderFor(client),
                service.createOrderFor(client));
    }


    @DisplayName("Один вид товар добавляется в заказ")
    @Test
    public void shouldAddItemToOrder() {
        int orderId = service.createOrderFor(client);
        Item item = service.getCatalogItemById(1);
        service.addItemToOrder(item, orderId);
        assertEquals(1, service.getCartSize(orderId));  // количество пунктов в корзине
        assertEquals(1, service.getOrderCountForItem(orderId, item)); // количество товаров по ключу item
    }


    @DisplayName("В каталоге уменьшается количество доступного товара")
    @Test
    public void shouldDecrementCatalogCountForItem() {
        int orderId = service.createOrderFor(client);
        Item item = service.getCatalogItemById(1);
        int n = service.getCatalogCountForItem(item);
        service.addItemToOrder(item, orderId);
        assertEquals(n - 1, service.getCatalogCountForItem(item));
    }

    @DisplayName("Два товара добавляется в заказ")
    @Test
    public void shouldAddTwoItemsToOrder() {
        int orderId = service.createOrderFor(client);
        service.addItemToOrder(1, orderId);
        service.addItemToOrder(2, orderId);
        System.out.println(service.getOrderInfo(orderId));
        assertEquals(2, service.getCartSize(orderId));
    }

    @DisplayName("Когда товар закончился, товар не добавляется в корзину")
    @Test
    public void shouldNotThrowWhenItemIsOut() {
        int orderId = service.createOrderFor(client);
        Item item = service.getCatalogItemById(1);
        int n = service.getCatalogCountForItem(item);
        for (int i = n; i > 0; i--) {
            service.addItemToOrder(item, orderId);
        }
        assertDoesNotThrow(() -> service.addItemToOrder(item, orderId));
        assertEquals(0, service.getCatalogCountForItem(item));
        assertEquals(n, service.getOrderCountForItem(orderId, item));
    }

    @DisplayName("Не должен добавляться в заказ пустой товар")
    @Test
    public void shouldNotAddNullItemToOrder() {
        int orderId = service.createOrderFor(client);
        assertThrows(NullPointerException.class, () -> service.addItemToOrder(null, orderId));
    }

    @DisplayName("К заказу без скидки можно применить скидку")
    @Test
    public void shouldApplyDiscountForOrder() {
        int orderId = service.createOrderFor(client);
        service.addItemToOrder(1, orderId);
        double total = service.getOrderInfo(orderId).getTotalPrice();
        assertEquals(total * (1 - PromoCodes.FIRST_ORDER.getDiscount()),
                service.applyDiscountForOrder(orderId, PromoCodes.FIRST_ORDER));
    }

    @DisplayName("Нельзя сделать скидку на заказ со скидкой")
    @ParameterizedTest
    @EnumSource(PromoCodes.class)
    public void shouldNotApplyTwoDiscountsForOrder(PromoCodes promo) {
        int orderId = service.createOrderFor(client);
        service.addItemToOrder(1, orderId);
        double total = service.applyDiscountForOrder(orderId, PromoCodes.FIRST_ORDER);
        assertEquals(total, service.applyDiscountForOrder(orderId, promo));
    }
}
