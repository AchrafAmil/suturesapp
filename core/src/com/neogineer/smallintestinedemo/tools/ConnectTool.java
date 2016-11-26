package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.neogineer.smallintestinedemo.organs.Openable;
import com.neogineer.smallintestinedemo.organs.OpenableSide;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.SmallIntestine;
import com.neogineer.smallintestinedemo.organs.SmallIntestineOrganPart;
import com.neogineer.smallintestinedemo.organs.SuturePoint;
import com.neogineer.smallintestinedemo.utils.Constants;

import java.util.List;

/**
 * Created by neogineer on 25/10/16.
 */
public class ConnectTool extends Tool {

    // RevoluteJoint allows rotation around the joint point, let's limit the rotation angle.
    private static final double MAX_ANGLE = 0 ;

    private static final float MAX_ACCEPTED_DISTANCE = 8f;

    ConnectToolHelper helper = new ConnectToolHelper();

    public ConnectTool(World world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX,screenY,pointer,button);
        if (hitBody != null) {
            if(helper.organA==null){
                helper.organA = (OrganPart) hitBody.getUserData();
                helper.anchorA = helper.organA.body.getLocalPoint(new Vector2(testPoint.x, testPoint.y));
            }else{
                helper.organB = (OrganPart) hitBody.getUserData();
                helper.anchorB = helper.organB.body.getLocalPoint(new Vector2(testPoint.x, testPoint.y));
                this.proceed();
            }
            return true;
        }

        return false;
    }

    public boolean proceed(){
        if( !( helper.checkSimilar()
                && helper.checkPossibility() && helper.checkConnectionIntents()) ){
            helper = new ConnectToolHelper();
            return false;
        }
        boolean ok = helper.proceed(true)!=null;
        helper = new ConnectToolHelper();
        return ok;
    }

    public class ConnectToolHelper{
        public OrganPart organA;
        public OrganPart organB;
        public Vector2 anchorA;
        public Vector2 anchorB;
        public RevoluteJointDef def;

        public boolean checkSimilar(){
            return organA!=organB;
        }

        public boolean checkDistance(){
            //todo (suggestion) use Utils.getDistance
            float distanceX = organA.body.getWorldPoint(anchorA).x - organB.body.getWorldPoint(anchorB).x;
            float distanceY = organA.body.getWorldPoint(anchorA).y - organB.body.getWorldPoint(anchorB).y;
            float distance = (float) Math.sqrt(distanceX*distanceX + distanceY*distanceY);
            Gdx.app.log("ConnectTool", "distance: "+distance);
            if(distance>MAX_ACCEPTED_DISTANCE*Constants.GLOBAL_SCALE)
                return false;
            return true;
        }

        public boolean checkPossibility(){
            //todo check possibilities matrix

            if((organA instanceof SmallIntestineOrganPart) && (organB instanceof SmallIntestineOrganPart)){
                if(Math.abs(((SmallIntestineOrganPart) organA).id - ((SmallIntestineOrganPart) organB).id)
                        < Constants.SMALL_INTESTINE_MIN_CONNECTION_ID_DIFFERENCE)
                    return false;
            }

            return true;
        }

        public boolean aWillGetVisible(){
            return organA.getZIndex()>=organB.getZIndex();
        }

        public boolean checkConnectionIntents(){
            return organA.connectionIntent(anchorA) && organB.connectionIntent(anchorB);
        }

        /**
         * this is called by the ConnectTool proceed method. This is the appropriate way to make a connection in response tu user's gesture.
         */
        public RevoluteJoint proceed(boolean visible){
            def = new RevoluteJointDef();
            def.bodyA = organA.body;
            def.bodyB = organB.body;
            def.collideConnected=true;
            def.localAnchorA.set(anchorA);
            def.localAnchorB.set(anchorB);
            def.enableLimit=false;
            def.lowerAngle= - (float) (MAX_ANGLE);
            def.upperAngle= (float) (MAX_ANGLE);

            organA.correctJointDef(def);
            organB.correctJointDef(def);

            //check distance after def has been updated
            anchorA=def.localAnchorA;
            anchorB=def.localAnchorB;
            if(!checkDistance())
                return null;

            RevoluteJoint joint = makeConnection(visible);

            // bad idea while suturing two small intestine edges
            /*if(organA instanceof Openable){
                for (OpenableSide side:
                     ((Openable) organA).getOpenableSides()) {
                    side.normal();
                }
            }
            if(organB instanceof Openable){
                for (OpenableSide side:
                        ((Openable) organB).getOpenableSides()) {
                    side.normal();
                }
            }*/

            return joint;
        }

        /**
         * usually called from proceed(), if we're connecting due to user's gesture.
         * but this can be directly called from organs' constructors.
         * @return the created joint. Never null.
         */
        public RevoluteJoint makeConnection(boolean visible){

            if(def==null){
                def = new RevoluteJointDef();
                def.bodyA = organA.body;
                def.bodyB = organB.body;
                def.collideConnected=true;
                def.localAnchorA.set(anchorA);
                def.localAnchorB.set(anchorB);
                def.enableLimit=true;
                def.lowerAngle= - (float) (MAX_ANGLE);
                def.upperAngle= (float) (MAX_ANGLE);
            }

            RevoluteJoint joint = (RevoluteJoint) world.createJoint(def);

            OrganPart visibleOrg = (aWillGetVisible())? organA:organB;
            OrganPart invisibleOrg = (!aWillGetVisible())? organA:organB;

            Vector2 visibleAnc = (aWillGetVisible())? anchorA:anchorB;
            Vector2 invisibleAnc = (!aWillGetVisible())? anchorA:anchorB;

            SuturePoint visibleSp = new SuturePoint();
            visibleSp.setLocalCoord(visibleAnc)
                    .setVisible(visible)
                    .setRelatedJoint(joint)
                    .setRotation(visibleOrg.getRotation());
            visibleOrg.addSuturePoint(visibleSp);

            SuturePoint invisibleSp = new SuturePoint();
            invisibleSp.setLocalCoord(invisibleAnc)
                    .setVisible(false)
                    .setRelatedJoint(joint)
                    .setRotation(invisibleOrg.getRotation());
            invisibleOrg.addSuturePoint(invisibleSp);


            return joint;
        }

    }

}
