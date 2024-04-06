package org.example.entities;


import org.example.exception.UserBuilderException;

public class UserBuilder {
    private User user;

    /**
     * Создает объект User Builder с заданными именем, фамилией и балансом.
     *
     * @param name    имя пользователя не может быть нулевым или пустым
     * @param surname фамилия пользователя не может быть нулевой или пустой
     * @param balance баланс пользователя
     * @throws UserBuilderException если имя или фамилия являются нулевыми или пустыми
     */
    public UserBuilder(String name, String surname, double balance) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new UserBuilderException("Incorrect user name value");
        }
        if (surname == null || surname.trim().isEmpty()) {
            throw new UserBuilderException("Incorrect user surname value");
        }
        user = new User(name, surname, balance);
    }

    /**
     * Задает идентификатор паспорта пользователя.
     *
     * @param passportId идентификационный номер паспорта пользователя
     * @return объект UserBuilder
     * @throws UserBuilderException если идентификационный номер паспорта меньше или равен нулю
     */
    public UserBuilder withPassportId(int passportId) throws Exception {
        if (passportId <= 0) {
            throw new UserBuilderException("Incorrect user passportID value");
        }
        user.setPassportId(passportId);
        return this;
    }

    /**
     * Задает адрес пользователя.
     *
     * @param address адрес пользователя не может быть нулевым или пустым
     * @return объект UserBuilder
     * @throws UserBuilderException если адрес равен нулю или пуст
     */
    public UserBuilder withAddress(String address) throws Exception {
        if (address == null || address.trim().isEmpty()) {
            throw new UserBuilderException("Incorrect user address value");
        }
        user.setAddress(address);
        return this;
    }

    /**
     * Создает и возвращает пользовательский объект.
     *
     * @return User
     */
    public User build() {
        return user;
    }
}

