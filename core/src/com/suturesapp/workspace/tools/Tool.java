package com.suturesapp.workspace.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.OrganPart;
import com.suturesapp.workspace.organs.rope.RopeOrganPart;
import com.suturesapp.workspace.utils.Constants;

/**
 * Created by neogineer on 25/10/16.
 */
public abstract class Tool extends InputAdapter {

    World world;
    OrthographicCamera camera;

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

    public Tool(World world, OrthographicCamera camera){
        this.world = world;
        this.camera = camera;
    }

    void extractHitBody(){
        hitBody = null;
        world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);

        if(Constants.VERBOSE_GESTURE){
            Gdx.app.log("GESTURE", "touched: (x:"+testPoint.x+",y:"+testPoint.y+")");
            if(hitBody!=null)
                Gdx.app.log("GESTURE", "Hit: "+hitBody.getUserData().getClass().getSimpleName()
                        +( (OrganPart) hitBody.getUserData() ).getIdentifier()
                        + ((hitBody.getUserData() instanceof RopeOrganPart)?
                        ( (RopeOrganPart) hitBody.getUserData() ).id:"")
                        +" at "+((OrganPart)hitBody.getUserData()).body.getLocalPoint(new Vector2(testPoint.x, testPoint.y)));
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // translate the mouse coordinates to world coordinates
        testPoint.set(screenX, screenY, 0);
        camera.unproject(testPoint);



        // ask the world which bodies are within the given
        // bounding box around the mouse pointer
        extractHitBody();

        //then let the specific child tool do what it got to do...
        return false;
    }

    public enum Tools{
        Cut, Close, Connect, Move, Trash
    }
}
