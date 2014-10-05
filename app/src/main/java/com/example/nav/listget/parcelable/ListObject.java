package com.example.nav.listget.parcelable;

import java.io.Serializable;



public class ListObject implements Serializable {
    private int categoryId;
    private String category;
    private int color;
    private int numTask = 0;


    public ListObject(int id, String l, int c){
        categoryId = id;
        category = l;
        color = c;

    }
    public ListObject(int id, String l){
        categoryId = id;
        category = l;
    }

    public ListObject(int id, String l, int c, int n){
        categoryId = id;
        category = l;
        color = c;
        numTask = n;
    }

    public int getNumTask(){
        return numTask;
    }
    public void setNumTask(int n){
        numTask = n;
    }

    public String toString(){
        return  category;
    }

    public void setCategoryId(int id){
        categoryId = id;
    }

    public void setCategory(String t){
        category = t;
    }
    public void setColor(int c){
        color = c;
    }

    public int getCategoryId(){
        return categoryId;
    }

    public String getCategory(){
        return category;
    }

    public int getColor(){
        return color;
    }



}
