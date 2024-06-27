package org.example.entities;


import org.example.exception.CreditCardException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CreditCard представляет собой кредитную карту, которая реализует интерфейс iCard.
 */
public class CreditCard implements ICard {
    private final List<Transaction> transaction;
    private final UUID cardId;
    private double balance;
    private double commission;
    private double creditLimit;
    private boolean identification;
    private double untrustedUserLimit;
    private final LocalDateTime dateCreate;
    private LocalDateTime timeNow;

    /**
     * Создает объект CreditCard с заданными параметрами.
     *
     * @param dateCreate     дата и время создания кредитной карты
     * @param balance        начальный баланс кредитной карты
     * @param identification независимо от того, был ли идентифицирован пользователь или нет
     * @throws CreditCardException если баланс отрицательный
     */
    public CreditCard(LocalDateTime dateCreate, double balance, boolean identification) throws Exception {
        if (balance < 0) {
            throw new CreditCardException("Creating an account must be with a positive balance");
        }
        this.balance = balance;
        this.transaction = new ArrayList<>();
        this.cardId = UUID.randomUUID();
        this.dateCreate = dateCreate;
        this.commission = 0;
        this.creditLimit = 0;
        this.identification = identification;
        this.timeNow = LocalDateTime.now();
    }

    public boolean getIdentification() {
        return identification;
    }

    public double getBalance() {
        return balance;
    }

    public double getUntrustedUserLimit() {
        return untrustedUserLimit;
    }

    public UUID getCardId() {
        return cardId;
    }

    public void setIdentificationFlag() {
        identification = true;
    }

    public void removeTransaction(int number) {
        transaction.remove(number);
    }

    public void setUntrustedUserLimit(double untrustedUserLimit) throws Exception {
        if (untrustedUserLimit < 0) {
            throw new CreditCardException("Limit must be positive");
        }
        this.untrustedUserLimit = untrustedUserLimit;
    }

    /**
     * Увеличивает время действия карты до указанной отметки даты и рассчитывает комиссию, если баланс отрицательный.
     *
     * @param dateStamp новая дата, на которую нужно установить время карты.
     */
    public void addDay(LocalDateTime dateStamp) {
        timeNow = dateStamp;
        if (balance < 0) {
            balance -= commission;
        }
    }

    public void setCommission(double commission) throws Exception {
        if (commission < 0) {
            throw new CreditCardException("Credit commission must be a positive number");
        }
        this.commission = commission;
    }

    public void setCreditLimit(double creditLimit) throws Exception {
        if (creditLimit > 0) {
            throw new CreditCardException("Credit limit must be negative");
        }
        this.creditLimit = creditLimit;
    }

    /**
     * Снимает деньги с баланса кредитной карты.
     *
     * @param money сумма денег, подлежащая выводу
     * @throws CreditCardException если сумма, подлежащая выводу, отрицательна и т.д
     */
    public void withdrawMoney(double money) throws Exception {
        if (!identification && money > untrustedUserLimit && untrustedUserLimit != 0) {
            throw new CreditCardException("Limit exceeded for an unidentified user");
        }
        if (money <= 0) {
            throw new CreditCardException("You can't take a negative value");
        }
        if (balance - money < creditLimit && creditLimit != 0) {
            throw new CreditCardException("Credit limit exceeded when withdrawing");
        }
        balance -= money;
        transaction.add(new Transaction(cardId, timeNow, money));
    }

    /**
     * Снимает деньги со счета кредитной карты без добавления транзакции в историю транзакций.
     *
     * @param money сумма денег, подлежащая снятию со счета.
     * @throws CreditCardException если пользователь не идентифицирован и сумма вывода превышает лимит ненадежного пользователя,
     *                             если сумма вывода отрицательна или если сумма вывода превышает кредитный лимит.
     */
    public void withdrawMoneyWithOutHistory(double money) throws Exception {
        if (!identification && money > untrustedUserLimit) {
            throw new CreditCardException("Limit exceeded for an unidentified user");
        }
        if (money <= 0) {
            throw new CreditCardException("You can't take a negative value");
        }
        if (balance - money < creditLimit) {
            throw new CreditCardException("Credit limit exceeded when withdrawing");
        }
        balance -= money;
    }

    /**
     * Добавляет указанную сумму денег на баланс карты и записывает транзакцию.
     *
     * @param money сумма денег, подлежащая зачислению на баланс, должна иметь положительное значение
     * @throws CreditCardException если сумма денег отрицательна или равна нулю
     */
    public void topUpCard(double money) throws Exception {
        if (money <= 0) {
            throw new CreditCardException("Can't top up card negative or zero value");
        }
        balance += money;
        transaction.add(new Transaction(LocalDateTime.now(), cardId, money));
    }

    public UUID getId() {
        return cardId;
    }

    /**
     * Adds specified amount of money to the balance of the credit card without recording the transaction history.
     *
     * @param money the amount of money to be added to the balance. Must be a positive non-zero value.
     * @throws CreditCardException if the specified amount of money is negative or zero, which is not allowed for topping up a card.
     */
    public void topUpCardWithOutHistory(double money) throws Exception {
        if (money <= 0) {
            throw new CreditCardException("Can't top up card negative or zero value");
        }
        balance += money;
    }

    /**
     * Переводит деньги с этой кредитной карты на другую карту.
     *
     * @param money сумма денег для перевода
     * @param card  карта для перевода денег на другой счет
     * @throws CreditCardException если пользователь ненадежен и сумма перевода превышает лимит ненадежного пользователя,
     *                             если сумма перевода отрицательна или равна нулю, или если при переводе превышен кредитный лимит.
     * @throws Exception           если во время передачи возникнет какая-либо другая ошибка.
     */
    public void transferMoney(double money, ICard card) throws Exception {
        if (!identification && money > untrustedUserLimit) {
            throw new CreditCardException("Untrusted user limit exceeded when transferring money");
        }
        if (money <= 0) {
            throw new CreditCardException("You can't take a negative value");
        }
        balance -= money;
        card.topUpCardWithOutHistory(money);
        transaction.add(new Transaction(cardId, card.getId(), LocalDateTime.now(), money));
    }

    /**
     * Возвращает транзакцию с указанным номером.
     * Выдает исключение CreditCardException, если индекс отрицательный или транзакция не существует.
     *
     * @param number индекс транзакции для извлечения
     * @return транзакция с указанным индексом
     * @throws CreditCardException если индекс отрицательный или транзакция не существует
     */
    public Transaction getTransaction(int number) throws Exception {
        if (number < 0) {
            throw new CreditCardException("Index cannot be negative");
        }
        return transaction.get(number);
    }
}

