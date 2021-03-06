package com.suturesapp.workspace.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by neogineer on 25/10/16.
 */
public class CloseTool extends Tool {
    public CloseTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera, groundBody);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        super.touchDown(screenX, screenY, pointer, button);

        if(hitBody!=null){
            com.suturesapp.workspace.organs.OrganPart op = (com.suturesapp.workspace.organs.OrganPart) hitBody.getUserData();
            if(op instanceof com.suturesapp.workspace.organs.Openable){
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
