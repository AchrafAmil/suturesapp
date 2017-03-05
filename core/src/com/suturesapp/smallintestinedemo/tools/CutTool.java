package com.suturesapp.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import java.util.List;

/**
 * Created by neogineer on 25/10/16.
 */
public class CutTool extends Tool {

    private static final float DISTANCE_LIMIT = 1.2f;
    private boolean cutDone = false;

    public CutTool(World world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(hitBody!=null){
            com.suturesapp.smallintestinedemo.organs.SuturePoint sp = getConcernedSuturePoint();
            if(sp==null)
                return false;
            return this.destroySuturePoint(sp);
        }else
            cutDone = false;

        return false;
    }

    private com.suturesapp.smallintestinedemo.organs.SuturePoint getConcernedSuturePoint(){

        com.suturesapp.smallintestinedemo.organs.OrganPart organPart = (com.suturesapp.smallintestinedemo.organs.OrganPart) hitBody.getUserData();

        //do not cut Small Intestine unless it's in the very middle (to avoid Small Intestine OP singleton).
        if(organPart instanceof com.suturesapp.smallintestinedemo.organs.rope.RopeOrganPart)
            if(!((com.suturesapp.smallintestinedemo.organs.rope.RopeOrganPart) organPart).isVeryMiddle())
                return null;


        List<com.suturesapp.smallintestinedemo.organs.SuturePoint> suturePoints = organPart.getSuturePoints();

        for (com.suturesapp.smallintestinedemo.organs.SuturePoint sp : suturePoints){
            Vector2 vec = hitBody.getWorldPoint(sp.getLocalCoord());

            float distance = com.suturesapp.smallintestinedemo.utils.Utils.getDistance(new Vector2(testPoint.x, testPoint.y), vec);
            if(distance<DISTANCE_LIMIT)
                return sp;
        }

        return null;
    }

    private boolean destroySuturePoint(com.suturesapp.smallintestinedemo.organs.SuturePoint sp){
        if(cutDone)
            return false;

        com.suturesapp.smallintestinedemo.organs.OrganPart organPart = (com.suturesapp.smallintestinedemo.organs.OrganPart) hitBody.getUserData();

        organPart.deleteSuturePoint(sp);

        sp.getTheOtherOrganPart().deleteSuturePoint(sp.getRelatedJoint());

        world.destroyJoint(sp.getRelatedJoint());

        //small intestine cutting should not destroy more than one suture point. So we're done.
        if(organPart instanceof com.suturesapp.smallintestinedemo.organs.rope.SmallIntestineOrganPart)
            cutDone = true;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return this.touchDown(screenX, screenY, pointer, 0);
    }
}

