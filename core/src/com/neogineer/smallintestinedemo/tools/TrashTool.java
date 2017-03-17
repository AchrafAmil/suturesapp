package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.SuturePoint;
import com.neogineer.smallintestinedemo.organs.abdominalwall.AbdominalWallOrganPart;
import com.neogineer.smallintestinedemo.organs.esophagus.EsophagusOrganPart;
import com.neogineer.smallintestinedemo.organs.rectum.RectumOrganPart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by neogineer on 26/01/17.
 */
public class TrashTool extends Tool {

    public static int TRASH_POSITION_X = 150 ;
    public static int TRASH_POSITION_Y = 150 ;


    public TrashTool(World world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);


        if(hitBody!=null){
            if(hitBody.getUserData() instanceof AbdominalWallOrganPart)
                return false;
            ArrayList<OrganPart> bloc = getOrganPartBloc((OrganPart) hitBody.getUserData());
            if(bloc==null)
                return false;

            if(hasRectumAndEsophagus(bloc))
                return false;

            for(OrganPart op : bloc){
                //op.body.setTransform(TRASH_POSITION_X+=5,TRASH_POSITION_Y+=5,0);
                Vector2 pos = op.body.getPosition();
                float rotation = op.body.getAngle();
                op.body.setTransform(pos.x+TRASH_POSITION_X, pos.y+TRASH_POSITION_Y, rotation);
            }

        }

        return false;
    }

    private static boolean hasRectumAndEsophagus(ArrayList<OrganPart> bloc) {
        boolean eso = false, rectu = false;
        for(OrganPart op : bloc){
            if(op instanceof EsophagusOrganPart)
                eso = true;
            if(op instanceof RectumOrganPart)
                rectu = true;
        }


            return (eso && rectu);

    }

    public ArrayList<OrganPart> getOrganPartBloc(OrganPart organPart){

        Set<OrganPart> set = new HashSet<>();
        set.add(organPart);

        int size ;

        do {
            size = set.size();
            Set<OrganPart> tmpSet = new HashSet<>();
            for(OrganPart op : set){
                for(SuturePoint sp: op.getSuturePoints()){
                    OrganPart other = sp.getTheOtherOrganPart();
                    tmpSet.add(other);
                }
            }
            set.addAll(tmpSet);
        }while (size!=set.size());

        return new ArrayList<>(set);
    }
}
