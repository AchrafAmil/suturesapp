package com.suturesapp.workspace.organs.esophagus;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.organs.OrganPart;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 27/11/16.
 */
public class EsophagusOrganPart extends OrganPart {

    public EsophagusOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("EsophagusOrganPart.json");
    }

    public EsophagusOrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef) {
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
        return com.suturesapp.workspace.utils.Constants.CATEGORY_ESOPHAGUS;
    }

    @Override
    public short getMask() {
        return com.suturesapp.workspace.utils.Constants.MASK_ESOPHAGUS;
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
