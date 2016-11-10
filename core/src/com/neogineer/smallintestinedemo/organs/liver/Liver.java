package com.neogineer.smallintestinedemo.organs.liver;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Utils;

/**
 * Created by neogineer on 08/11/16.
 */
public class Liver extends Organ {

    public static final float SCALE = 2f;

    //0.7699999809265137,"y":0.062499940395355225
    //0.7975001931190491,"y":2.3703131675720215

    public Liver(World world, OrthographicCamera camera) {
        super(world, camera);

        OrganPart part;
        part = new LiverOrganPart1(world, camera, null, SCALE, new Vector2(0, 20 ), 0);

        addActor(part);

        OrganPart part2 = new LiverOrganPart2(world, camera, null, SCALE, new Vector2(0,0), 0);

        addActor(part2);


        ConnectTool tool = new ConnectTool(world, camera);
        ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

        connector.organA =part;
        float consA = LiverOrganPart1.VERTICES_SCALE;
        connector.anchorA = new Vector2((0.7699999809265137f-0.5f)*SCALE *consA, (0.062499940395355225f-0.7083333730697632f)*SCALE*consA);
        connector.organB = part2;
        float consB = LiverOrganPart2.VERTICES_SCALE;
        connector.anchorB = new Vector2((0.7975001931190491f-0.5f)*SCALE*consB, (2.3703131675720215f-1.2f)*SCALE*consB);

        connector.makeConnection(false);

        OrganPart part3 = new LiverOrganPart3(world, camera, null, SCALE, new Vector2(5,20), 0);

        addActor(part3);

        OrganPart part4 = new LiverOrganPart4(world, camera, null, SCALE, new Vector2(5,0), 0);

        addActor(part4);


        OrganPart part5 = new LiverOrganPart5(world, camera, null, SCALE, new Vector2(10,20), 0);

        addActor(part5);


        OrganPart part6 = new LiverOrganPart6(world, camera, null, SCALE, new Vector2(10,0), 0);

        addActor(part6);

        OrganPart part7 = new LiverOrganPart7(world, camera, null, SCALE, new Vector2(15,15), 0);

        addActor(part7);


        OrganPart part8 = new LiverOrganPart8(world, camera, null, SCALE, new Vector2(20,15), 0);

        addActor(part8);

    }
}
