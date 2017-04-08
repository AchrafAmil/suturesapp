package com.suturesapp.workspace.organs.pancreas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.organs.OrganPart;
import com.suturesapp.workspace.utils.Constants;
import com.suturesapp.workspace.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 16/03/17.
 */
public class Pancreas extends Organ {



    public static final float SCALE = Constants.PANCREAS_SCALE;

    public static final Vector2 POSITION = Constants.PANCREAS_POSITION;

    private static final int SIZE = 2;

    public Pancreas(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            OrganPart part = new PancreasOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }


        JSONArray joints = Utils.loadJoints("pancreas_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint;try{joint = (JSONObject) joints.get(i);}catch(Exception e){continue;}
            com.suturesapp.workspace.tools.ConnectTool tool = new com.suturesapp.workspace.tools.ConnectTool(world, camera, null);
            com.suturesapp.workspace.tools.ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            PancreasOrganPart organA = (PancreasOrganPart) this.organParts.get(joint.getString("organA"));
            PancreasOrganPart organB = (PancreasOrganPart) this.organParts.get(joint.getString("organB"));

            connector.organA = organA;
            connector.organB = organB;

            connector.anchorA = organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);
        }
    }

    @Override
    public void loadState(Kryo kryo, Input input){
        this.free();

        for(int i=1; i<=SIZE; i++){
            OrganPart op = kryo.readObject(input, PancreasOrganPart.class);
            Gdx.app.log("loadState","creating "+op+op.getIdentifier());
            organParts.put(op.getIdentifier(), op);
            addActor(op);
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
                return new Vector2(POSITION.x+8f*SCALE, POSITION.y+2f*SCALE);
            default:
                return null;
        }
    }

}
