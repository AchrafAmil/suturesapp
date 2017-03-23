package com.neogineer.smallintestinedemo.organs.esophagus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 27/11/16.
 */
public class Esophagus extends Organ {

    public static final float SCALE = Constants.ESOPHAGUS_SCALE;

    public static final Vector2 POSITION = Constants.ESOPHAGUS_POSITION;

    private static final int SIZE = 3;

    public Esophagus(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            OrganPart part = new EsophagusOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }


        JSONArray joints = com.neogineer.smallintestinedemo.utils.Utils.loadJoints("esophagus_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
                JSONObject joint;try{joint = (JSONObject) joints.get(i);}catch(Exception e){continue;}
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
    public void loadState(Kryo kryo, Input input){
        this.free();

        for(int i=1; i<=SIZE; i++){
            OrganPart op = kryo.readObject(input, EsophagusOrganPart.class);
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
                return new Vector2(POSITION.x, POSITION.y-3f*SCALE);
            case 3:
                return new Vector2(POSITION.x, POSITION.y-6f*SCALE);
            default:
                return null;
        }
    }
}
