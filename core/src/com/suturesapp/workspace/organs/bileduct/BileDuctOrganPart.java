package com.suturesapp.workspace.organs.bileduct;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.suturesapp.workspace.organs.Organ;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 15/03/17.
 */
public class BileDuctOrganPart extends com.suturesapp.workspace.organs.OrganPart {

    public BileDuctOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("BileDuctOrganPart.json");
    }

    public BileDuctOrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef) {
        super(opDef);
        setupBody("BileDuctOrganPart.json");
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        Texture texture = new Texture(Gdx.files.internal( loader.getImagePath("base_highlighted"+ identifier) ));
        highlightedBaseSprite = new Sprite(texture);
    }
    @Override
    public boolean setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
        return true;
    }


    @Override
    public short getCategory() {
        return com.suturesapp.workspace.utils.Constants.CATEGORY_BILEDUCT;
    }

    @Override
    public short getMask() {
        return com.suturesapp.workspace.utils.Constants.MASK_BILEDUCT;
    }


    @Override
    public boolean connectionIntent(Vector2 point) {
        return true;
    }

    @Override
    public void correctJointDef(RevoluteJointDef def) {
        Vector2 vec = (this.body==def.bodyA)? def.localAnchorA:def.localAnchorB;
        vec.set(pushToEdge(vec));
    }

    @Override
    public boolean addSuturePoint(com.suturesapp.workspace.organs.SuturePoint sp) {
        if(sp.isVisible())
            sp.setTransparent(true);
        return super.addSuturePoint(sp);
    }
}
