package com.iprody.xpaymentadapterapp.async;

import com.iprody.paymentserviceapp.async.Message;

public interface MessageHandler<T extends Message> {
    void handle(T message);
}
