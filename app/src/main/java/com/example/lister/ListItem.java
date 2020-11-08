package com.example.lister;

/**
 * Custom ListView item for constructing items in lists
 */
public class ListItem {
    private String name;
    private int quantity;
    private double price;
    private int itemId;
    private int listId;

    public ListItem(String name, int quantity, double price, int listId){
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.listId = listId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setItemId(int id) { this.itemId = id; }

    public int getItemId() {
        return itemId;
    }

    public int getListId() {
        return listId;
    }
}
