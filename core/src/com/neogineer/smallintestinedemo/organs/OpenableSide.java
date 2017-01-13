package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by neogineer on 24/10/16.
 */
public class OpenableSide {

    Polygon polygon;
    Sprite staplesSprite;
    Sprite openingSprite;
    State state;


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

    private void debugPolygon(Batch batch){
        Texture tx = new Texture(Gdx.files.internal("redpixel.png"));
        //Sprite sp = new Sprite(tx);
        for(float i = -2; i<2; i+=0.01){
            for(float j = -2; j<2; j+=0.01){
                if(this.contains(i,j)){
                    batch.draw(tx,(2+i)*16, (2+j)*13f );
                }
            }
        }
    }

    public enum State{
        OPEN, CLOSED, NORMAL
    }
}
