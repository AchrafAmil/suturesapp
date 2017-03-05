package com.suturesapp.smallintestinedemo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.suturesapp.smallintestinedemo.organs.rope.SmallIntestine;

/**
 * Created by neogineer on 25/10/16.
 *
 * DndTool = Drag'n'Drop tool.
 */

public class DndTool extends Tool implements GestureDetector.GestureListener {

    private MouseJoint mouseJoint = null;

    Vector2 target = new Vector2();


    float currentZoom;

    public DndTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera);
        this.currentZoom = camera.zoom;
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
            def.maxForce = 1000.0f * SmallIntestine.getLENGTH() * hitBody.getMass();
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

    /* GestureListener METHODS */

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        super.touchDown((int)x,(int)y,(int)deltaX,(int)deltaY);
        if(hitBody!=null || mouseJoint != null)
            return false;
        Gdx.app.log("pan", "x "+x+" y"+y+" deltaX "+deltaX+" deltaY "+y);

        camera.translate(-deltaX * currentZoom *0.025f,deltaY * currentZoom*0.025f);
        if(camera.position.x> com.suturesapp.smallintestinedemo.utils.Constants.CAMERA_X_LIMIT_RIGHT || camera.position.x< com.suturesapp.smallintestinedemo.utils.Constants.CAMERA_X_LIMIT_LEFT
                || camera.position.y> com.suturesapp.smallintestinedemo.utils.Constants.CAMERA_Y_LIMIT_TOP || camera.position.y< com.suturesapp.smallintestinedemo.utils.Constants.CAMERA_Y_LIMIT_BUTTOM)
            camera.translate(+deltaX * currentZoom *0.025f,-deltaY * currentZoom*0.025f);
        camera.update();
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("panStop", "x "+x+" y"+y);
        currentZoom = camera.zoom;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        Gdx.app.log("zoom", "from "+initialDistance+" to "+distance);
        camera.zoom = com.suturesapp.smallintestinedemo.utils.Utils.zoomToFitInterval((initialDistance / distance) * currentZoom);
        camera.update();
        return true;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

}
