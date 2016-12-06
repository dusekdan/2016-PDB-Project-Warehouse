/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.vutbr.fit.pdb.teamincredibles.model;

/**
 *
 * @author Sucha
 */
public class Stock {

    
    // size
    private int height;
    private int width;
    
    // volume
    private int capacity;
    
    
    // position of middle point?
    private int posX;
    private int posY;
    
    
    public Stock() {
        
    }

    public Stock(int height, int width, int capacity, int posX, int posY) {
        this.height = height;
        this.width = width;
        this.capacity = capacity;
        this.posX = posX;
        this.posY = posY;
    }
    
       
    
    
    
}
