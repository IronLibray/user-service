package com.ironlibrary.user_service.model;


/**
 * Enum para los tipos de membresía de usuarios
 */
public enum MembershipType {
    BASIC("Básica", 3, 14),
    PREMIUM("Premium", 10, 30),
    STUDENT("Estudiante", 5, 21);

    private final String displayName;
    private final int maxBooks;
    private final int loanDurationDays;

    MembershipType(String displayName, int maxBooks, int loanDurationDays) {
        this.displayName = displayName;
        this.maxBooks = maxBooks;
        this.loanDurationDays = loanDurationDays;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxBooks() {
        return maxBooks;
    }

    public int getLoanDurationDays() {
        return loanDurationDays;
    }
}
