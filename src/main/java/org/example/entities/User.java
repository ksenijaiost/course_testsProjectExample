package org.example.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Класс User представляет пользователя платежной системы.
 * Он содержит личную информацию пользователя, такую как имя, фамилия, идентификационный номер паспорта и адрес, а
 * также их баланс и список идентификаторов их карт.
 */
public class User {
    private final List<UUID> listCardId = new ArrayList<>();
    private UUID userId;
    private final String Name;
    private final String Surname;
    public int PassportId;
    public double Balance;
    public String Address;

    /**
     * Создает новый пользовательский объект с заданными именем, фамилией и балансом.
     *
     * @param name    Имя пользователя.
     * @param surname Фамилия пользователя.
     * @param balance Баланс пользователя.
     */
    public User(String name, String surname, double balance) {
        Name = name;
        Surname = surname;
        Balance = balance;
    }

    public List<UUID> getListCardId() {
        return Collections.unmodifiableList(listCardId);
    }

    public UUID getUserId() {
        return userId;
    }

    /**
     * Проверяет, является пользователь подтвержденным.
     *
     * @return True, если данные пользователя полные, false в противном случае.
     */
    public boolean verificationPersonalData() {
        return Name != null && Surname != null && PassportId > 0 && Address != null;
    }

    void setAddress(String address) {
        Address = address;
    }

    public void addCard(UUID card) {
        listCardId.add(card);
    }

    void setPassportId(int passportId) {
        PassportId = passportId;
    }
}