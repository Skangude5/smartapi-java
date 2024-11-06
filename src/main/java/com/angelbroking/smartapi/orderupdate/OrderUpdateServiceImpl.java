package com.angelbroking.smartapi.orderupdate;

import com.angelbroking.smartapi.smartstream.models.SmartStreamError;

public class OrderUpdateServiceImpl implements OrderUpdateListner{
    @Override
    public void onConnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDisconnected() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onError(SmartStreamError error) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onPong() {

    }

    @Override
    public void onOrderUpdate(String data) {
        System.out.println("order data: " + data);
    }
}
