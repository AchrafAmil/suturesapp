package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.SuturePoint;
import com.neogineer.smallintestinedemo.organs.abdominalconnector.AbdominalConnectorOrganPart;
import com.neogineer.smallintestinedemo.organs.rope.RopeOrganPart;
import com.neogineer.smallintestinedemo.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neogineer on 25/10/16.
 */
public class CutTool extends Tool {

    private static final float DISTANCE_LIMIT = 1.2f;
    private boolean cutDone = false;

    public CutTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera, groundBody);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        if(hitBody!=null){
            OrganPart op = (OrganPart) hitBody.getUserData();
            if( op instanceof AbdominalConnectorOrganPart){
                op.destroyAllSuturePoints();
                op.body.setTransform(1000,1000,0);
                return true;/*
                ArrayList<SuturePoint> sps = op.getSuturePoints();
                int size = sps.size();
                for(int i=0; i<sps.size(); i++){
                    this.destroySuturePoint(sps.get(i), op);
                }
                op.body.setTransform(1000,1000,0);
//                for(int i=0; i<op.getSuturePoints().size();i++){
//                    this.destroySuturePoint(op.getSuturePoints().get(i), op);
//                }
//                Organ organ = op.organCallback;
//                organ.organParts.values().remove(op);
//                organ.removeActor(op);
//                op.destroy();
                return true;*/
            }
            SuturePoint sp = getConcernedSuturePoint();
            if(sp==null)
                return false;
            return this.destroySuturePoint(sp, (OrganPart) hitBody.getUserData());
        }else
            cutDone = false;

        return false;
    }

    private SuturePoint getConcernedSuturePoint(){

        OrganPart organPart = (OrganPart) hitBody.getUserData();

        //do not cut Small Intestine unless it's in the very middle (to avoid Small Intestine OP singleton).
        if(organPart instanceof RopeOrganPart)
            if(!((RopeOrganPart) organPart).isVeryMiddle())
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

    private boolean destroySuturePoint(SuturePoint sp, OrganPart organPart){
        if(cutDone)
            return false;

        organPart.deleteSuturePoint(sp);

        sp.getTheOtherOrganPart().deleteSuturePoint(sp.getRelatedJoint());

        world.destroyJoint(sp.getRelatedJoint());

        //small intestine cutting should not destroy more than one suture point. So we're done.
        if(organPart instanceof RopeOrganPart)
            cutDone = true;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return this.touchDown(screenX, screenY, pointer, 0);
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean bodyMoving() throws NullPointerException {
        //disable body moving for cut tool.
        return false;
    }
}

