package com.suturesapp.workspace.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by neogineer on 12/12/16.
 *
 * Helps keeping a state of an organPart for easier memory storage (serialisation) process.
 *
 * Can also be used, with default state data, to initiate the game.
 *
 * Correctional Pattern.
 */
public class OrganPartDefinition implements KryoSerializable{

    public OrganPartDefinition(){
        this.openableSidesStates = new ArrayList<>();
        this.ropeId = 0;
    }


    public Vector2 position;

    public float angle;

    public String fullIdentifier;

    public List<com.suturesapp.workspace.organs.OpenableSide.State> openableSidesStates ;

    public int ropeId;

    public String getIdentifier(){
        if(!fullIdentifier.contains("Intest"))
            new Integer(5);
        Gdx.app.log("identifier",fullIdentifier);
        try{
            return this.fullIdentifier.split(com.suturesapp.workspace.organs.OrganPart.class.getSimpleName())[1];
        }catch (Exception e){
            return "";
        }
    }

    public String getOrganName(){
        return this.fullIdentifier.split(com.suturesapp.workspace.organs.OrganPart.class.getSimpleName())[0];
    }

    @Override
    public void write(Kryo kryo, Output output) {
        kryo.writeObject(output, position);
        output.writeFloat(angle);
        output.writeString(fullIdentifier);
        kryo.writeObject(output, openableSidesStates);
        output.writeInt(ropeId);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        this.position = kryo.readObject(input, Vector2.class);
        this.angle = input.readFloat();
        this.fullIdentifier = input.readString();
        this.openableSidesStates = kryo.readObject(input, ArrayList.class);
        this.ropeId = input.readInt();
    }
}
