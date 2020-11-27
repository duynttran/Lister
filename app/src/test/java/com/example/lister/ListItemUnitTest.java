package com.example.lister;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * ListItem class unit testing, to be used in filling out custom ListView item rows
 */
public class ListItemUnitTest {
    ListItem item;
    int quantity = 4;
    String name = "testing";
    double price = 13.23;
    int listId = 7;

    public ListItemUnitTest(){
        item = new ListItem(name, quantity, price, listId);
    }

    @Test
    public void getName_isCorrect() {
        assertEquals(name, item.getName());
    }

    @Test
    public void setName_isCorrect_avg_case() {
        item.setName("Something else");
        assertEquals("Something else", item.getName());
        item.setName(name);
        assertEquals(name, item.getName());
    }

    @Test
    public void setName_isCorrect_empty_case() {
        String new_name = "";
        item.setName(new_name);
        assertEquals(new_name, item.getName());
        item.setName(name);
        assertEquals(name, item.getName());
    }

    @Test
    public void setName_isCorrect_singleCharacter_case() {
        String new_name = "a";
        item.setName(new_name);
        assertEquals(new_name, item.getName());
        item.setName(name);
        assertEquals(name, item.getName());
    }

    @Test
    public void setName_isCorrect_manyCharacter_case() {
        String new_name = "abcdefghi!@$AWERrstuvZCVSAwxyz!@$@$!$@N(awer. ,340@#(%";
        item.setName(new_name);
        assertEquals(new_name, item.getName());
        item.setName(name);
        assertEquals(name, item.getName());
    }

    @Test
    public void getQuantity_isCorrect(){
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    public void setQuantity_isCorrect_avg_case() {
        int new_quantity = 4;
        item.setQuantity(new_quantity);
        assertEquals(new_quantity, item.getQuantity());
        item.setQuantity(quantity);
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    public void setQuantity_isCorrect_negative_case() {
        int new_quantity = -4;
        item.setQuantity(new_quantity);
        assertEquals(0, item.getQuantity());
        item.setQuantity(quantity);
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    public void setQuantity_isCorrect_zero_case() {
        int new_quantity = 0;
        item.setQuantity(new_quantity);
        assertEquals(new_quantity, item.getQuantity());
        item.setQuantity(quantity);
        assertEquals(quantity, item.getQuantity());
    }

    @Test
    public void getPrice_isCorrect() {
        assertEquals(price, item.getPrice(), 0);
    }

    @Test
    public void setPrice_isCorrect_avg_case() {
        double new_price = 6.69;
        item.setPrice(new_price);
        assertEquals(new_price, item.getPrice(), 0);
        item.setPrice(price);
        assertEquals(price, item.getPrice(), 0);
    }

    @Test
    public void setPrice_isCorrect_zero_case() {
        double new_price = 0;
        item.setPrice(new_price);
        assertEquals(new_price, item.getPrice(), 0);
        item.setPrice(price);
        assertEquals(price, item.getPrice(), 0);
    }

    @Test
    public void setPrice_isCorrect_negative_case() {
        double new_price = -3.39;
        item.setPrice(new_price);
        assertEquals(0, item.getPrice(), 0);
        item.setPrice(price);
        assertEquals(price, item.getPrice(), 0);
    }

    @Test
    public void setPrice_isCorrect_large_case() {
        double new_price = 1249124.39;
        item.setPrice(new_price);
        assertEquals(new_price, item.getPrice(), 0);
        item.setPrice(price);
        assertEquals(price, item.getPrice(), 0);
    }

    @Test
    public void getListId_isCorrect() {
        assertEquals(listId, item.getListId(), 0);
    }

    @Test
    public void setItemId_isCorrect_zero_case() {
        int new_itemId = 0;
        item.setItemId(new_itemId);
        assertEquals(new_itemId, item.getItemId(), 0);
    }

    @Test
    public void setItemId_isCorrect_avg_case() {
        int new_itemId = 12;
        item.setItemId(new_itemId);
        assertEquals(new_itemId, item.getItemId(), 0);
    }
}