package com.neogineer.smallintestinedemo.organs.liver;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 08/11/16.
 */
public class Liver extends Organ {

    public static final float SCALE = 0.85f;

    public static final Vector2 POSITION = new Vector2(15, 15);

    private static final Vector2 STEP = new Vector2(2.5f*SCALE, 10*SCALE);


    public Liver(World world, OrthographicCamera camera) {
        super(world, camera);

        OrganPart part;
        part = new LiverOrganPart1(world, camera, null, SCALE, new Vector2(POSITION.x, POSITION.y ), 0);
        organParts.put(((LiverOrganPart)part).getID(), part);
        addActor(part);

        OrganPart part2 = new LiverOrganPart2(world, camera, null, SCALE, new Vector2(POSITION.x,POSITION.y - STEP.y), 0);
        organParts.put(((LiverOrganPart)part2).getID(), part2);
        addActor(part2);

        OrganPart part3 = new LiverOrganPart3(world, camera, null, SCALE, new Vector2(POSITION.x+STEP.x, POSITION.y), 0);
        organParts.put(((LiverOrganPart)part3).getID(), part3);
        addActor(part3);

        OrganPart part4 = new LiverOrganPart4(world, camera, null, SCALE, new Vector2(POSITION.x+STEP.x, POSITION.y-STEP.y), 0);
        organParts.put(((LiverOrganPart)part4).getID(), part4);
        addActor(part4);


        OrganPart part5 = new LiverOrganPart5(world, camera, null, SCALE, new Vector2(POSITION.x+2*STEP.x, POSITION.y), 0);
        organParts.put(((LiverOrganPart)part5).getID(), part5);
        addActor(part5);


        OrganPart part6 = new LiverOrganPart6(world, camera, null, SCALE, new Vector2(POSITION.x+2*STEP.x, POSITION.y-STEP.y), 0);
        organParts.put(((LiverOrganPart)part6).getID(), part6);
        addActor(part6);

        OrganPart part7 = new LiverOrganPart7(world, camera, null, SCALE, new Vector2(POSITION.x+3*STEP.x,POSITION.y-0.75f*STEP.y), 0);
        organParts.put(((LiverOrganPart)part7).getID(), part7);
        addActor(part7);


        OrganPart part8 = new LiverOrganPart8(world, camera, null, SCALE, new Vector2(POSITION.x+4*STEP.x , POSITION.y-0.75f*STEP.y),0 );
        organParts.put(((LiverOrganPart)part8).getID(), part8);
        addActor(part8);



        JSONArray joints = Utils.loadJoints("liver_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            LiverOrganPart organA = (LiverOrganPart) this.organParts.get(joint.getString("organA"));
            LiverOrganPart organB = (LiverOrganPart) this.organParts.get(joint.getString("organB"));

            connector.organA = organA;
            connector.organB = organB;

            connector.anchorA = organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);

        }

    }
}
