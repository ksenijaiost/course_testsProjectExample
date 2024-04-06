package org.example.entities;

import java.time.LocalDateTime;

public interface IObserver {
    void update(LocalDateTime timeStamp) throws Exception;
}

