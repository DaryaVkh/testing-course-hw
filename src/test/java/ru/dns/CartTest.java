package ru.dns;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartTest {
    WebDriver driver;

    @BeforeAll
    public static void setSystem() {
        System.setProperty("webdriver.http.factory", "jdk-http-client");
    }

    @BeforeEach
    public void setUp() {
        WebDriverManager.chromedriver().browserVersion("112").setup();
        driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1400, 1000));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(15));
    }

    /**
     * Тест на добавление товара в корзину
     */
    @Test
    public void testAddToCart() {
        // Заходим на карточку товара
        driver.get("https://www.dns-shop.ru/product/5429da9af387ed20/61-smartfon-apple-iphone-13-128-gb-cernyj/");

        // Нажимаем на кнопку "Купить"
        driver.findElement(By.xpath("//div[8]/div/button[2]")).click();

        // Ждём, пока появится иконка, означающая, что в корзине есть товары
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-link-counter__badge")));

        // Переходим в корзину
        driver.get("https://www.dns-shop.ru/cart/");

        // Проверки
        WebElement cartBadge = driver.findElement(By.xpath("//span[contains(@class, 'cart-link-counter__badge')]"));
        String badgeText = cartBadge.getText();
        String message = String.format("Неверное количество товаров в корзине. Ожидалось: %s, получили: %s", "1", badgeText);
        Assertions.assertEquals("1", badgeText, message);
    }

    /**
     * Тест на удаление товара из корзины
     */
    @Test
    public void testRemoveFromCart() {
        // Заходим на карточку товара
        driver.get("https://www.dns-shop.ru/product/5429da9af387ed20/61-smartfon-apple-iphone-13-128-gb-cernyj/");

        // Нажимаем на кнопку "Купить"
        driver.findElement(By.xpath("//div[8]/div/button[2]")).click();

        // Ждём, пока появится иконка, означающая, что в корзине есть товары
        WebDriverWait waitForBadgeExist = new WebDriverWait(driver, Duration.ofSeconds(15));
        waitForBadgeExist.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-link-counter__badge")));

        // Переходим в корзину
        driver.get("https://www.dns-shop.ru/cart/");

        try {
            // Проверка, что в корзине есть один товар
            String cartProductsCountText = driver.findElement(By.xpath("//div[contains(@class, 'cart-products-count')]")).getText();
            String message = String.format("В корзине находится не 1 товар. Ожидалось %s, получили %s", "1 товар", cartProductsCountText);
            Assertions.assertEquals("1 товар", cartProductsCountText, message);
        } catch (NoSuchElementException e) {
            Assertions.fail("В корзине нет товаров.");
            return;
        }

        // Удаляем товар из корзины
        driver.findElement(By.xpath("//button[contains(@class, 'remove-button')]")).click();

        // Ждём, пока исчезнет иконка, означающая, что в корзине есть товары
        WebDriverWait waitForBadgeDisappear = new WebDriverWait(driver, Duration.ofSeconds(15));
        waitForBadgeDisappear.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".cart-link-counter__badge")));

        WebElement cartEmptyEl = null;

        try {
            // Проверяем, что на странице есть елемент с надписью, говорящей о пустоте корзины
            cartEmptyEl = driver.findElement(By.xpath("//div[contains(@class, 'empty-message__title-empty-cart')]"));
        }
        catch (NoSuchElementException e)
        {
            Assertions.fail("Элемент не найден на странице");
        }

        // Проверки
        Assertions.assertNotNull(cartEmptyEl);
        String emptyCartText = cartEmptyEl.getText();
        String message = String.format("Сообщение в пустой корзине не соответствует ожидаемому. Ожидалось %s, получили %s", "Корзина пуста", emptyCartText);
        Assertions.assertEquals("Корзина пуста", emptyCartText, message);
    }

    @AfterEach
    public void close() {
        driver.quit();
    }
}
