package com.suturesapp.workspace.organs;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Created by neogineer on 31/12/16.
 */
public class SuturePointDef implements KryoSerializable, Comparable<SuturePointDef>{

    private String organA ;
    private String organB ;
    private Vector2 anchorA ;
    private Vector2 anchorB ;
    private boolean Avisible;
    private boolean Bvisible;

    /** Optional (uses e.g: small intestine ids) */
    public int idA;
    public int idB;

    public SuturePointDef(){
        idA=0;
        idB=0;
        anchorA = new Vector2();
        anchorB = new Vector2();
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
        anchorA.set(kryo.readObject(input, Vector2.class));
        anchorB.set(kryo.readObject(input, Vector2.class));
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

    public String getOrganA() {
        return organA;
    }

    public void setOrganA(String organA) {
        this.organA = organA;
    }

    public String getOrganB() {
        return organB;
    }

    public void setOrganB(String organB) {
        this.organB = organB;
    }

    public Vector2 getAnchorA() {
        com.suturesapp.workspace.utils.Utils.vectorZeroPrecision(anchorA);
        return anchorA;
    }

    public void setAnchorA(Vector2 anchorA) {
        this.anchorA = anchorA;
    }

    public Vector2 getAnchorB() {
        com.suturesapp.workspace.utils.Utils.vectorZeroPrecision(anchorB);
        return anchorB;
    }

    public void setAnchorB(Vector2 anchorB) {
        this.anchorB = anchorB;
    }

    public boolean isAvisible() {
        return Avisible;
    }

    public void setAvisible(boolean avisible) {
        Avisible = avisible;
    }

    public boolean isBvisible() {
        return Bvisible;
    }

    public void setBvisible(boolean bvisible) {
        Bvisible = bvisible;
    }

    public int getIdA() {
        return idA;
    }

    public void setIdA(int idA) {
        this.idA = idA;
    }

    public int getIdB() {
        return idB;
    }

    public void setIdB(int idB) {
        this.idB = idB;
    }
}
