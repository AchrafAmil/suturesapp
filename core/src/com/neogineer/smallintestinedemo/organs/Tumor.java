package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.neogineer.smallintestinedemo.organs.rope.RopeOrganPart;

/**
 * Created by neogineer on 05/03/17.
 */
public class Tumor {

    Vector2 localCoord ;

    Sprite sprite;

    public Tumor(){
        Texture tx = new Texture(Gdx.files.internal("tumor.png"));
        this.sprite = new Sprite(tx);
        this.localCoord = new Vector2();

    }

    public Tumor(float x, float y){
        this();
        this.localCoord = new Vector2(x,y);

    }

    public Tumor setLocalCoord(int x, int y){
        this.localCoord.set(x, y);
        return this;
    }

    public void free(){
        try{
            this.sprite.getTexture().dispose();
        }catch (NullPointerException npe){
        }
    }
}
