package com.neogineer.smallintestinedemo.organs.stomach;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 19/11/16.
 */
public class StomachOrganPart extends OrganPart {

    public StomachOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("StomachOrganPart.json");
    }

    public StomachOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("StomachOrganPart.json");
    }

    @Override
    protected void loadHighlightedSprite(BodyEditorLoader loader, String identifier) {
        // TODO: 19/11/16 implement highlighting
    }

    @Override
    public boolean setHighlighted(boolean highlighted) {
        // TODO: 19/11/16 implement highlighting
        return false;
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
}
