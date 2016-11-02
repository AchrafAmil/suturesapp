package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Utils;

/**
 * Created by neogineer on 27/10/16.
 */
public class SmallIntestine extends Organ {

    // number of SmallIntestineOrganPart instances
    public static final int LENGTH = 350 ;

    public static final float SCALE = 0.2f;

    // how much the joint point is offset from the center (percentage from body dimensions) (0.5f means it's on the edge)
    public static final float JOINT_OFFSET_PERCENT = 0.35f;

    /**
     * Please make sure startPos is on <b> LEFT </b> of the endPos.
     */
    public SmallIntestine(World world, OrthographicCamera camera, Vector2 startPos, Vector2 endPos){
        super(world, camera);

        SmallIntestineOrganPart firstActor = new SmallIntestineOrganPart(world, camera, this, 0, SCALE, startPos );
        firstActor.getOpenableSides().get(0).close();
        addActor(firstActor);
        Body body = firstActor.body;

        Body link = body ;


        final float JOINT_OFFSET = SmallIntestineOrganPart.BASE_HEIGHT / (Utils.getPixelPerMeter().y )
                                        * SCALE * JOINT_OFFSET_PERCENT ;

        float slope = (endPos.y - startPos.y) / (endPos.x - startPos.x);
        double alpha = Math.atan(slope);

        final Vector2 step = new Vector2(JOINT_OFFSET*2*(float)Math.cos(alpha), JOINT_OFFSET*2*(float)Math.sin(alpha));

        for(int i=0; i<LENGTH; i++){

            SmallIntestineOrganPart actor;
            actor = new SmallIntestineOrganPart(world, camera, this, i+1, SCALE,
                        new Vector2( startPos.x+step.x*i, startPos.y +step.y*i ));
            addActor(actor);
            body = actor.body;

            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            connector.organA = (OrganPart) link.getUserData();
            connector.anchorA = new Vector2(0, -JOINT_OFFSET);
            connector.organB = (OrganPart) body.getUserData();
            connector.anchorB = new Vector2(0, JOINT_OFFSET);

            //just for connectTool debug
            //connector.proceed(true);

            connector.makeConnection(false);

            link = body;
        }

        ((SmallIntestineOrganPart) link.getUserData()).getOpenableSides().get(1).open();
    }

    /**
     * with predefined start/end positions
     */
    public SmallIntestine(World world, OrthographicCamera camera) {
        this(world, camera, new Vector2(7,7), new Vector2(15,20));
    }

    @Override
    public void addActor(Actor actor) {
        super.addActor(actor);
        this.organParts.put(""+ size++, (SmallIntestineOrganPart) actor );
    }

    @Override
    public boolean highlightCutting() {
        return false;
    }
}
