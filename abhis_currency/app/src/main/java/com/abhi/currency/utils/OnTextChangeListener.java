package com.abhi.currency.utils;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.Timer;
import java.util.TimerTask;

public abstract class OnTextChangeListener implements TextWatcher {

    public OnTextChangeListener(final long DELAY_MILL_SECONDS) {
        DELAY = DELAY_MILL_SECONDS;
    }

    public OnTextChangeListener() {
        DELAY = 200;
    }

    public abstract void onChange(final String mInputValue);

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    private Timer timer = new Timer();
    private final long DELAY; // Milliseconds

    @Override
    public void afterTextChanged(Editable s) {
        try {
            timer.cancel();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   String value = "";
                                   try {
                                       if (s != null && s.toString().trim().length() > 0) {
                                           value = s.toString().trim();
                                       }
                                   } catch (Exception e) {
                                       Utils.handledException(e);
                                   } finally {
                                       onChange(value.toLowerCase());
                                   }
                               }
                           },
                    DELAY
            );
        } catch (Exception e) {
            Utils.handledException(e);
        }
    }
}
