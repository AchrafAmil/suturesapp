package com.neogineer.smallintestinedemo.organs.rectum;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;
import com.neogineer.smallintestinedemo.utils.Constants;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 26/01/17.
 */
public class RectumOrganPart extends OrganPart{


    public RectumOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("RectumOrganPart.json");
    }

    public RectumOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("RectumOrganPart.json");
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        // TODO: 15/01/17 implement highlighting
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        // TODO: 15/01/17 implement highlighting
        return false;
    }

    @Override
    public short getCategory() {
        return Constants.CATEGORY_RECTUM;
    }

    @Override
    public short getMask() {
        return Constants.MASK_RECTUM;
    }

}
