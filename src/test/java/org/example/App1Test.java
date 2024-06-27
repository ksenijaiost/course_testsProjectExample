package org.example;

import org.example.entities.Bank;
import org.example.entities.User;
import org.example.entities.UserBuilder;
import org.example.exception.CentralBankException;
import org.example.service.CentralBank;
import org.example.service.TimeManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class App1Test {
    private User sasha;
    private TimeManager timeManager;
    private Bank sber;
    private LocalDateTime dateFirst;

    @BeforeEach
    public void setUp() throws Exception {
        sasha = new UserBuilder("Sasha", "Ivanov", 100000)
                .withAddress("Green Street").withPassportId(124).build();
        timeManager = new TimeManager(LocalDateTime
                .of(2022, 9, 1, 0, 0, 0));
        sber = new Bank(
                "SberBank",
                1,
                2,
                5,
                5000,
                10000,
                2,
                -1000000,
                1000,
                999999999);
        dateFirst = LocalDateTime.of(2022, 9, 1, 0, 0, 0);
    }

    @Test
    public void checkUserAuthorization() throws Exception {
        User ivan = new UserBuilder("Ivan", "Petrov", 10000)
                .withAddress("Green Street").withPassportId(123).build();
        assertTrue(ivan.verificationPersonalData());
        //assertFalse(sasha.verificationPersonalData());
    }

    @Test
    public void testCheckDebitCard() throws Exception {
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addDebitCard(dateFirst, 50000, sasha.getUserId());
        timeManager.addObserver(sber);
        timeManager.addMonth();
        assertEquals(80000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testCheckCreditCard() throws Exception {
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addCreditCard(dateFirst, 100, sasha.getUserId());
        timeManager.addMonth();
        assertEquals(0, sber.getListCreditCards().get(0).getUntrustedUserLimit(), 0.001);
        sber.getListCreditCards().get(0).withdrawMoney(2000);
        timeManager.addObserver(sber);
        timeManager.addMonth();
        assertEquals(-31900, sber.getListCreditCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testCheckDepositCard() throws Exception {
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addDepositCard(
                dateFirst,
                LocalDateTime.of(2022, 9, 2, 0, 0, 0),
                15000,
                sasha.getUserId());
        timeManager.addObserver(sber);
        timeManager.addMonth();
        assertEquals(37500, sber.getListDepositCards().get(0).getBalance(), 0.001);
    }

    @Test
    @Timeout(value = 1000000, unit = TimeUnit.NANOSECONDS)
    public void testCheckTransferAndCancellationOfTransaction() throws Exception {
        User ivan = new UserBuilder("Ivan", "Petrov", 10000)
                .withAddress("Green Street").withPassportId(123).build();
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addUser(ivan);
        sber.addDebitCard(dateFirst, 50000, sasha.getUserId());
        sber.addDebitCard(dateFirst, 50000, ivan.getUserId());
        timeManager.addObserver(sber);
        centralBank.transferMoney(
                25000,
                sber.getListDebitCards().get(0).getCardId(),
                sber.getListDebitCards().get(1).getCardId());
        assertEquals(75000, sber.getListDebitCards().get(1).getBalance(), 0.001);
        assertEquals(25000, sber.getListDebitCards().get(0).getBalance(), 0.001);
        centralBank.transactionCancellation(sber.getListDebitCards().get(0).getCardId(), 0);
        assertEquals(50000, sber.getListDebitCards().get(1).getBalance(), 0.001);
        assertEquals(50000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testAddNullBank() {
        CentralBank centralBank = new CentralBank();

        // Проверяем, что при попытке добавить null банк, будет выброшено исключение
        CentralBankException exception = assertThrows(CentralBankException.class,
                () -> centralBank.addBank(null));

        // Убеждаемся, что сообщение исключения соответствует ожидаемому
        assertEquals("Unable to add bank due to null object", exception.getMessage());
    }

    @Test
    public void testMoneyTransferAndTransactionCancellation() throws Exception {
        User ivan = new UserBuilder("Ivan", "Petrov", 10000)
                .withAddress("Green Street").withPassportId(123).build();
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addUser(ivan);
        sber.addDebitCard(dateFirst, 50000, sasha.getUserId());
        sber.addDebitCard(dateFirst, 50000, ivan.getUserId());

        // Добавляем клиенту деньги 50000 + 10000 = 60000
        sber.getListDebitCards().get(0).topUpCard(10000);
        assertEquals(60000, sber.getListDebitCards().get(0).getBalance(), 0.001);

        // Отнимаем у клиента деньги 60000 - 5000 = 55000
        sber.getListDebitCards().get(0).withdrawMoney(5000);
        assertEquals(55000, sber.getListDebitCards().get(0).getBalance(), 0.001);

        // Делаем перевод денег 55000 - 20000 = 35000 и 50000 + 20000 = 70000
        centralBank.transferMoney(
                20000,
                sber.getListDebitCards().get(0).getCardId(),
                sber.getListDebitCards().get(1).getCardId());
        assertEquals(70000, sber.getListDebitCards().get(1).getBalance(), 0.001);
        assertEquals(35000, sber.getListDebitCards().get(0).getBalance(), 0.001);

        // Отменяем нулевую транзакцию 35000 - 10000 = 25000
        centralBank.transactionCancellation(sber.getListDebitCards().get(0).getCardId(), 0);
        assertEquals(25000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }
}