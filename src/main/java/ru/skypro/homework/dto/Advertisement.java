package ru.skypro.homework.dto;


import lombok.Data;

/**
 * @author Michail Z. (GH: HeimTN)
 */
@Data
public class Advertisement {
    private int author;
    private String image;
    private int pk;
    private int price;
    private String title;
}
