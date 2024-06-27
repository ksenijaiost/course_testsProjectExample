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

public class AppTest {
    // создание пользователя банка
    private final User sasha = new UserBuilder("Sasha", "Ivanov", 100000)
            .withAddress("Green Street").build();
    // объявление и инициализация "машины времени"
    private final TimeManager timeManager = new TimeManager(LocalDateTime
            .of(2022, 9, 1, 0, 0, 0));
    // создаем банк
    private final Bank sber = new Bank(
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
    // временную точку отчета
    private final LocalDateTime dateFirst =
            LocalDateTime.of(2022, 9, 1, 0, 0, 0);

    public AppTest() throws Exception {
    }

    // проверка на то что у нас пользователь доверенный или нет
    // пользователь Александр не указал паспортные данные
    @Test
    public void checkUserAuthorization() throws Exception {
        User ivan = new UserBuilder("Ivan", "Petrov", 10000).withAddress("Green Street")
                .withPassportId(123).build();
        // Проверка булевского метода внутри пользователя
        // Иван должен вернуть true, то есть это значит что он доверенный
        Assert.assertTrue(ivan.verificationPersonalData());
        // Это плох вариант и у нас в вариации метода assert есть много синтаксического сахара
        // Assert.assertEquals(true, ivan.verificationPersonalData());
        // Александр должен вернуть False, ибо он не доверенный
        Assert.assertFalse(sasha.verificationPersonalData());
    }

    // Проверка функциональности дебетовой карты
    @Test
    public void testCheckDebitCard() throws Exception {
        // создаем центральный банк
        CentralBank centralBank = new CentralBank();
        // добавляем в центральный банк сбербанк
        centralBank.addBank(sber);
        // добавляем в сбербанк пользователя
        sber.addUser(sasha);
        // выдаем дебетовую карту пользователю Александр с изначальным балансом
        sber.addDebitCard(dateFirst, 50000, sasha.getUserId());
        // добавляем банк к наблюдателю, чтобы тот уведомил его каждый раз, когда наступает новый месяц
        timeManager.addObserver(sber);
        // имитируем добавление одного месяца
        timeManager.addMonth();
        // с помощью assert сравниваем баланс карты с ожидаемым значением после пройденного месяца
        // при заданных условиях банка
        // он делает не проверку, что x = 80000
        // а делает проверку, что 79999.999 <= x <= 80000.001
        assertEquals(80000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }

    // Проверка функциональности кредитной карты
    @Test
    public void testCheckCreditCard() throws Exception {
        // создаем центральный банк
        CentralBank centralBank = new CentralBank();
        // добавляем сбербанк в центральный банк
        centralBank.addBank(sber);
        // добавляем пользователя
        sber.addUser(sasha);
        // заводим ему кредитку
        sber.addCreditCard(dateFirst, 100, sasha.getUserId());
        // добавляем имитационный месяц
        timeManager.addMonth();
        // проверяем, что у нас баланс нулевой
        assertEquals(0, sber.getListCreditCards().get(0).getUntrustedUserLimit(), 0.001);
        // Пользователь снимает со счета 2 тыс. рублей
        sber.getListCreditCards().get(0).withdrawMoney(2000);
        timeManager.addObserver(sber);
        // потом мы добавляем еще месяц
        timeManager.addMonth();
        // И смотрим, что при жестких процентах пользователю накапал большой долг
        assertEquals(-31900, sber.getListCreditCards().get(0).getBalance(), 0.001);
    }

    // Проверка функциональности депозитного счета
    @Test
    public void testCheckDepositCard() throws Exception {
        // создаем центральный банк и добавляем сбербанк в центральный банк
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        // добавляем пользователя
        sber.addUser(sasha);
        // Открываем ему депозитный счет с 15 тыс. рублей
        sber.addDepositCard(dateFirst,
                LocalDateTime.of(2022, 9, 2, 0, 0, 0),
                15000,
                sasha.getUserId());
        // добавляем месяц
        timeManager.addObserver(sber);
        timeManager.addMonth();
        // и можно увидеть, что через месяц хранения денежной суммы у него вырос счет
        assertEquals(37500, sber.getListDepositCards().get(0).getBalance(), 0.001);
    }

    @Test
    public void testCheckTransferAndCancellationOfTransaction() throws Exception {
        // создаем Ивана с помощью билдера
        User ivan = new UserBuilder("Ivan", "Petrov", 10000)
                .withAddress("Green Street").withPassportId(123).build();
        // добавляем сбербанк в центральный банк и также
        // добавляем Александра и Ивана в наш банк
        CentralBank centralBank = new CentralBank();
        centralBank.addBank(sber);
        sber.addUser(sasha);
        sber.addUser(ivan);
        // создаем им дебетовые карты с начальным балансом 50000
        sber.addDebitCard(dateFirst, 50000, sasha.getUserId());
        sber.addDebitCard(dateFirst, 50000, ivan.getUserId());
        timeManager.addObserver(sber);
        // Один переводит другому денежные средства в сумме 25000
        centralBank.transferMoney(25000, sber.getListDebitCards().get(0).getCardId(),
                sber.getListDebitCards().get(1).getCardId());
        // И теперь мы видим, что в результате у нас у одного 75 тыс. а у другого 25 тыс
        assertEquals(75000, sber.getListDebitCards().get(1).getBalance(), 0.001);
        assertEquals(25000, sber.getListDebitCards().get(0).getBalance(), 0.001);
        // вызовем отмену транзакции
        centralBank.transactionCancellation(sber.getListDebitCards().get(0).getCardId(), 0);
        // и вызовем проверку баланса и в результате увидим, что после отмены транзакции мы вернулись к изначальному
        // балансу каждого пользователя = 50000
        assertEquals(50000, sber.getListDebitCards().get(1).getBalance(), 0.001);
        assertEquals(50000, sber.getListDebitCards().get(0).getBalance(), 0.001);
    }
}
