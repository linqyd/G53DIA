/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.ac.nott.cs.g53dia.multidemo;

/**
 *
 * @author Sylvia
 */
public class AccessablePoint {
    private int x;
    private int y;
    private int absx;
    private int absy;

    public AccessablePoint(int x, int y) {
        this.x = x;
        this.y = y;
        this.absx = Math.abs(x);
        this.absy = Math.abs(y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    
    
    public int getX() {
        return this.x;
    }
    
    public void setX(int x) {
        this.x = x;
        this.absx = Math.abs(x);
    }

    public int getY() {
        return this.y;
    }

    
    public void setY(int y) {
        this.y = y;
        this.absy = Math.abs(y);
    }

    public int getAbsx() {
        return absx;
    }

    public int getAbsy() {
        return absy;
    }
    
    public int distance(AccessablePoint from)
    {
        return Math.max(Math.abs(x - from.x), Math.abs(y - from.y));
    }
    
    public boolean withinBlock(AccessablePoint from, AccessablePoint target)
    {
        boolean xWithinBlock = ((target.x <= from.x) && (target.x >= this.x) )||
                ((target.x <= this.x) && (target.x >= this.x));
        boolean yWithinBlock = ((target.y <= from.y) && (target.y >= this.y) )||
                ((target.y <= this.y) && (target.y >= this.y));                
        if(xWithinBlock && yWithinBlock)
            return true;
        
        return false;
    }
}

