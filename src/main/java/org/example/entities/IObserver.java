package org.example.entities;

import java.time.LocalDateTime;

/**
 * Паттерн проектирования - уведомляет о том, что прошёл месяц.
 */
public interface IObserver {
    void update(LocalDateTime timeStamp) throws Exception;
}

