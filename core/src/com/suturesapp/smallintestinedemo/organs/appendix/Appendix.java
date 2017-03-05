package com.suturesapp.smallintestinedemo.organs.appendix;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.suturesapp.smallintestinedemo.organs.Organ;
import com.suturesapp.smallintestinedemo.organs.OrganPart;
import com.suturesapp.smallintestinedemo.tools.ConnectTool;
import com.suturesapp.smallintestinedemo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 15/01/17.
 */
public class Appendix extends Organ {

    public static final float SCALE = Constants.APPENDIX_SCALE;

    public static final Vector2 POSITION = Constants.APPENDIX_POSITION;

    private static final int SIZE = 3;

    public Appendix(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            OrganPart part = new AppendixOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), (float) (5 * Math.PI / 6));
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }

        JSONArray joints = com.suturesapp.smallintestinedemo.utils.Utils.loadJoints("appendix_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            AppendixOrganPart organA = (AppendixOrganPart) this.organParts.get(joint.getString("organA"));
            AppendixOrganPart organB = (AppendixOrganPart) this.organParts.get(joint.getString("organB"));

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
            OrganPart op = kryo.readObject(input, AppendixOrganPart.class);
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
                return new Vector2(POSITION.x-2.1f*SCALE, POSITION.y-12.64f*SCALE);
            case 3:
                return new Vector2(POSITION.x-11.32f*SCALE, POSITION.y-20.39f*SCALE);
            default:
                return null;
        }
    }
}
