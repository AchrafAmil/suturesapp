package com.suturesapp.workspace.organs.abdominalwall;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.suturesapp.workspace.utils.Constants;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.organs.OrganPart;

import aurelienribon.bodyeditor.BodyEditorLoader;

/**
 * Created by neogineer on 26/01/17.
 */
public class AbdominalWallOrganPart extends OrganPart {


    public AbdominalWallOrganPart(World world, OrthographicCamera camera, Organ callback, String identifier, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, identifier, scale, position, rotation);
        setupBody("AbdominalWallOrganPart.json");
    }

    public AbdominalWallOrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef) {
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


    @Override
    public boolean connectionIntent(Vector2 point) {
        return true;
    }

    @Override
    public void correctJointDef(RevoluteJointDef def) {
        Vector2 vec = (this.body==def.bodyA)? def.localAnchorA:def.localAnchorB;
        if(vec.x>0.5)
            vec.set(pushToEdge(vec,false));
        else
            vec.set(pushToEdge(vec,true));
    }
}
