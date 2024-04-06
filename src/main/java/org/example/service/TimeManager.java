package org.example.service;


import org.example.entities.IObserver;
import org.example.exception.TimeManagerException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс TimeManager отвечает за управление временем и уведомление своих наблюдателей об изменении времени.
 */
public class TimeManager {
    private List<IObserver> observers;
    private LocalDateTime timeStamp;

    /**
     * Создает новый объект Time Manager с заданной начальной временной меткой.
     *
     * @param timeStamp начальная временная метка, которая должна быть установлена
     */
    public TimeManager(LocalDateTime timeStamp) {
        this.observers = new ArrayList<>();
        this.timeStamp = timeStamp;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    /**
     * Добавляет нового наблюдателя в список наблюдателей, которые будут уведомлены об изменении времени.
     *
     * @param bank наблюдатель, который будет добавлен
     * @throws TimeManagerException если наблюдатель равен нулю
     */
    public void addObserver(IObserver bank) throws Exception {
        if (bank == null) {
            throw new TimeManagerException("bank is null");
        }
        observers.add(bank);
    }

    /**
     * Добавляет один день к текущей временной метке, управляемой объектом Time Manager, и уведомляет своих наблюдателей.
     */
    public void addDay() throws Exception {
        timeStamp = timeStamp.plusDays(1);
        notifyObservers();
    }

    /**
     * Добавляет один месяц (30 дней) к текущей временной метке, управляемой объектом Time Manager, и уведомляет своих наблюдателей.
     */
    public void addMonth() throws Exception {
        for (int i = 0; i < 30; i++) {
            addDay();
        }
    }

    /**
     * Уведомляет всех наблюдателей о том, что текущая временная метка была обновлена.
     */
    public void notifyObservers() throws Exception {
        for (IObserver observer : observers) {
            observer.update(timeStamp);
        }
    }
}