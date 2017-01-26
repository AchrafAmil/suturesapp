package com.neogineer.smallintestinedemo.organs.rectum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.appendix.AppendixOrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;
import com.neogineer.smallintestinedemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 26/01/17.
 */
public class Rectum extends Organ {

    public static final float SCALE = Constants.RECTUM_SCALE;

    public static final Vector2 POSITION = Constants.RECTUM_POSITION;

    private static final int SIZE = 6;

    public Rectum(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            OrganPart part = new RectumOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i),0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }

        JSONArray joints = Utils.loadJoints("rectum_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            RectumOrganPart organA = (RectumOrganPart) this.organParts.get(joint.getString("organA"));
            RectumOrganPart organB = (RectumOrganPart) this.organParts.get(joint.getString("organB"));

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
            OrganPart op = kryo.readObject(input, RectumOrganPart.class);
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
                return new Vector2(POSITION.x, POSITION.y +4*SCALE);
            case 2:
                return new Vector2(POSITION.x-12f*SCALE, POSITION.y-10f*SCALE);
            case 3:
                return new Vector2(POSITION.x-12f*SCALE, POSITION.y-16f*SCALE);
            case 4:
                return new Vector2(POSITION.x-12f*SCALE, POSITION.y-25f*SCALE);
            case 5:
                return new Vector2(POSITION.x-12f*SCALE, POSITION.y-34f*SCALE);
            case 6:
                return new Vector2(POSITION.x-12f*SCALE, POSITION.y-43f*SCALE);
            default:
                return null;
        }
    }
}
