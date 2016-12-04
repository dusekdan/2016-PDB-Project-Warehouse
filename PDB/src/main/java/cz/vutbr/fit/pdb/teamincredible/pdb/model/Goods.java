/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredible.pdb.model;

/**
 *
 * @author Sucha
 */
public class Goods {

    private String title;
    public void setTitle(String newTitle)
    {
        title = newTitle;
    }
    public String getTitle()
    {
        return title;
    }

    private String picture;
    public void setPicture(String newPicture)
    {
        picture = newPicture;
    }
    
    private String description;
    public void setDescription(String newDescription)
    {
        description = newDescription;
    }

    private int id;
    public void setId(int newId)
    {
        id = newId;
    }
    public int getId()
    {
        return id;
    }
    
    private int count;
    public void setCount(int newCount)
    {
        count = newCount;
    }
        
    private int size;
    public void setSize(int newSize)
    {
        size = newSize;
    }
    
    public int getSize() {
        return size; 
    }
    
    public int getSizeAll() {
        return size*count;
    }
    
    public Goods() {
    }

    public Goods(String title, String picture, String description, int id, int count, int size) {
        this.title = title;
        this.picture = picture;
        this.description = description;
        this.id = id;
        this.count = count;
        this.size = size;
    }
    
    
    
   
    
    
}
