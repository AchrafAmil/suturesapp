package com.suturesapp.workspace.organs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Polygon;

/**
 * Created by neogineer on 24/10/16.
 */
public class OpenableSide {

    Polygon polygon;
    Sprite staplesSprite;
    Sprite openingSprite;
    public State state;


    public OpenableSide( Sprite opening, Sprite staples, Polygon polygon){
        this.openingSprite = opening;
        this.staplesSprite = staples;
        this.polygon = polygon;
        state = State.NORMAL;
    }

    public OpenableSide( Sprite opening, Sprite staples, Polygon polygon, State state){
        this(opening, staples, polygon);
        this.state = state;
    }

    public void close(){
        setState(State.CLOSED);
    }

    public void open(){
        setState(State.OPEN);
    }

    public void normal(){
        setState(State.NORMAL);
    }

    public void setState(State state){
        this.state = state;
    }

    public State getState() {
        return state;
    }

    /**
     * @param x local coordinates relative to the organ holding this
     * @param y local coordinates relative to the organ holding this
     * @return true if the local point is within the openable side
     */
    public boolean contains(float x, float y){
        return this.polygon.contains(x,y);
    }

    public void draw(Batch batch){
        try {
            getSprite().draw(batch);
        }catch (NullPointerException npe){}
    }

    public Sprite getSprite(){
        switch (this.state){
            case NORMAL:
                return null;
            case OPEN:
                return openingSprite;
            case CLOSED:
                return staplesSprite;
            default:
                return null;
        }
    }

    public void free(){
        try{
            this.openingSprite.getTexture().dispose();
            this.staplesSprite.getTexture().dispose();
        }catch (NullPointerException npe){
        }
    }

    public enum State{
        OPEN, CLOSED, NORMAL
    }
}
