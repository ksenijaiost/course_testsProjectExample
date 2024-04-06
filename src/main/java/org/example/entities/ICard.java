package org.example.entities;

import java.util.UUID;

public interface ICard {
    void withdrawMoneyWithOutHistory(double money) throws Exception;

    void topUpCardWithOutHistory(double money) throws Exception;

    UUID getId();

    void transferMoney(double money, ICard card) throws Exception;

    Transaction getTransaction(int number) throws Exception;

    void topUpCard(double money) throws Exception;

    void withdrawMoney(double money) throws Exception;

    boolean getIdentification();

    void setIdentificationFlag();

    void removeTransaction(int number) throws Exception;
}

