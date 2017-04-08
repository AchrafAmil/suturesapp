package com.suturesapp.workspace.organs.spleen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.suturesapp.workspace.organs.Organ;

/**
 * Created by neogineer on 17/03/17.
 */
public class Spleen extends Organ {


    public static final float SCALE = com.suturesapp.workspace.utils.Constants.SPLEEN_SCALE;

    public static final Vector2 POSITION = com.suturesapp.workspace.utils.Constants.SPLEEN_POSITION;

    private static final int SIZE = 1;

    public Spleen(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            com.suturesapp.workspace.organs.OrganPart part = new com.suturesapp.workspace.organs.spleen.SpleenOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }

    }

    @Override
    public void loadState(Kryo kryo, Input input){
        this.free();

        for(int i=1; i<=SIZE; i++){
            com.suturesapp.workspace.organs.OrganPart op = kryo.readObject(input, com.suturesapp.workspace.organs.spleen.SpleenOrganPart.class);
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
            default:
                return null;
        }
    }

}
