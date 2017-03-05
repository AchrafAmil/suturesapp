package com.suturesapp.smallintestinedemo.organs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.HashMap;

/**
 * Created by neogineer on 24/10/16.
 */
public abstract class Organ extends Group {

    OrthographicCamera camera;
    World mWorld;
    public HashMap<String, OrganPart> organParts = new HashMap<String, OrganPart>();
    public int size=0;

    public Organ(World world, OrthographicCamera camera) {
        this.mWorld = world;
        this.camera = camera;
    }

    public void saveState(Kryo kryo, Output output){
        for(OrganPart op: organParts.values()){
            kryo.writeObject(output, op);
        }
    }

    public abstract void loadState(Kryo kryo, Input input);

    /**
     * Empty from OrganPart-s.
     */
    public void free(){
        try {
            for(OrganPart op: organParts.values()){
                removeActor(op);
                op.destroy();
            }
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }

        organParts = new HashMap<>();
    }

    /**
     * highlight cutting areas, by calling highlightCutting() upon all children.
     * if the organ won't highlight anything, it won't do nothing else than returning false.
     * @return if it's been highlighted
     */
    public boolean highlightCutting(){
        boolean highlighted = false;
        for(OrganPart part: organParts.values()){
            highlighted = part.setHighlighted(true);
        }
        return highlighted;
    }

    public boolean unhighlightCutting(){
        boolean highlighted = false;
        for(OrganPart part: organParts.values()){
            highlighted = part.setHighlighted(false);
        }
        return !highlighted;
    }

    public abstract float getScale();

    public boolean loadBufferedOpenableSides(){
        for(OrganPart part : organParts.values()){
            part.loadBufferedOpenableSides();
        }
        return true;
    }
}
