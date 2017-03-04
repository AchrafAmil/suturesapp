package com.suturesapp.workspace.organs.rope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.suturesapp.workspace.tools.ConnectTool;
import com.suturesapp.workspace.utils.Constants;

/**
 * Created by neogineer on 27/10/16.
 */
public class SmallIntestine extends com.suturesapp.workspace.organs.Organ {

    // number of SmallIntestineOrganPart instances
    private static final int LENGTH = Constants.SMALLINTESTINE_LENGTH ;

    private static final float SCALE = Constants.SMALLINTESTINE_SCALE;

    // how much the joint point is offset from the center (percentage from body dimensions) (0.5f means it's on the edge)
    private static final float JOINT_OFFSET_PERCENT = 0.35f;

    /**
     * Please make sure startPos is on <b> LEFT </b> of the endPos.
     */
    public SmallIntestine(World world, OrthographicCamera camera, Vector2 startPos, Vector2 endPos){
        super(world, camera);

        float slope = (endPos.y - startPos.y) / (endPos.x - startPos.x);
        double alpha = Math.atan(slope);

        SmallIntestineOrganPart firstActor
                = new SmallIntestineOrganPart(world, camera, this, 0, getSCALE(), startPos, (float) (alpha- Math.PI/2) );
        addActor(firstActor);
        this.organParts.put(""+ size++, firstActor );
        Body body = firstActor.body;

        Body link = body ;


        final float JOINT_OFFSET = firstActor
                .getVertex(0,firstActor.origin.cpy().y + firstActor.origin.cpy().y * 2 * getJointOffsetPercent()).y;



        final Vector2 step = new Vector2(JOINT_OFFSET*2*(float)Math.cos(alpha), JOINT_OFFSET*2*(float)Math.sin(alpha));

        for(int i = 0; i< getLENGTH(); i++){

            SmallIntestineOrganPart actor;
            actor = new SmallIntestineOrganPart(world, camera, this, i+1, getSCALE(),
                        new Vector2( startPos.x+step.x*i, startPos.y +step.y*i ), (float) (alpha- Math.PI/2));
            addActor(actor);
            this.organParts.put(""+ size++, actor );
            body = actor.body;

            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            // TODO: 18/11/16 review the offset,
            // TODO:          get it from the organ part so that it takes in consideration the vertices scaling and stuff
            connector.organA = (com.suturesapp.workspace.organs.OrganPart) link.getUserData();
            connector.anchorA = new Vector2(0, JOINT_OFFSET);
            connector.organB = (com.suturesapp.workspace.organs.OrganPart) body.getUserData();
            connector.anchorB = new Vector2(0, -JOINT_OFFSET);

            //just for connectTool debug
            //connector.proceed(true);

            connector.makeConnection(false);

            link = body;
        }

        //((SmallIntestineOrganPart) link.getUserData()).getOpenableSides().get(0).open();
    }

    /**
     * with predefined start/end positions
     */
    public SmallIntestine(World world, OrthographicCamera camera) {
        this(world, camera, Constants.SMALLINTESTINE_LEFT_POSITION, Constants.SMALLINTESTINE_RIGHT_POSITION);
    }

    public static int getLENGTH() {
        return LENGTH;
    }

    public static float getSCALE() {
        return SCALE;
    }

    public static float getJointOffsetPercent() {
        return JOINT_OFFSET_PERCENT;
    }

    @Override
    public void loadState(Kryo kryo, Input input){
        this.free();
        size=0;

        for(int i = 0; i<= getLENGTH(); i++){
            com.suturesapp.workspace.organs.OrganPart op = kryo.readObject(input, SmallIntestineOrganPart.class);
            Gdx.app.log("loadState","creating "+op+i);
            super.addActor(op);
            this.organParts.put(""+ ((SmallIntestineOrganPart)op).id, op );
        }
    }


    public static final int SEGMENTATION = 90;
    public void loadState(Kryo kryo, Input input, int segment){
        if(segment==1){
            this.free();
            size=0;

            for(int i = 0; i<= SEGMENTATION; i++){
                com.suturesapp.workspace.organs.OrganPart op = kryo.readObject(input, SmallIntestineOrganPart.class);
                Gdx.app.log("loadState","creating "+op+i);
                super.addActor(op);
                this.organParts.put(""+ ((SmallIntestineOrganPart)op).id, op );
            }
        }else{
            for(int i = SEGMENTATION+1; i<= getLENGTH(); i++){
                com.suturesapp.workspace.organs.OrganPart op = kryo.readObject(input, SmallIntestineOrganPart.class);
                Gdx.app.log("loadState","creating "+op+i);
                super.addActor(op);
                this.organParts.put(""+ ((SmallIntestineOrganPart)op).id, op );
            }
        }
    }

    @Override
    public boolean highlightCutting() {
        return false;
    }

    @Override
    public float getScale() {
        return getSCALE();
    }
}
