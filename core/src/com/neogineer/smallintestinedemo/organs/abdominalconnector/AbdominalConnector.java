package com.neogineer.smallintestinedemo.organs.abdominalconnector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.abdominalconnector.AbdominalConnectorOrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by neogineer on 17/03/17.
 */
public class AbdominalConnector extends Organ {


    public static final float SCALE = Constants.ABDOMINALCONNECTOR_SCALE;

    public static final Vector2 POSITION = Constants.ABDOMINALCONNECTOR_POSITION;

    private static final Vector2 STEP = new Vector2(2.5f*SCALE, 10*SCALE);

    public static final int SIZE = 8 ;


    public AbdominalConnector(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            OrganPart part = new AbdominalConnectorOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }

    }

    @Override
    public void loadState(Kryo kryo, Input input){
        this.free();

        for(int i=1; i<=SIZE; i++){
            OrganPart op = kryo.readObject(input, AbdominalConnectorOrganPart.class);
            Gdx.app.log("loadState","creating "+op+op.getIdentifier());
            organParts.put(op.getIdentifier(), op);
            addActor(op);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
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
                return new Vector2(POSITION.x,POSITION.y - STEP.y);
            case 3:
                return new Vector2(POSITION.x+STEP.x, POSITION.y);
            case 4:
                return new Vector2(POSITION.x+STEP.x, POSITION.y-STEP.y);
            case 5:
                return new Vector2(POSITION.x+2*STEP.x, POSITION.y);
            case 6:
                return new Vector2(POSITION.x+2*STEP.x, POSITION.y-STEP.y);
            case 7:
                return new Vector2(POSITION.x+3*STEP.x,POSITION.y-0.75f*STEP.y);
            case 8:
                return new Vector2(POSITION.x+4*STEP.x , POSITION.y-0.75f*STEP.y);
            default:
                return null;
        }
    }
}
