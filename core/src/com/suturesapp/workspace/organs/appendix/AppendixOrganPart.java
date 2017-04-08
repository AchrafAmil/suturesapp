package com.suturesapp.workspace.organs.appendix;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.OrganPartDefinition;
import com.suturesapp.workspace.utils.Constants;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.organs.OrganPart;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 15/01/17.
 */
public class AppendixOrganPart extends OrganPart {


    public AppendixOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("AppendixOrganPart.json");
    }

    public AppendixOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("AppendixOrganPart.json");
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
        return Constants.CATEGORY_APPENDIX;
    }

    @Override
    public short getMask() {
        return Constants.MASK_APPENDIX;
    }

}
