package com.suturesapp.workspace.organs.spleen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.organs.OrganPart;
import com.suturesapp.workspace.organs.OrganPartDefinition;
import com.suturesapp.workspace.utils.Constants;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 17/03/17.
 */
public class SpleenOrganPart extends OrganPart {


    public SpleenOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("SpleenOrganPart.json");
    }

    public SpleenOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("SpleenOrganPart.json");
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
        return Constants.CATEGORY_SPLEEN;
    }

    @Override
    public short getMask() {
        return Constants.MASK_SPLEEN;
    }
}
