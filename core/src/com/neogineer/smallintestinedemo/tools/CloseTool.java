package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Openable;
import com.neogineer.smallintestinedemo.organs.OpenableSide;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.rope.RopeOrganPart;

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
            OrganPart op = (OrganPart) hitBody.getUserData();
            if(op instanceof Openable){
                op.close(testPoint.x,testPoint.y);
//                if(op instanceof RopeOrganPart){
//                    RopeOrganPart ropeOp = (RopeOrganPart) op;
//                    try {
//                        ropeOp.getTheOpenOs().close();
//                    }catch (NullPointerException e){
//                        try {
//                            ((RopeOrganPart)ropeOp.getSuturePoints().get(0).getTheOtherOrganPart()).getTheOpenOs().close();
//                        }catch (NullPointerException | ClassCastException ee){
//                            try {
//                                ((RopeOrganPart)ropeOp.getSuturePoints().get(1).getTheOtherOrganPart()).getTheOpenOs().close();
//                            }catch (NullPointerException | ClassCastException eee){
//                                return false;
//                            }
//                        }
//                    }
//                    return true;
//                }
//
//                OpenableSide side = ((Openable) hitBody.getUserData()).getOpenableSide(testPoint.x, testPoint.y);
//                if(side==null)
//                    return false;
//                if(side.getState() == OpenableSide.State.OPEN){
//                    side.close();
//                    return true;
//                }
            }
        }

        return false;
    }


}
