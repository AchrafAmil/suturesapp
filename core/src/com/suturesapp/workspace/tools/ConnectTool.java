package com.suturesapp.workspace.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.suturesapp.workspace.organs.OrganPart;
import com.suturesapp.workspace.organs.SuturePoint;
import com.suturesapp.workspace.organs.SuturePointDef;
import com.suturesapp.workspace.organs.abdominalconnector.AbdominalConnectorOrganPart;
import com.suturesapp.workspace.organs.abdominalwall.AbdominalWallOrganPart;
import com.suturesapp.workspace.organs.duedenum.DuodenumOrganPart;
import com.suturesapp.workspace.organs.pancreas.PancreasOrganPart;
import com.suturesapp.workspace.organs.rectum.RectumOrganPart;
import com.suturesapp.workspace.organs.rope.SmallIntestineOrganPart;
import com.suturesapp.workspace.utils.Constants;
import com.suturesapp.workspace.utils.Utils;
import com.suturesapp.workspace.organs.rope.ColonOrganPart;

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

    public ConnectTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera, groundBody);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX,screenY,pointer,button);
        if (hitBody != null) {
            if(helper.organA==null){
                helper.organA = (OrganPart) hitBody.getUserData();
                helper.anchorA = new Vector2()
                                .set(helper.organA.body.getLocalPoint(new Vector2(testPoint.x, testPoint.y)));
            }else{
                helper.organB = (OrganPart) hitBody.getUserData();
                helper.anchorB = new Vector2()
                                .set(helper.organB.body.getLocalPoint(new Vector2(testPoint.x, testPoint.y)));
                this.proceed();
            }
            return true;
        }

        return false;
    }

    public boolean proceed(){
        helper.correctSelectedOrganParts();
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
        private boolean resutureAgain = false;

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

            if((organA instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart) && (organB instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart)){
                if(
                        //if both are very edge (we are end to end suturing) tolerate and continue the suturing
                        !(((com.suturesapp.workspace.organs.rope.RopeOrganPart) organA).isVeryEdge() && ((com.suturesapp.workspace.organs.rope.RopeOrganPart) organB).isVeryEdge())
                    &&
                        (Math.abs(((com.suturesapp.workspace.organs.rope.RopeOrganPart) organA).id - ((com.suturesapp.workspace.organs.rope.RopeOrganPart) organB).id)
                        < Constants.ROPE_MIN_CONNECTION_ID_DIFFERENCE)
                        )
                    return false;
            }

            if(organA instanceof DuodenumOrganPart && organB instanceof DuodenumOrganPart)
                return false;

            return true;
        }

        public boolean aWillGetVisible(){
            return organA.getGlobalOrder()>=organB.getGlobalOrder();
        }

        public boolean checkConnectionIntents(){
            return organA.connectionIntent(anchorA) && organB.connectionIntent(anchorB);
        }

        public void correctSelectedOrganParts(){
            organA = correctSelectedOrganPart(organA);
            organB = correctSelectedOrganPart(organB);
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

            if(organA instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart && organB instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart)
                def.referenceAngle = (float) Utils.fitClosestWellKnowAngle(def.referenceAngle);

            Gdx.app.log("ConnectTool", "referenceAngle = "+Math.toDegrees(def.referenceAngle));

            organA.correctJointDef(def);
            organB.correctJointDef(def);

            //check distance after def has been updated
            anchorA.set(def.localAnchorA);
            anchorB.set(def.localAnchorB);
            if(!checkDistance())
                return null;

            // check SmallIntestine end-to-end suturing regions
            if((organA instanceof SmallIntestineOrganPart)
                    &&(organB instanceof SmallIntestineOrganPart)){
                //otherwise it's side-to-... : regions doesn't make sens.
                if(anchorA.y !=0 && anchorB.y!=0){
                    int regionA = Utils.getRegion(def.localAnchorA);
                    int regionB = Utils.getRegion(def.localAnchorB);

                    if(!Utils.regionsMatch(regionA, regionB)){
                        def.localAnchorB.set(((SmallIntestineOrganPart) organB).getSuturableRegion(regionA));
                        anchorB.set(def.localAnchorB);
                        anchorA.set(def.localAnchorA);
                    }
                    resutureAgain = ((SmallIntestineOrganPart) organA).hasAnOpenSide()
                            && ((SmallIntestineOrganPart) organB).hasAnOpenSide();
                }
            }

            RevoluteJoint joint = makeConnection(visible);

            if(resutureAgain && (organB instanceof SmallIntestineOrganPart)){
                anchorA = anchorA.cpy();
                anchorB = anchorB.cpy();
                anchorA.x*= -1;
                anchorB.set(((SmallIntestineOrganPart) organB).getSuturableRegion(Utils.getRegion(anchorA)));
                def = null;
                proceed(true);
            }

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

            this.organA = com.suturesapp.workspace.organs.OrgansHolder.organPartFromSpDef(spDef, true);
            this.organB = com.suturesapp.workspace.organs.OrgansHolder.organPartFromSpDef(spDef, false);

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

            OrganPart visibleOrg = (aWillGetVisible())? organA:organB;
            OrganPart invisibleOrg = (!aWillGetVisible())? organA:organB;

            Vector2 visibleAnc = (aWillGetVisible())? anchorA:anchorB;
            Vector2 invisibleAnc = (!aWillGetVisible())? anchorA:anchorB;

            SuturePoint visibleSp = new SuturePoint();
            visibleSp.setLocalCoord(visibleAnc)
                    .setVisible(visible)
                    .setRelatedJoint(joint)
                    .setTransparent((organA instanceof AbdominalWallOrganPart) || (organB instanceof AbdominalWallOrganPart))
                    .setTransparent(organA instanceof PancreasOrganPart || organB instanceof PancreasOrganPart)
                    .setRotation(visibleOrg.getRotation());
            visibleOrg.addSuturePoint(visibleSp);

            SuturePoint invisibleSp = new SuturePoint();
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


    public static double getMaxAngle(OrganPart organA, OrganPart organB){
        if(organA instanceof SmallIntestineOrganPart
                && organB instanceof SmallIntestineOrganPart)
            return SMALLINTESTINE_MAX_ANGLE;
        if(organA instanceof ColonOrganPart
                && organB instanceof ColonOrganPart)
            return COLON_MAX_ANGLE;
        if((organA instanceof ColonOrganPart && organB instanceof SmallIntestineOrganPart)
            || (organA instanceof SmallIntestineOrganPart && organB instanceof ColonOrganPart) )
            return SMALLINTESTINE_MAX_ANGLE;
        if(organA instanceof RectumOrganPart || organB instanceof RectumOrganPart)
            return SMALLINTESTINE_MAX_ANGLE;
        if(organA instanceof AbdominalConnectorOrganPart || organB instanceof AbdominalConnectorOrganPart)
            return ABDOMINALCONNECTOR_MAX_ANGLE;
        else
            return MAX_ANGLE;
    }

    /**
     * @param op
     * @return either op or the open edge beside (if op is neither veryMiddle nor veryEdge = it has an open Op just beside).
     */
    private static OrganPart correctSelectedOrganPart(OrganPart op){
        if(op instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart)
            if(!((com.suturesapp.workspace.organs.rope.RopeOrganPart) op).isVeryEdge() && !((com.suturesapp.workspace.organs.rope.RopeOrganPart) op).isVeryMiddle()){
                for (SuturePoint sp: op.getSuturePoints()) {
                    OrganPart theOtherOp = sp.getTheOtherOrganPart();
                    if(theOtherOp instanceof com.suturesapp.workspace.organs.rope.RopeOrganPart)
                        if(((com.suturesapp.workspace.organs.rope.RopeOrganPart) theOtherOp).isVeryEdge()){
                            op = theOtherOp;
                        }
                }
            }
        return op;
    }

    public static boolean collideConnected(OrganPart organA, OrganPart organB){
        return COLLIDE_CONNECTED || (organA instanceof RectumOrganPart && organB instanceof RectumOrganPart);
    }
}
