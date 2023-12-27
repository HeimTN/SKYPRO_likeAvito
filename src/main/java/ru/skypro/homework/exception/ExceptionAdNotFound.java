package ru.skypro.homework.exception;

public class ExceptionAdNotFound extends RuntimeException{
    public ExceptionAdNotFound(String errorMessage){
        super(errorMessage);
    }
}
