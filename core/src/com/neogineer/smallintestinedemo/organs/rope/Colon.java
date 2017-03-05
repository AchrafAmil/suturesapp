package com.neogineer.smallintestinedemo.organs.rope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;

/**
 * Created by neogineer on 18/01/17.
 */
public class Colon extends com.neogineer.smallintestinedemo.organs.Organ {

    // number of ColonOrganPart instances
    private static final int LENGTH = Constants.COLON_LENGTH ;

    private static final float SCALE = Constants.COLON_SCALE;

    // how much the joint point is offset from the center (percentage from body dimensions) (0.5f means it's on the edge)
    private static final float JOINT_OFFSET_PERCENT = 0.30f;

    /**
     * Please make sure startPos is on <b> LEFT </b> of the endPos.
     */
    public Colon(World world, OrthographicCamera camera, Vector2 startPos, Vector2 endPos){
        super(world, camera);

        float slope = (endPos.y - startPos.y) / (endPos.x - startPos.x);
        double alpha = Math.atan(slope);

        ColonOrganPart firstActor
                = new ColonOrganPart(world, camera, this, 0, getSCALE(), startPos, (float) (alpha- Math.PI/2) );
        addActor(firstActor);
        this.organParts.put(""+ size++, firstActor );
        Body body = firstActor.body;

        Body link = body ;


        final float JOINT_OFFSET = firstActor
                .getVertex(0,firstActor.origin.cpy().y + firstActor.origin.cpy().y * 2 * getJointOffsetPercent()).y;



        final Vector2 step = new Vector2(JOINT_OFFSET*2*(float)Math.cos(alpha), JOINT_OFFSET*2*(float)Math.sin(alpha));

        for(int i = 0; i< getLENGTH(); i++){

            ColonOrganPart actor;
            actor = new ColonOrganPart(world, camera, this, i+1, getSCALE(),
                    new Vector2( startPos.x+step.x*i, startPos.y +step.y*i ), (float) (alpha- Math.PI/2));
            addActor(actor);
            this.organParts.put(""+ size++, actor );
            body = actor.body;

            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            // TODO: 18/11/16 review the offset,
            // TODO:          get it from the organ part so that it takes in consideration the vertices scaling and stuff
            connector.organA = (com.neogineer.smallintestinedemo.organs.OrganPart) link.getUserData();
            connector.anchorA = new Vector2(0, JOINT_OFFSET);
            connector.organB = (com.neogineer.smallintestinedemo.organs.OrganPart) body.getUserData();
            connector.anchorB = new Vector2(0, -JOINT_OFFSET);

            //just for connectTool debug
            //connector.proceed(true);

            connector.makeConnection(false);

            link = body;
        }

        //((ColonOrganPart) link.getUserData()).getOpenableSides().get(0).open();
    }

    /**
     * with predefined start/end positions
     */
    public Colon(World world, OrthographicCamera camera) {
        this(world, camera, Constants.COLON_LEFT_POSITION, Constants.COLON_RIGHT_POSITION);
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
            com.neogineer.smallintestinedemo.organs.OrganPart op = kryo.readObject(input, ColonOrganPart.class);
            Gdx.app.log("loadState","creating "+op+i);
            super.addActor(op);
            this.organParts.put(""+ ((ColonOrganPart)op).id, op );
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
