package org.example.entities;

import org.example.exception.DepositCardException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Класс DepositCard представляет собой депозитную карту, которая является разновидностью iCard.
 * В нем есть список транзакций, начальный баланс, текущий баланс, дата создания, дата окончания,
 * идентификационный флаг, текущая дата, сумма в процентах, уникальный идентификатор карты и лимит ненадежного пользователя.
 */
public class DepositCard implements ICard {
    private final List<Transaction> transactions;
    private final double startBalance;
    private double balance;
    private final LocalDateTime dateCreate;
    private final LocalDateTime dateEnd;
    private boolean identification;
    private LocalDateTime dateNow;
    private double percentSum;
    private final UUID cardId;
    private double untrustedUserLimit;

    /**
     * Создает новый объект DepositCard с заданным балансом, датой окончания, датой создания и идентификационным флагом.
     *
     * @param balance        Начальный баланс депозитной карты.
     * @param dateEnd        Дата истечения срока действия депозитной карты.
     * @param dateCreate     Дата создания депозитной карты.
     * @param identification Флажок, указывающий, была ли идентифицирована депозитная карта.
     * @throws DepositCardException Если баланс отрицательный или дата окончания предшествует дате создания.
     */
    public DepositCard(double balance, LocalDateTime dateEnd, LocalDateTime dateCreate, boolean identification) throws Exception {
        if (balance <= 0) {
            throw new DepositCardException("You cannot create an account with a negative balance");
        }
        if (dateEnd.isBefore(dateCreate)) {
            throw new DepositCardException("Account end time must be later than creation time");
        }
        this.balance = balance;
        this.startBalance = balance;
        this.percentSum = 0;
        this.cardId = UUID.randomUUID();
        this.dateEnd = dateEnd;
        this.dateCreate = dateCreate;
        this.dateNow = dateCreate;
        this.transactions = new ArrayList<>();
        this.identification = identification;
    }

    /**
     * Получает транзакцию по указанному номеру.
     *
     * @param number Номер транзакции, которую нужно получить.
     * @return Транзакция по указанному номеру.
     * @throws DepositCardException Если число отрицательное.
     */
    public Transaction getTransaction(int number) throws Exception {
        if (number < 0) {
            throw new DepositCardException("The transaction number cannot be negative");
        }
        return transactions.get(number);
    }

    public double getStartBalance() {
        return startBalance;
    }

    public double getBalance() {
        return balance;
    }

    public LocalDateTime getDateCreate() {
        return dateCreate;
    }

    public boolean getIdentification() {
        return identification;
    }

    public UUID getCardId() {
        return cardId;
    }

    public void setUntrustedUserLimit(double untrustedUserLimit) throws Exception {
        if (untrustedUserLimit < 0) {
            throw new DepositCardException("You cannot withdraw above the limit for an unidentified user");
        }
        this.untrustedUserLimit = untrustedUserLimit;
    }

    /**
     * Удаляет транзакцию из списка транзакций, связанных с депозитной картой.
     *
     * @param number номер транзакции, подлежащей удалению
     * @throws DepositCardException если номер транзакции отрицательный
     * @throws Exception            если при попытке удалить транзакцию возникает ошибка
     */
    public void removeTransaction(int number) throws Exception {
        if (number < 0) {
            throw new DepositCardException("The transaction number cannot be negative");
        }
        transactions.remove(number);
    }

    public void setIdentificationFlag() {
        identification = true;
    }

    /**
     * Устанавливает текущую дату на указанную дату.
     *
     * @param dateStamp дата, чтобы установить текущую дату
     */
    public void addDay(LocalDateTime dateStamp) {
        dateNow = dateStamp;
    }

    /**
     * Снимает указанную сумму денег с баланса депозитной карты.
     *
     * @param money сумма денег для вывода
     * @throws DepositCardException если пользователь не идентифицирован и сумма вывода превышает лимит для неидентифицированных пользователей,
     *                              если сумма вывода отрицательная,
     *                              если сумма вывода превышает баланс,
     *                              если текущая дата предшествует дате окончания срока действия депозитной карты
     * @throws Exception            если при попытке вывести деньги возникает ошибка
     */
    public void withdrawMoney(double money) throws Exception {
        forWithdrawMoney(money);
        if (dateNow.isBefore(dateEnd)) {
            throw new DepositCardException("Error");
        }
        balance -= money;
        transactions.add(new Transaction(cardId, dateNow, money));
    }

    /**
     * Снимает указанную сумму денег с баланса депозитной карты без добавления транзакции в историю транзакций.
     *
     * @param money сумма денег для вывода
     * @throws DepositCardException если пользователь не идентифицирован и сумма вывода превышает лимит для неидентифицированных пользователей,
     *                              если сумма вывода отрицательная,
     *                              если сумма вывода превышает баланс.
     * @throws Exception            если при попытке вывести деньги возникает ошибка
     */
    public void withdrawMoneyWithOutHistory(double money) throws Exception {
        forWithdrawMoney(money);
        if (LocalDateTime.now().isBefore(dateEnd)) {
            throw new DepositCardException("Date is uncorrected");
        }
        balance -= money;
    }

    /**
     * Общие проверки для снятия указанной суммы денег с баланса депозитной карты.
     *
     * @param money сумма денег для вывода
     * @throws DepositCardException если пользователь не идентифицирован и сумма вывода превышает лимит для неидентифицированных пользователей,
     *                              если сумма вывода отрицательная,
     *                              если сумма вывода превышает баланс.
     * @throws Exception            если при попытке вывести деньги возникает ошибка
     */
    private void forWithdrawMoney(double money) throws Exception {
        if (!identification && money > untrustedUserLimit) {
            throw new DepositCardException("You cannot withdraw above the limit for an unidentified user");
        }
        if (money <= 0) {
            throw new DepositCardException("You can't take a negative value");
        }
        if (balance - money < 0) {
            throw new DepositCardException("Debit card cannot go into negative");
        }
    }

    /**
     * Добавляет указанную сумму денег на баланс депозитной карты и добавляет транзакцию в историю транзакций.
     *
     * @param money сумма денег для пополнения баланса депозитной карты
     * @throws DepositCardException если сумма пополнения отрицательна или равна нулю
     * @throws Exception            если при попытке пополнить баланс депозитной карты произошла ошибка
     */
    public void topUpCard(double money) throws Exception {
        if (money <= 0) {
            throw new DepositCardException("Can't top up card negative or zero value");
        }
        balance += money;
        transactions.add(new Transaction(LocalDateTime.now(), cardId, money));
    }

    public UUID getId() {
        return cardId;
    }

    /**
     * Начисляет проценты на баланс депозитной карты в соответствии с указанным процентом.
     *
     * @param percentSum процент, который будет добавлен к балансу депозитной карты
     * @throws DepositCardException если процент отрицательный
     * @throws Exception            если при попытке начисления процентов на баланс депозитной карты возникает ошибка
     */
    public void addPercentSum(double percentSum) throws Exception {
        if (percentSum < 0) {
            throw new DepositCardException("Percentage cannot be negative");
        }
        this.percentSum += balance * percentSum / 100;
    }

    /**
     * Рассчитывает общую сумму процентов, накопленных по депозитной карте, и добавляет ее к балансу депозитной карты.
     * После расчета процентов установлю значение percentSum равным нулю.
     */
    public void interestCalculation() {
        balance += percentSum;
        percentSum = 0;
    }

    /**
     * Добавляет указанную сумму денег на баланс депозитной карты без добавления транзакции в историю транзакций.
     *
     * @param money сумма денег для пополнения баланса депозитной карты
     * @throws DepositCardException если сумма пополнения отрицательна или равна нулю
     * @throws Exception            если при попытке пополнить баланс депозитной карты произошла ошибка
     */
    public void topUpCardWithOutHistory(double money) throws Exception {
        if (money <= 0) {
            throw new DepositCardException("Can't top up card negative or zero value");
        }
        balance += money;
    }

    /**
     * Переводит указанную сумму денег на указанный объект iCard без добавления
     * транзакции в историю транзакций текущей депозитной карты.
     *
     * @param money сумма денег для перевода
     * @param card  объект iCard, на который должны быть переведены деньги
     * @throws DepositCardException если сумма перевода отрицательна или равна нулю, баланс текущей депозитной карты
     *                              недостаточен или перевод осуществляется неустановленному пользователю сверх лимита
     * @throws Exception            если при попытке перевести деньги на указанный объект iCard возникает ошибка
     */
    public void transferMoney(double money, ICard card) throws Exception {
        if (!identification && money > untrustedUserLimit) {
            throw new DepositCardException("You cannot transfer money to an unidentified user above the limit");
        }
        if (money <= 0) {
            throw new DepositCardException("You can't take a negative value");
        }
        if (balance - money < 0) {
            throw new DepositCardException("Debit card cannot go into negative");
        }
        if (LocalDateTime.now().isBefore(dateEnd)) {
            throw new DepositCardException("The card hasn't expired yet");
        }
        balance -= money;
        card.topUpCardWithOutHistory(money);
        transactions.add(new Transaction(cardId, card.getId(), LocalDateTime.now(), money));
    }
}
