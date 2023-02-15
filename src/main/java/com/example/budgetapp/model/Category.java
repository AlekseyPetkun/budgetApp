package com.example.budgetapp.model;

/**
 * Категории трат.
 */

public enum Category {

    FOOD("еда"),
    CLOTHES("одежда"),
    TRANSPORT("транспорт"),
    HOBBY("хобби"),
    ;

    private final String translation;

    Category(String translation) {
        this.translation = translation;
    }

    public String getTranslation() {
        return translation;
    }
}
