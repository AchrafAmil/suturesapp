package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by neogineer on 31/12/16.
 */
public class SuturePointDef implements KryoSerializable, Comparable<SuturePointDef>{

    public String organA ;
    public String organB ;
    public Vector2 anchorA ;
    public Vector2 anchorB ;
    public boolean Avisible;
    public boolean Bvisible;

    /** Optional (uses e.g: small intestine ids) */
    public int idA;
    public int idB;

    public SuturePointDef(){
        idA=0;
        idB=0;
    }

    @Override
    public void write(Kryo kryo, Output output) {
        output.writeString(organA);
        output.writeString(organB);
        kryo.writeObject(output, anchorA);
        kryo.writeObject(output, anchorB);
        output.writeBoolean(Avisible);
        output.writeBoolean(Bvisible);
        output.writeInt(idA);
        output.writeInt(idB);
    }

    @Override
    public void read(Kryo kryo, Input input) {
        organA = input.readString();
        organB = input.readString();
        anchorA = kryo.readObject(input, Vector2.class);
        anchorB = kryo.readObject(input, Vector2.class);
        Avisible = input.readBoolean();
        Bvisible = input.readBoolean();
        idA = input.readInt();
        idB = input.readInt();
    }

    @Override
    public int compareTo(SuturePointDef o) {
        if(((idA - o.idA)+(idB - o.idB))==0)
            return this.equals(o)? 0:1;
        return (idA - o.idA)+(idB - o.idB);


    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof SuturePointDef))
            return false;
        SuturePointDef other = (SuturePointDef) obj;
        return organA.equals(other.organA)
                && organB.equals(other.organB)
                && anchorA.equals(other.anchorA)
                && anchorB.equals(other.anchorB)
                && Avisible== other.Avisible
                && Bvisible== other.Bvisible
                && idA==other.idA
                && idB==other.idB ;
    }
}
