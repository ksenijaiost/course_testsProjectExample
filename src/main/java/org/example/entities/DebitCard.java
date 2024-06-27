package org.example.entities;


import org.example.exception.DebitCardException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс DebitCard реализует интерфейс iCard и представляет собой дебетовую карту, которую можно использовать для
 * снятия и пополнения денежных средств,
 * перевода средств между картами, добавления процентной суммы и просмотра истории транзакций. У него есть
 * идентификатор карты, дата создания, текущая дата,
 * баланс, список транзакций, процентная сумма, флаг, указывающий, был ли пользователь идентифицирован или нет, и
 * лимит ненадежного пользователя.
 */
public class DebitCard implements ICard {
    private final List<Transaction> transactions;
    private final UUID cardId;
    private final LocalDateTime dateCreate;
    private LocalDateTime dateNow;
    private double percentSum;
    private double balance;
    private boolean identification;
    private double untrustedUserLimit;

    /**
     * Создает новый экземпляр DebitCard с заданной датой создания, балансом и статусом идентификации.  *
     *
     * @param dateCreate     дата создания дебетовой карты.
     * @param balance        начальный баланс дебетовой карты.
     * @param identification идентификационный статус пользователя.
     * @throws DebitCardException если баланс отрицательный.
     */
    public DebitCard(LocalDateTime dateCreate, double balance, boolean identification) throws Exception {
        if (balance < 0) {
            throw new DebitCardException("Account creation cannot be with a negative balance");
        }
        this.balance = balance;
        this.transactions = new ArrayList<>();
        this.cardId = UUID.randomUUID();
        this.dateCreate = dateCreate;
        this.dateNow = dateCreate;
        this.percentSum = 0;
        this.identification = identification;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public double getBalance() {
        return balance;
    }

    public UUID getCardId() {
        return cardId;
    }

    /**
     * Удаляет транзакцию с указанным номером из списка транзакций.
     *
     * @param number номер транзакции, которую нужно удалить.
     * @throws DebitCardException если число отрицательное.
     */
    public void removeTransaction(int number) throws Exception {
        if (number < 0) {
            throw new DebitCardException("The transaction number cannot be negative");
        }
        transactions.remove(number);
    }

    public void setUntrustedUserLimit(double untrustedUserLimit) throws Exception {
        if (untrustedUserLimit < 0) {
            throw new DebitCardException("Limit must be positive");
        }
        this.untrustedUserLimit = untrustedUserLimit;
    }

    /**
     * Устанавливает текущую дату дебетовой карты на заданную дату.
     *
     * @param dateStamp дата, которую нужно установить в качестве текущей даты.
     */
    public void addDay(LocalDateTime dateStamp) {
        dateNow = dateStamp;
    }

    public boolean getIdentification() {
        return identification;
    }

    public void setIdentificationFlag() {
        identification = true;
    }

    /**
     * Этот метод позволяет пользователю снять указанную сумму денег с баланса дебетовой карты.
     *
     * @param money Сумма денег, подлежащая снятию.
     * @throws DebitCardException Если пользователь не идентифицирован и сумма вывода превышает лимит ненадежного
     * пользователя
     */
    public void withdrawMoney(double money) throws Exception {
        if (!identification && money > untrustedUserLimit) {
            throw new DebitCardException("Limit exceeded for an unidentified user");
        }
        if (money <= 0) {
            throw new DebitCardException("You can't take a negative value");
        }
        if (balance - money < 0) {
            throw new DebitCardException("Debit card cannot go into negative");
        }
        balance -= money;
        transactions.add(new Transaction(cardId, dateNow, money));
    }

    /**
     * Снимает указанную сумму денег с баланса дебетовой карты без добавления транзакции в историю.
     *
     * @param money сумма денег для снятия с баланса дебетовой карты
     * @throws DebitCardException если вывод средств нарушает любое из следующих условий::
     *                            - Пользователь не идентифицирован, и сумма вывода превышает лимит ненадежного
     *                            пользователя (если он не равен 0)
     *                            - Сумма вывода меньше или равна 0
     *                            - Сумма вывода превышает текущий баланс на дебетовой карте
     */
    public void withdrawMoneyWithOutHistory(double money) throws Exception {
        if (!identification & money > untrustedUserLimit & untrustedUserLimit != 0) {
            throw new DebitCardException("Limit exceeded for an unidentified user");
        }
        if (money <= 0) {
            throw new DebitCardException("You can't take a negative value");
        }
        if (balance - money < 0) {
            throw new DebitCardException("Debit card cannot go into negative");
        }
        balance -= money;
    }

    /**
     * Увеличивает баланс дебетовой карты на указанную сумму.
     *
     * @param money сумма денег, которая будет добавлена к балансу дебетовой карты.
     * @throws DebitCardException если указанная сумма отрицательна или равна нулю.
     */
    public void topUpCard(double money) throws Exception {
        if (money <= 0) {
            throw new DebitCardException("Can't top up card negative or zero value");
        }
        balance += money;
        transactions.add(new Transaction(dateNow, cardId, money));
    }

    /**
     * Увеличивает баланс дебетовой карты на заданную сумму без создания новой истории транзакций.
     *
     * @param money сумма денег для пополнения баланса карты
     * @throws DebitCardException если предоставленная денежная сумма отрицательна или равна нулю
     */
    public void topUpCardWithOutHistory(double money) throws Exception {
        if (money <= 0) {
            throw new DebitCardException("Can't top up card negative or zero value");
        }
        balance += money;
    }

    /**
     * Переведите определенную сумму денег на другую карту.
     *
     * @param money сумма денег для перевода
     * @param card  карта назначения для перевода денег на другой счет
     * @throws DebitCardException если сумма перевода отрицательная, текущий баланс недостаточен или лимит превышен
     * для неидентифицированного пользователя
     */
    public void transferMoney(double money, ICard card) throws Exception {
        if (!identification && money > untrustedUserLimit && untrustedUserLimit != 0) {
            throw new DebitCardException("Limit exceeded for an unidentified user");
        }
        if (money <= 0) {
            throw new DebitCardException("You can't take a negative value");
        }
        if (balance - money < 0) {
            throw new DebitCardException("Debit card cannot go into negative");
        }
        balance -= money;
        card.topUpCardWithOutHistory(money);
        transactions.add(new Transaction(cardId, card.getId(), dateNow, money));
    }

    public UUID getId() {
        return cardId;
    }

    /**
     * Добавляет заданный процент от текущего баланса в виде процентной суммы.
     *
     * @param percent процент, который будет добавлен к сумме процентов
     * @throws DebitCardException если процент отрицательный
     */
    public void addPercentSum(double percent) throws Exception {
        if (percent < 0) {
            throw new DebitCardException("Percentage cannot be negative");
        }
        percentSum += balance * percent / 100;
    }

    /**
     * Рассчитывает и добавляет накопленные проценты к текущему балансу карты.
     * Этот метод должен быть вызван в конце указанного периода расчета процентов.
     */
    public void interestCalculation() {
        balance += percentSum;
        percentSum = 0;
    }

    /**
     * Возвращает объект транзакции для указанного номера транзакции.
     *
     * @param number номер транзакции для извлечения
     * @return объект транзакции для указанного номера транзакции
     * @throws DebitCardException если номер транзакции отрицательный или недействительный
     */
    public Transaction getTransaction(int number) throws Exception {
        if (number < 0) {
            throw new DebitCardException("The transaction number cannot be negative");
        }
        return transactions.get(number);
    }
}
