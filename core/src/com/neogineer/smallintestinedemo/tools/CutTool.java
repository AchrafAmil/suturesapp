package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Openable;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.SmallIntestineOrganPart;
import com.neogineer.smallintestinedemo.organs.SuturePoint;
import com.neogineer.smallintestinedemo.utils.Utils;

import java.util.List;

/**
 * Created by neogineer on 25/10/16.
 */
public class CutTool extends Tool {

    private static final float DISTANCE_LIMIT = 2.0f;
    private boolean cutDone = false;

    public CutTool(World world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(hitBody!=null){
            SuturePoint sp = getConcernedSuturePoint();
            if(sp==null)
                return false;
            return this.destroySuturePoint(sp);
        }else
            cutDone = false;

        return false;
    }

    private SuturePoint getConcernedSuturePoint(){

        OrganPart organPart = (OrganPart) hitBody.getUserData();

        //do not cut Small Intestine unless it's in the very middle (to avoid Small Intestine OP singleton).
        if(organPart instanceof SmallIntestineOrganPart)
            if(!((SmallIntestineOrganPart) organPart).isVeryMiddle())
                return null;


        List<SuturePoint> suturePoints = organPart.getSuturePoints();

        for (SuturePoint sp : suturePoints){
            Vector2 vec = hitBody.getWorldPoint(sp.getLocalCoord());

            float distance = Utils.getDistance(new Vector2(testPoint.x, testPoint.y), vec);
            if(distance<DISTANCE_LIMIT)
                return sp;
        }

        return null;
    }

    private boolean destroySuturePoint(SuturePoint sp){
        if(cutDone)
            return false;

        OrganPart organPart = (OrganPart) hitBody.getUserData();

        organPart.deleteSuturePoint(sp);

        sp.getTheOtherOrganPart().deleteSuturePoint(sp.getRelatedJoint());

        world.destroyJoint(sp.getRelatedJoint());

        //small intestine cutting should not destroy more than one suture point. So we're done.
        if(organPart instanceof SmallIntestineOrganPart)
            cutDone = true;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return this.touchDown(screenX, screenY, pointer, 0);
    }
}

