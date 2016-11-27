package com.neogineer.smallintestinedemo.organs.Esophagus;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 27/11/16.
 */
public class EsophagusOrganPart extends OrganPart {

    public EsophagusOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("EsophagusOrganPart.json");
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        // TODO: 27/11/16 implement highlighting
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        // TODO: 27/11/16 implement highlighting
        return false;
    }

}
