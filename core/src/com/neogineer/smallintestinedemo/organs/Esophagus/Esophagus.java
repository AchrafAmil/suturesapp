package com.neogineer.smallintestinedemo.organs.Esophagus;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.stomach.StomachOrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;
import com.neogineer.smallintestinedemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 27/11/16.
 */
public class Esophagus extends Organ {

    public static final float SCALE = Constants.ESOPHAGUS_SCALE;

    public static final Vector2 POSITION = Constants.ESOPHAGUS_POSITION;

    public Esophagus(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=3; i++){
            OrganPart part = new EsophagusOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }


        JSONArray joints = Utils.loadJoints("esophagus_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            EsophagusOrganPart organA = (EsophagusOrganPart) this.organParts.get(joint.getString("organA"));
            EsophagusOrganPart organB = (EsophagusOrganPart) this.organParts.get(joint.getString("organB"));

            connector.organA = organA;
            connector.organB = organB;

            connector.anchorA = organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);

        }
    }




    @Override
    public float getScale() {
        return SCALE;
    }

    private static Vector2 getOrganPartPosition(int identifier){
        switch (identifier){
            case 1:
                return new Vector2(POSITION.x, POSITION.y );
            case 2:
                return new Vector2(POSITION.x, POSITION.y-3f*SCALE);
            case 3:
                return new Vector2(POSITION.x, POSITION.y-6f*SCALE);
            default:
                return null;
        }
    }
}
