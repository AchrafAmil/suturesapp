package com.neogineer.smallintestinedemo.organs.duedenum;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 26/11/16.
 */
public class DuodenumOrganPart extends OrganPart {


    public DuodenumOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("DuodenumOrganPart.json");
    }

    public DuodenumOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("DuodenumOrganPart.json");
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        // TODO: 26/11/16 implement highlighting
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        // TODO: 26/11/16 implement highlighting
        return false;
    }
}
