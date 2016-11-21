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
    private String picture;
    
    private String description;
    
    private int id;
    
    private int count;
    
        
    private int size;
    
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
