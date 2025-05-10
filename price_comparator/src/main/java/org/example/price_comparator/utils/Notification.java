package org.example.price_comparator.utils;

import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Notification<T> {
    private List<String> errors;
    private String successMessage;

    private T result;

    public Notification() {
        this.errors = new ArrayList<>();
    }

    public void addError(String error) {
        errors.add(error);
    }

    public List<String> getErrors() {
        return errors;
    }
    public boolean hasErrors(){
        return !errors.isEmpty();
    }
    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
    public String getSuccessMessage(){
        return successMessage;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}

