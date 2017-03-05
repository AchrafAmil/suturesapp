package com.neogineer.smallintestinedemo.organs.esophagus;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;
import com.neogineer.smallintestinedemo.utils.Constants;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 27/11/16.
 */
public class EsophagusOrganPart extends com.neogineer.smallintestinedemo.organs.OrganPart {

    public EsophagusOrganPart(World world, OrthographicCamera camera, com.neogineer.smallintestinedemo.organs.Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("EsophagusOrganPart.json");
    }

    public EsophagusOrganPart(OrganPartDefinition opDef) {
        super(opDef);
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


    @Override
    public short getCategory() {
        return Constants.CATEGORY_ESOPHAGUS;
    }

    @Override
    public short getMask() {
        return Constants.MASK_ESOPHAGUS;
    }

}
