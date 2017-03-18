package com.neogineer.smallintestinedemo.organs.abdominalconnector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;
import com.neogineer.smallintestinedemo.utils.Constants;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 17/03/17.
 */
public class AbdominalConnectorOrganPart extends OrganPart {


    public AbdominalConnectorOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("AbdominalConnectorOrganPart.json");
    }

    public AbdominalConnectorOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("AbdominalConnectorOrganPart.json");
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
        return Constants.CATEGORY_ABDOMINALCONNECTOR;
    }

    @Override
    public short getMask() {
        return Constants.MASK_ABDOMINALCONNECTOR;
    }



}
