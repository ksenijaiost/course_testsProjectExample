package org.example;

import junit.framework.Assert;
import org.example.entities.Bank;
import org.example.entities.User;
import org.example.entities.UserBuilder;
import org.example.service.CentralBank;
import org.example.service.TimeManager;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static junit.framework.Assert.assertEquals;

public class AppTest
{
    private User sasha = new UserBuilder("Sasha", "Ivanov", 100000).withAddress("Green Street").build();
    private TimeManager timeManager = new TimeManager(LocalDateTime.of(2022, 9, 1, 0, 0, 0));
    private Bank sber = new Bank("SberBank", 1, 2, 5, 5000, 10000, 2, -1000000, 1000, 999999999);
    private LocalDateTime dateFirst = LocalDateTime.of(2022, 9, 1, 0, 0, 0);

    public AppTest() throws Exception {
    }

    @Test
    public void checkUserAuthorization() throws Exception {
        User ivan = new UserBuilder("Ivan", "Petrov", 10000).withAddress("Green Street").withPassportId(123).build();
        Assert.assertTrue(ivan.verificationPersonalData());
        Assert.assertFalse(sasha.verificationPersonalData());
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
        sber.addDepositCard(dateFirst, LocalDateTime.of(2022, 9, 2, 0, 0, 0), 15000, sasha.getUserId());
        timeManager.addObserver(sber);
        timeManager.addMonth();
        assertEquals(37500, sber.getListDepositCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testCheckTransferAndCancellationOfTransaction() throws Exception {
        User ivan = new UserBuilder("Ivan", "Petrov", 10000).withAddress("Green Street").withPassportId(123).build();
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addUser(ivan);
        sber.addDebitCard(dateFirst, 50000, sasha.getUserId());
        sber.addDebitCard(dateFirst, 50000, ivan.getUserId());
        timeManager.addObserver(sber);
        centralBank.transferMoney(25000, sber.getListDebitCards().get(0).getCardId(), sber.getListDebitCards().get(1).getCardId());
        assertEquals(75000, sber.getListDebitCards().get(1).getBalance(), 0.001);
        assertEquals(25000, sber.getListDebitCards().get(0).getBalance(), 0.001);
        centralBank.transactionCancellation(sber.getListDebitCards().get(0).getCardId(), 0);
        assertEquals(50000, sber.getListDebitCards().get(1).getBalance(), 0.001);
        assertEquals(50000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }
}
