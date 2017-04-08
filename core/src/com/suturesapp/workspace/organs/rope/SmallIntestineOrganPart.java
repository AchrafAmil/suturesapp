package com.suturesapp.workspace.organs.rope;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.utils.Constants;

/**
 * Created by neogineer on 18/01/17.
 */
public class SmallIntestineOrganPart extends RopeOrganPart {

    private static final int MAX_SUTURE_POINTS = 3 ;

    private static final float HORIZONTAL_SUTUREPOINT_POSITION = 0.18f;

    public SmallIntestineOrganPart(World world, OrthographicCamera camera, Organ callback, int id, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, id, scale, position, rotation);
        setupBody("SmallIntestineOrganPart.json");
        setupOpenableSides("SmallIntestineOrganPart.json");
    }

    public SmallIntestineOrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef) {
        super(opDef);
        setupBody("SmallIntestineOrganPart.json");
        setupOpenableSides("SmallIntestineOrganPart.json");
    }

    public int getMaxSuturePoints() {
        return MAX_SUTURE_POINTS;
    }

    @Override
    protected float getHorizontalSuturePointPosition(Vector2 vec) {
        return HORIZONTAL_SUTUREPOINT_POSITION;
    }

    @Override
    public boolean isVeryMiddle(){
        if(hasAnOpenSide())
            return false;
        for(com.suturesapp.workspace.organs.SuturePoint sp: suturePoints){
            com.suturesapp.workspace.organs.OrganPart other = sp.getTheOtherOrganPart();
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
