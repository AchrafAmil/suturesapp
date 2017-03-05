package com.neogineer.smallintestinedemo.organs.abdominalwall;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;
import com.neogineer.smallintestinedemo.utils.Constants;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 26/01/17.
 */
public class AbdominalWallOrganPart extends com.neogineer.smallintestinedemo.organs.OrganPart {


    public AbdominalWallOrganPart(World world, OrthographicCamera camera, com.neogineer.smallintestinedemo.organs.Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("AbdominalWallOrganPart.json");
    }

    public AbdominalWallOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("AbdominalWallOrganPart.json");
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        return false;
    }


    @Override
    public short getCategory() {
        return Constants.CATEGORY_ABDOMINALWALL;
    }

    @Override
    public short getMask() {
        return Constants.MASK_ABDOMINALWALL;
    }
}
