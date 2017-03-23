package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.SuturePointDef;
import com.neogineer.smallintestinedemo.organs.OrgansHolder;
import com.neogineer.smallintestinedemo.organs.abdominalconnector.AbdominalConnector;
import com.neogineer.smallintestinedemo.organs.abdominalconnector.AbdominalConnectorOrganPart;
import com.neogineer.smallintestinedemo.organs.abdominalwall.AbdominalWallOrganPart;
import com.neogineer.smallintestinedemo.organs.rectum.Rectum;
import com.neogineer.smallintestinedemo.organs.rectum.RectumOrganPart;
import com.neogineer.smallintestinedemo.utils.Constants;

/**
 * Created by neogineer on 25/10/16.
 */
public class ConnectTool extends Tool {

    // RevoluteJoint allows rotation around the joint point, let's limit the rotation angle.
    public static final double MAX_ANGLE = 0 ;
    public static final double SMALLINTESTINE_MAX_ANGLE = Math.PI/24 ;
    public static final double ABDOMINALCONNECTOR_MAX_ANGLE = Math.PI*2 ;
    public static final double COLON_MAX_ANGLE = Math.PI/30 ;

    public static final float MAX_ACCEPTED_DISTANCE = 1.35f;

    public static final boolean COLLIDE_CONNECTED= false;

    ConnectToolHelper helper = new ConnectToolHelper();

    public ConnectTool(World world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX,screenY,pointer,button);
        if (hitBody != null) {
            if(helper.organA==null){
                helper.organA = (com.neogineer.smallintestinedemo.organs.OrganPart) hitBody.getUserData();
                helper.anchorA = helper.organA.body.getLocalPoint(new Vector2(testPoint.x, testPoint.y));
            }else{
                helper.organB = (com.neogineer.smallintestinedemo.organs.OrganPart) hitBody.getUserData();
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
        public com.neogineer.smallintestinedemo.organs.OrganPart organA;
        public com.neogineer.smallintestinedemo.organs.OrganPart organB;
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

            if((organA instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart) && (organB instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart)){
                if(Math.abs(((com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart) organA).id - ((com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart) organB).id)
                        < Constants.SMALL_INTESTINE_MIN_CONNECTION_ID_DIFFERENCE)
                    return false;
            }

            if((organA instanceof com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart) && (organB instanceof com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart)){
                if(Math.abs(((com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart) organA).id - ((com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart) organB).id)
                        < Constants.SMALL_INTESTINE_MIN_CONNECTION_ID_DIFFERENCE)
                    return false;
            }

            if(organA instanceof com.neogineer.smallintestinedemo.organs.duedenum.DuodenumOrganPart && organB instanceof com.neogineer.smallintestinedemo.organs.duedenum.DuodenumOrganPart)
                return false;

            return true;
        }

        public boolean aWillGetVisible(){
            return organA.getGlobalOrder()>=organB.getGlobalOrder();
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
            def.collideConnected=collideConnected(organA,organB);
            def.localAnchorA.set(anchorA);
            def.localAnchorB.set(anchorB);
            def.enableLimit=true;
            def.lowerAngle= - (float) (getMaxAngle(organA, organB));
            def.upperAngle= (float) (getMaxAngle(organA, organB));
            def.referenceAngle = def.bodyB.getAngle() - def.bodyA.getAngle();

            if(organA instanceof com.neogineer.smallintestinedemo.organs.rope.RopeOrganPart && organB instanceof com.neogineer.smallintestinedemo.organs.rope.RopeOrganPart)
                def.referenceAngle = (float) com.neogineer.smallintestinedemo.utils.Utils.fitClosestWellKnowAngle(def.referenceAngle);

            Gdx.app.log("ConnectTool", "referenceAngle = "+Math.toDegrees(def.referenceAngle));

            organA.correctJointDef(def);
            organB.correctJointDef(def);

            //check distance after def has been updated
            anchorA=def.localAnchorA;
            anchorB=def.localAnchorB;
            if(!checkDistance())
                return null;

            // check SmallIntestine end-to-end suturing regions
            if((organA instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart)
                    &&(organB instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart)){
                //otherwise it's side-to-... : regions doesn't make sens.
                if(anchorA.y !=0 && anchorB.y!=0){
                    int regionA = com.neogineer.smallintestinedemo.utils.Utils.getRegion(def.localAnchorA);
                    int regionB = com.neogineer.smallintestinedemo.utils.Utils.getRegion(def.localAnchorB);

                    if(!com.neogineer.smallintestinedemo.utils.Utils.regionsMatch(regionA, regionB))
                        return null;
                }
            }

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
         * This is used to re-created joints after re-load.
         *
         * @param spDef stored SuturePointDef
         * @return created joint (returned from this.makeConnection()
         */
        public RevoluteJoint proceed(SuturePointDef spDef){

            this.organA = OrgansHolder.organPartFromSpDef(spDef, true);
            this.organB = OrgansHolder.organPartFromSpDef(spDef, false);

            if(spDef.getOrganA().contains("SmallIntestine"))
                new Integer(42);

            this.anchorA = spDef.getAnchorA();
            this.anchorB = spDef.getAnchorB();

            def = new RevoluteJointDef();
            def.bodyA = organA.body;
            def.bodyB = organB.body;
            def.collideConnected=collideConnected(organA,organB);
            def.localAnchorA.set(anchorA);
            def.localAnchorB.set(anchorB);
            def.enableLimit=true;
            def.lowerAngle= - (float) (getMaxAngle(organA, organB));
            def.upperAngle= (float) (getMaxAngle(organA, organB));
            if(organA instanceof AbdominalConnectorOrganPart || organB instanceof AbdominalConnectorOrganPart)
                def.referenceAngle = def.bodyB.getAngle() - def.bodyA.getAngle();

            return makeConnection(spDef.isAvisible() || spDef.isBvisible());
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
                def.collideConnected=collideConnected(organA,organB);
                def.localAnchorA.set(anchorA);
                def.localAnchorB.set(anchorB);
                def.enableLimit=true;
                def.lowerAngle= - (float) (getMaxAngle(organA, organB));
                def.upperAngle= (float) (getMaxAngle(organA, organB));
                def.referenceAngle = def.bodyB.getAngle() - def.bodyA.getAngle();
            }

            RevoluteJoint joint = (RevoluteJoint) world.createJoint(def);

            com.neogineer.smallintestinedemo.organs.OrganPart visibleOrg = (aWillGetVisible())? organA:organB;
            com.neogineer.smallintestinedemo.organs.OrganPart invisibleOrg = (!aWillGetVisible())? organA:organB;

            Vector2 visibleAnc = (aWillGetVisible())? anchorA:anchorB;
            Vector2 invisibleAnc = (!aWillGetVisible())? anchorA:anchorB;

            com.neogineer.smallintestinedemo.organs.SuturePoint visibleSp = new com.neogineer.smallintestinedemo.organs.SuturePoint();
            visibleSp.setLocalCoord(visibleAnc)
                    .setVisible(visible)
                    .setRelatedJoint(joint)
                    .setBackgrounded((organA instanceof AbdominalWallOrganPart) || (organB instanceof AbdominalWallOrganPart))
                    .setRotation(visibleOrg.getRotation());
            visibleOrg.addSuturePoint(visibleSp);

            com.neogineer.smallintestinedemo.organs.SuturePoint invisibleSp = new com.neogineer.smallintestinedemo.organs.SuturePoint();
            invisibleSp.setLocalCoord(invisibleAnc)
                    .setVisible(false)
                    .setRelatedJoint(joint)
                    .setRotation(invisibleOrg.getRotation());
            invisibleOrg.addSuturePoint(invisibleSp);

            // always put organA's suturepoint
            joint.setUserData( (aWillGetVisible())? visibleSp:invisibleSp);

            return joint;
        }

    }


    public static double getMaxAngle(com.neogineer.smallintestinedemo.organs.OrganPart organA, com.neogineer.smallintestinedemo.organs.OrganPart organB){
        if(organA instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart
                && organB instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart)
            return SMALLINTESTINE_MAX_ANGLE;
        if(organA instanceof com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart
                && organB instanceof com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart)
            return COLON_MAX_ANGLE;
        if((organA instanceof com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart && organB instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart)
            || (organA instanceof com.neogineer.smallintestinedemo.organs.rope.SmallIntestineOrganPart && organB instanceof com.neogineer.smallintestinedemo.organs.rope.ColonOrganPart) )
            return SMALLINTESTINE_MAX_ANGLE;
        if(organA instanceof RectumOrganPart || organB instanceof RectumOrganPart)
            return SMALLINTESTINE_MAX_ANGLE;
        if(organA instanceof AbdominalConnectorOrganPart || organB instanceof AbdominalConnectorOrganPart)
            return ABDOMINALCONNECTOR_MAX_ANGLE;
        else
            return MAX_ANGLE;
    }

    public static boolean collideConnected(OrganPart organA, OrganPart organB){
        return COLLIDE_CONNECTED || (organA instanceof RectumOrganPart && organB instanceof RectumOrganPart);
    }
}
