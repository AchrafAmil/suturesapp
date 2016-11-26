package com.neogineer.smallintestinedemo.organs.stomach;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;
import com.neogineer.smallintestinedemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 19/11/16.
 */
public class Stomach extends Organ {

    public static final float SCALE = Constants.STOMACH_SCALE;

    public static final Vector2 POSITION = Constants.STOMACH_POSITION;


    public Stomach(World world, OrthographicCamera camera) {
        super(world, camera);


        for(int i=1; i<=8; i++){
            OrganPart part = new StomachOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }


        JSONArray joints = Utils.loadJoints("stomach_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            StomachOrganPart organA = (StomachOrganPart) this.organParts.get(joint.getString("organA"));
            StomachOrganPart organB = (StomachOrganPart) this.organParts.get(joint.getString("organB"));

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
                return new Vector2(POSITION.x+0.5f*SCALE, POSITION.y-7*SCALE);
            case 3:
                return new Vector2(POSITION.x-5*SCALE, POSITION.y-7*SCALE);
            case 4:
                return new Vector2(POSITION.x-10.5f*SCALE, POSITION.y-11f*SCALE);
            case 5:
                return new Vector2(POSITION.x + 6*SCALE, POSITION.y );
            case 6:
                return new Vector2(POSITION.x + 7*SCALE, POSITION.y-2*SCALE );
            case 7:
                return new Vector2(POSITION.x + 7*SCALE, POSITION.y-4*SCALE );
            case 8:
                return new Vector2(POSITION.x + 7*SCALE, POSITION.y-6*SCALE );
            default:
                return null;
        }
    }
}
