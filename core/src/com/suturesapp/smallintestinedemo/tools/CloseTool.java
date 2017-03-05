package com.suturesapp.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.smallintestinedemo.organs.Openable;
import com.suturesapp.smallintestinedemo.organs.OpenableSide;

/**
 * Created by neogineer on 25/10/16.
 */
public class CloseTool extends Tool {
    public CloseTool(World world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        super.touchDown(screenX, screenY, pointer, button);

        if(hitBody!=null){
            if(hitBody.getUserData() instanceof Openable){
                OpenableSide side = ((Openable) hitBody.getUserData()).getOpenableSide(testPoint.x, testPoint.y);
                if(side==null)
                    return false;
                if(side.getState() == OpenableSide.State.OPEN){
                    side.close();
                    return true;
                }

            }
        }

        return false;
    }


}
