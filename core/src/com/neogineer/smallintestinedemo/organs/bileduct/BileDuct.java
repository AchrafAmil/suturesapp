package com.neogineer.smallintestinedemo.organs.bileduct;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.bileduct.BileDuctOrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 15/03/17.
 */
public class BileDuct extends Organ {


    public static final float SCALE = Constants.BILEDUCT_SCALE;

    public static final Vector2 POSITION = Constants.BILEDUCT_POSITION;

    private static final int SIZE = 5;

    public BileDuct(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            OrganPart part = new BileDuctOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }


        /*JSONArray joints = Utils.loadJoints("bileDuct_initial_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            BileDuctOrganPart organA = (BileDuctOrganPart) this.organParts.get(joint.getString("organA"));
            BileDuctOrganPart organB = (BileDuctOrganPart) this.organParts.get(joint.getString("organB"));

            connector.organA = organA;
            connector.organB = organB;

            connector.anchorA = organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);
        }*/
    }

    @Override
    public void loadState(Kryo kryo, Input input){
        this.free();

        for(int i=1; i<=SIZE; i++){
            OrganPart op = kryo.readObject(input, BileDuctOrganPart.class);
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
                return new Vector2(POSITION.x-2f*SCALE, POSITION.y+2f*SCALE);
            case 3:
                return new Vector2(POSITION.x-4f*SCALE, POSITION.y+4f*SCALE);
            case 4:
                return new Vector2(POSITION.x-7f*SCALE, POSITION.y+5f*SCALE);
            case 5:
                return new Vector2(POSITION.x-5.5f*SCALE, POSITION.y+8f*SCALE);
            default:
                return null;
        }
    }
}
