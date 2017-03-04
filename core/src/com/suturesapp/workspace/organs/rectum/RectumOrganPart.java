package com.suturesapp.workspace.organs.rectum;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.OrganPart;
import com.suturesapp.workspace.utils.Constants;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 26/01/17.
 */
public class RectumOrganPart extends OrganPart{


    public RectumOrganPart(World world, OrthographicCamera camera, com.suturesapp.workspace.organs.Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("RectumOrganPart.json");
    }

    public RectumOrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef) {
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
