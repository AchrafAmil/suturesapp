package com.suturesapp.workspace.organs.liver;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 08/11/16.
 */
public class LiverOrganPart extends com.suturesapp.workspace.organs.OrganPart {

    public LiverOrganPart(World world, OrthographicCamera camera, com.suturesapp.workspace.organs.Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("LiverOrganPart.json");
    }

    public LiverOrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef) {
        super(opDef);
        setupBody("LiverOrganPart.json");
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        return this.highlighted = highlighted;
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        Texture texture = new Texture(Gdx.files.internal( loader.getImagePath("base_highlighted"+ identifier) ));
        highlightedBaseSprite = new Sprite(texture);
    }


    @Override
    public short getCategory() {
        return com.suturesapp.workspace.utils.Constants.CATEGORY_LIVER;
    }

    @Override
    public short getMask() {
        return com.suturesapp.workspace.utils.Constants.MASK_LIVER;
    }


}
