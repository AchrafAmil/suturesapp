package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.rope.RopeOrganPart;
import com.neogineer.smallintestinedemo.organs.rope.SmallIntestine;
import com.neogineer.smallintestinedemo.utils.Constants;
import com.neogineer.smallintestinedemo.utils.Utils;

/**
 * Created by neogineer on 25/10/16.
 */
public abstract class Tool extends InputAdapter implements GestureDetector.GestureListener {

    World world;
    OrthographicCamera camera;

    private MouseJoint mouseJoint = null;

    Vector2 target = new Vector2();
    float currentZoom;

    Body hitBody = null;
    Body groundBody = null;
    Vector3 testPoint = new Vector3();
    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture (Fixture fixture) {
            // if the hit fixture's body is the ground body
            // we ignore it
            if (fixture.getBody() == groundBody)
                return true;

            // if the hit point is inside the fixture of the body
            // we report it
            if (fixture.testPoint(testPoint.x, testPoint.y)) {
                hitBody = fixture.getBody();
                return false;
            } else
                return true;
        }
    };

    public Tool(World world, OrthographicCamera camera, Body groundBody){
        this.world = world;
        this.camera = camera;
        this.currentZoom = camera.zoom;
        this.groundBody = groundBody;
    }

    void extractHitBody(){
        hitBody = null;
        world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);

        if(Constants.VERBOSE_GESTURE){
            Gdx.app.log("extractHitBody", "from: (x:"+testPoint.x+",y:"+testPoint.y+")");
            if(hitBody!=null)
                Gdx.app.log("extractHitBody", "Hit: "+hitBody.getUserData().getClass().getSimpleName()
                        +( (com.neogineer.smallintestinedemo.organs.OrganPart) hitBody.getUserData() ).getIdentifier()
                        + ((hitBody.getUserData() instanceof RopeOrganPart)?
                        ( (RopeOrganPart) hitBody.getUserData() ).id:"")
                        +" at "+((OrganPart)hitBody.getUserData()).body.getLocalPoint(new Vector2(testPoint.x, testPoint.y))
                        +" equivalent to json coordinates: "
                        +((OrganPart)hitBody.getUserData()).jsonCoordinatesFromVertex(
                        ((OrganPart)hitBody.getUserData()).body.getLocalPoint(new Vector2(testPoint.x, testPoint.y))
                ).toString());
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Gdx.app.log("Tool", "touchDown");
        // translate the mouse coordinates to world coordinates
        testPoint.set(screenX, screenY, 0);
        camera.unproject(testPoint);
        // ask the world which bodies are within the given
        // bounding box around the mouse pointer
        extractHitBody();


        try {
            return bodyMoving();
        }catch (NullPointerException npe){
            return false;
        }

        //then let the specific child tool do what it got to do...
    }

    /**
     * Override this to disable Drag'n'drop
     */
    public boolean bodyMoving() throws NullPointerException{
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
        }else
            return false;
    }

    @Override
    public boolean touchDragged (int x, int y, int pointer) {
        // if a mouse joint exists we simply update
        // the target of the joint based on the new
        // mouse coordinates
        if (mouseJoint != null) {
            Gdx.app.log("Tool", "touchDragged set mouseJoint target");
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

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        Gdx.app.log("Tool", "tap");
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        Gdx.app.log("Tool", "fling");
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        Gdx.app.log("Tool", "pan x "+x+" y"+y+" deltaX "+deltaX+" deltaY "+y);
        if(hitBody==null){
            camera.translate(-deltaX * currentZoom *0.025f,deltaY * currentZoom*0.025f);
            if(camera.position.x> com.neogineer.smallintestinedemo.utils.Constants.CAMERA_X_LIMIT_RIGHT || camera.position.x< com.neogineer.smallintestinedemo.utils.Constants.CAMERA_X_LIMIT_LEFT
                    || camera.position.y> com.neogineer.smallintestinedemo.utils.Constants.CAMERA_Y_LIMIT_TOP || camera.position.y< com.neogineer.smallintestinedemo.utils.Constants.CAMERA_Y_LIMIT_BUTTOM)
                camera.translate(+deltaX * currentZoom *0.025f,-deltaY * currentZoom*0.025f);
            camera.update();
            return true;
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        Gdx.app.log("Tool", "panStop x "+x+" y"+y);
        currentZoom = camera.zoom;
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        Gdx.app.log("zoom", "from "+initialDistance+" to "+distance);
        camera.zoom = Utils.zoomToFitInterval((initialDistance / distance) * currentZoom);
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

    public enum Tools{
        Cut, Close, Connect, Move, Trash, Tumor
    }
}
