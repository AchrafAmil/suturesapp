package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.neogineer.smallintestinedemo.organs.rope.SmallIntestine;

/**
 * Created by neogineer on 25/10/16.
 *
 * DndTool = Drag'n'Drop tool.
 */

public class DndTool extends Tool {

    private MouseJoint mouseJoint = null;

    Vector2 target = new Vector2();

    public DndTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera);
        this.groundBody = groundBody;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        // if we hit something we create a new mouse joint
        // and attach it to the hit body.
        if (hitBody != null) {
            if(!hitBody.getType().equals(BodyDef.BodyType.DynamicBody))
                return false;
            MouseJointDef def = new MouseJointDef();
            def.bodyA = groundBody;
            def.bodyB = hitBody;
            def.collideConnected = true;
            def.target.set(testPoint.x, testPoint.y);
            def.maxForce = 1000.0f * SmallIntestine.LENGTH * hitBody.getMass();
            mouseJoint = (MouseJoint)world.createJoint(def);

            return true;
        }

        return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        // if a mouse joint exists we simply update
        // the target of the joint based on the new
        // mouse coordinates
        if (mouseJoint != null) {
            camera.unproject(testPoint.set(x, y, 0));
            mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
        }
        return false;
    }

    @Override
    public boolean touchUp (int x, int y, int pointer, int button) {
        // if a mouse joint exists we simply destroy it
        if (mouseJoint != null) {
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
        }
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body body:
                bodies){
            body.setLinearVelocity(0,0);
            body.setAngularVelocity(0);
        }

        return false;
    }

}
