package com.abhi.currency.utils;

import androidx.annotation.NonNull;

import com.abhi.currency.models.CurrencyRate;

import java.util.LinkedList;

public class BubbleSortManager {
    private CurrencyRate head;
    private final int size;

    public BubbleSortManager(@NonNull LinkedList<CurrencyRate> mCurrencyRateList) {
        this.head = null;
        for (CurrencyRate rate : mCurrencyRateList) {
            add(new CurrencyRate(rate.getRate(), rate.getCurrencyCode()));
        }
        this.size = mCurrencyRateList.size();
    }

    private void add(CurrencyRate node) {
        if (head == null) {
            head = node;
        } else {
            CurrencyRate currentNode = head;
            while (currentNode.nextNode != null) {
                currentNode = currentNode.nextNode;
            }
            currentNode.nextNode = node;
        }
    }

    public void sort(boolean isAscending) {
        if (size > 1) {
            boolean wasChanged;
            do {
                CurrencyRate current = head;
                CurrencyRate previous = null;
                CurrencyRate next = head.nextNode;
                wasChanged = false;

                while (next != null) {
                    if (isAscending ? (current.getRate() > next.getRate()) : (current.getRate() < next.getRate())) {
                        wasChanged = true;

                        if (previous != null) {
                            CurrencyRate sig = next.nextNode;

                            previous.nextNode = next;
                            next.nextNode = current;
                            current.nextNode = sig;
                        } else {
                            CurrencyRate sig = next.nextNode;

                            head = next;
                            next.nextNode = current;
                            current.nextNode = sig;
                        }
                        previous = next;
                        next = current.nextNode;
                    } else {
                        previous = current;
                        current = next;
                        next = next.nextNode;
                    }
                }
            } while (wasChanged);
        }
    }

    public LinkedList<CurrencyRate> getList() {
        LinkedList<CurrencyRate> mList = new LinkedList<>();
        CurrencyRate currentNode = head;
        while (currentNode != null) {
            mList.add(currentNode);
            currentNode = currentNode.nextNode;
        }
        return mList;
    }
}
