package com.neogineer.smallintestinedemo.organs.rope;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.Organ;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.OrganPartDefinition;
import com.neogineer.smallintestinedemo.organs.SuturePoint;
import com.neogineer.smallintestinedemo.utils.Constants;

/**
 * Created by neogineer on 18/01/17.
 */
public class SmallIntestineOrganPart extends RopeOrganPart {

    private static final int MAX_SUTURE_POINTS = 3 ;

    public SmallIntestineOrganPart(World world, OrthographicCamera camera, Organ callback, int id, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, id, scale, position, rotation);
        setupBody("SmallIntestineOrganPart.json");
        setupOpenableSides("SmallIntestineOrganPart.json");
    }

    public SmallIntestineOrganPart(OrganPartDefinition opDef) {
        super(opDef);
        setupBody("SmallIntestineOrganPart.json");
        setupOpenableSides("SmallIntestineOrganPart.json");
    }

    public int getMaxSuturePoints() {
        return MAX_SUTURE_POINTS;
    }

    @Override
    public boolean isVeryMiddle(){
        if(hasAnOpenSide())
            return false;
        for(SuturePoint sp: suturePoints){
            OrganPart other = sp.getTheOtherOrganPart();
            // connected to another organ = not very middle.
            if(!(other instanceof SmallIntestineOrganPart))
                return false;
            if(!sp.isNoXY() && !(((SmallIntestineOrganPart)other).isMiddle()))
                return false;
        }
        return true;
    }

    @Override
    public boolean isVeryEdge(){
        return (hasAnOpenSide() || endSuturePointsCount()>=1);
    }


    @Override
    public short getCategory() {
        return Constants.CATEGORY_SMALLINTESTINE;
    }

    @Override
    public short getMask() {
        return Constants.MASK_SMALLINTESTINE;
    }
}
