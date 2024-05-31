package com.ua.accommodation.service.event;

import org.springframework.context.ApplicationEvent;

public class NotificationEvent extends ApplicationEvent {
    private final String message;

    public NotificationEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

