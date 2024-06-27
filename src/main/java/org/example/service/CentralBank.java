package org.example.service;

import org.example.entities.Bank;
import org.example.entities.ICard;
import org.example.exception.CentralBankException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
/**
 * CentralBank представляет собой центральный банк, который управляет списком банков и их транзакциями.
 *  @author Alexander Kichmarev
 *  @version 1.0
 *  @since 26.02.2023
 */
public class CentralBank {

    private final List<Bank> listBanks;

    /**
     * Создает новый объект CentralBank.
     */
    public CentralBank() {
        listBanks = new ArrayList<>();
    }

    public List<Bank> getListBanks() {
        return Collections.unmodifiableList(listBanks);
    }

    /**
     * Добавляет новый банк в список банков, управляемых этим центральным банком.
     *
     * @param newBank новый банк для добавления
     * @throws CentralBankException если параметр newBank равен нулю
     */
    public void addBank(Bank newBank) throws Exception {
        if (newBank == null) {
            throw new CentralBankException("Unable to add bank due to null object");
        }
        listBanks.add(newBank);
    }

    /**
     * Возвращает банк с указанным названием.
     *
     * @param title название банка к возврату
     * @return банк с указанным названием
     * @throws CentralBankException если банк с указанным названием не найден
     */
    public Bank getBank(String title) throws Exception {
        return listBanks.stream()
                .filter(bank -> title.equals(bank.getTitle()))
                .findFirst()
                .orElseThrow(() -> new CentralBankException("Bank not found"));
    }

    /**
     * Возвращает карту с указанным идентификатором карты.
     *
     * @param cardId ID карты для возврата
     * @return карта с указанным идентификатором карты
     * @throws CentralBankException если не найдена карта с указанным ID карты
     */
    public ICard getCard(UUID cardId) throws Exception {
        return listBanks.stream()
                .flatMap(bank -> bank.getListCards().stream())
                .filter(card -> card.getId().equals(cardId))
                .findFirst()
                .orElseThrow(() -> new CentralBankException("Card not found"));
    }

    /**
     * Переводит деньги с одной карты на другую.
     *
     * @param money      сумма денег для перевода
     * @param fromCardId ID карты, с которой нужно перевести деньги
     * @param toCardId   ID карты, на которую нужно перевести деньги
     * @throws CentralBankException если либо fromCardId, либо toCardId недействительны
     */
    public void transferMoney(double money, UUID fromCardId, UUID toCardId) throws Exception {
        getCard(fromCardId).transferMoney(money, getCard(toCardId));
    }

    /**
     * Отменяет транзакцию.
     *
     * @param user   ID пользователя, совершившего транзакцию
     * @param number номер транзакции для отмены
     * @throws CentralBankException если транзакцию нельзя отменить
     */
    public void transactionCancellation(UUID user, int number) throws Exception {
        ICard getCardTransaction = getCard(user);
        if (getCardTransaction.getTransaction(number).getFrom() != null && getCardTransaction.getTransaction(number).getTo() == null) {
            getCardTransaction.withdrawMoneyWithOutHistory(getCardTransaction.getTransaction(number).getMoney());
            getCardTransaction.removeTransaction(number);
        }
        else {
            if (getCardTransaction.getTransaction(number).getFrom() == null && getCardTransaction.getTransaction(number).getTo() != null) {
                getCardTransaction.topUpCardWithOutHistory(getCardTransaction.getTransaction(number).getMoney());
                getCardTransaction.removeTransaction(number);
            } else
            if (getCardTransaction.getTransaction(number).getFrom() != null && getCardTransaction.getTransaction(number).getTo() != null) {
                getCardTransaction.topUpCardWithOutHistory(getCardTransaction.getTransaction(number).getMoney());
                getCard(getCardTransaction.getTransaction(number).getTo()).withdrawMoneyWithOutHistory(getCardTransaction.getTransaction(number).getMoney());
                getCardTransaction.removeTransaction(number);
            }
        }
    }
}
