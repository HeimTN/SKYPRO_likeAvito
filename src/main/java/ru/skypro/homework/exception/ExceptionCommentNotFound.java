package ru.skypro.homework.exception;

public class ExceptionCommentNotFound extends RuntimeException{
    public ExceptionCommentNotFound(String errorMessage){
        super(errorMessage);
    }
}
