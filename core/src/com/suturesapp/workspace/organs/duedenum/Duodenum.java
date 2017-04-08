package com.suturesapp.workspace.organs.duedenum;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.utils.Constants;

/**
 * Created by neogineer on 26/11/16.
 */
public class Duodenum extends Organ {

    public static final float SCALE = Constants.DUODENUM_SCALE;

    public static final Vector2 POSITION = Constants.DUODENUM_POSITION;

    private static final int SIZE = 2;

    public Duodenum(World world, OrthographicCamera camera) {
        super(world, camera);

        for(int i=1; i<=SIZE; i++){
            com.suturesapp.workspace.organs.OrganPart part = new DuodenumOrganPart(world, camera, this, ""+i, SCALE, getOrganPartPosition(i), 0);
            organParts.put(part.getIdentifier(), part);
            addActor(part);
        }

        // Duodenum has only one internal joint.
        com.suturesapp.workspace.tools.ConnectTool tool = new com.suturesapp.workspace.tools.ConnectTool(world, camera, null);
        com.suturesapp.workspace.tools.ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

        DuodenumOrganPart organA = (DuodenumOrganPart) this.organParts.get("1");
        DuodenumOrganPart organB = (DuodenumOrganPart) this.organParts.get("2");

        connector.organA = organA;
        connector.organB = organB;

        connector.anchorA = organA.getVertex(0.9143751263618469f, 0.06750062853097916f);
        connector.anchorB = organB.getVertex(0.1609283983707428f, 0.9446786642074585f);

        connector.makeConnection(false);


    }

    @Override
    public void loadState(Kryo kryo, Input input){
        this.free();

        for(int i=1; i<=SIZE; i++){
            com.suturesapp.workspace.organs.OrganPart op = kryo.readObject(input, DuodenumOrganPart.class);
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
                return new Vector2(POSITION.x+7.3f*SCALE, POSITION.y-8.2f*SCALE);
            default:
                return null;
        }
    }
}
