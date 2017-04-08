package com.suturesapp.workspace.organs.rope;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.Organ;
import com.suturesapp.workspace.organs.OrganPart;
import com.suturesapp.workspace.utils.Utils;

/**
 * Created by neogineer on 18/01/17.
 */
public class ColonOrganPart extends RopeOrganPart {

    private static final int MAX_SUTURE_POINTS = 5 ;
    private static final float HORIZONTAL_SUTUREPOINT_POSITION = 0.105f;

    public ColonOrganPart(World world, OrthographicCamera camera, Organ callback, int id, float scale, Vector2 position, float rotation) {
        super(world, camera, callback, id, scale, position, rotation);
        setupBody("ColonOrganPart.json");
        setupOpenableSides("ColonOrganPart.json");
    }

    public ColonOrganPart(com.suturesapp.workspace.organs.OrganPartDefinition opDef) {
        super(opDef);
        setupBody("ColonOrganPart.json");
        setupOpenableSides("ColonOrganPart.json");
    }

    public int getMaxSuturePoints() {
        return MAX_SUTURE_POINTS;
    }

    @Override
    protected float getHorizontalSuturePointPosition(Vector2 vec) {
        return HORIZONTAL_SUTUREPOINT_POSITION * ((Utils.getRegion(vec)>4)? 2.8f:1);
    }

    @Override
    public boolean isVeryMiddle(){
        try{
            if(hasAnOpenSide())
                return false;
            for(com.suturesapp.workspace.organs.SuturePoint sp: suturePoints){
                OrganPart other = sp.getTheOtherOrganPart();
                // connected to another organ = not very middle.
                if(!(other instanceof ColonOrganPart))
                    return false;
                if(!sp.isNoXY() && !(((ColonOrganPart)other).isMiddle()))
                    return false;
            }
            return true;
        }catch(NullPointerException npe){
            return false;
        }
    }

    @Override
    public boolean isVeryEdge(){
        return (hasAnOpenSide() || endSuturePointsCount()>=1);
    }

    @Override
    public short getCategory() {
        return com.suturesapp.workspace.utils.Constants.CATEGORY_COLON;
    }

    @Override
    public short getMask() {
        return com.suturesapp.workspace.utils.Constants.MASK_COLON;
    }

    @Override
    public Vector2 getDimensions() {
        Vector2 vec = super.getDimensions();
        vec.y*=0.85f;
        return vec;

    }
}
