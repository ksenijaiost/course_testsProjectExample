package org.example.entities;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Класс транзакций представляет собой перевод денег с одного счета на другой.
 * Он содержит информацию об отправителе, получателе, дате транзакции и сумме переведенных денег.
 */
public class Transaction {
    private UUID from;
    private UUID to;
    private LocalDateTime dateTransaction;
    private double money;

    /**
     * Создает новый объект транзакции с указанным отправителем, получателем, датой и суммой денег.
     *
     * @param from            UUID карты, отправляющей деньги.
     * @param to              UUID карты, на которую поступают деньги.
     * @param dateTransaction дата и время совершения транзакции.
     * @param money           сумма денег, переведенная в ходе транзакции.
     */
    public Transaction(UUID from, UUID to, LocalDateTime dateTransaction, double money) {
        this.from = from;
        this.to = to;
        this.dateTransaction = dateTransaction;
        this.money = money;
    }

    /**
     * Создает новый объект транзакции с указанным получателем, датой и суммой денег, предполагая, что отправитель равен null.     *
     *
     * @param to              UUID учетной записи, на которую поступают деньги.
     * @param dateTransaction дата и время совершения транзакции.
     * @param money           сумма денег, переведенная в ходе транзакции.
     */
    public Transaction(UUID to, LocalDateTime dateTransaction, double money) {
        this.to = to;
        this.dateTransaction = dateTransaction;
        this.money = money;
        this.from = null;
    }

    /**
     * Создает новый объект транзакции с указанным отправителем, датой и суммой денег, предполагая, что получатель равен null
     *
     * @param dateTransaction дата и время совершения транзакции.
     * @param from            UUID учетной записи, отправляющей деньги.
     * @param money           сумма денег, переведенная в ходе транзакции.
     */
    public Transaction(LocalDateTime dateTransaction, UUID from, double money) {
        this.from = from;
        this.dateTransaction = dateTransaction;
        this.money = money;
        this.to = null;
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

    public double getMoney() {
        return money;
    }
}

