package model;

public interface Validatable {
    void validate();

    default boolean isValid() {
        try {
            validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static void printValidationMessage(Validatable obj) {
        System.out.println("Validating: " + obj.getClass().getSimpleName());
    }
}