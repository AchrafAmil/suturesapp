package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.HashMap;

/**
 * Created by neogineer on 24/10/16.
 */
public abstract class Organ extends Group {

    OrthographicCamera camera;
    World mWorld;
    HashMap<String, OrganPart> organParts = new HashMap<String, OrganPart>();
    int size=0;

    public Organ(World world, OrthographicCamera camera) {
        this.mWorld = world;
        this.camera = camera;
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
}
