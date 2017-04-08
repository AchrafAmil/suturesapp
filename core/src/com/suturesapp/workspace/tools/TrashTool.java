package com.suturesapp.workspace.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.esophagus.EsophagusOrganPart;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by neogineer on 26/01/17.
 */
public class TrashTool extends Tool {

    public static int TRASH_POSITION_X = 150 ;
    public static int TRASH_POSITION_Y = 150 ;


    public TrashTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera, groundBody);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);


        if(hitBody!=null){
            if(hitBody.getUserData() instanceof com.suturesapp.workspace.organs.abdominalwall.AbdominalWallOrganPart)
                return false;
            ArrayList<com.suturesapp.workspace.organs.OrganPart> bloc = getOrganPartBloc((com.suturesapp.workspace.organs.OrganPart) hitBody.getUserData());
            if(bloc==null)
                return false;

            if(hasNonTrashableOrganParts(bloc))
                return false;

            for(com.suturesapp.workspace.organs.OrganPart op : bloc){
                //op.body.setTransform(TRASH_POSITION_X+=5,TRASH_POSITION_Y+=5,0);
                Vector2 pos = op.body.getPosition();
                float rotation = op.body.getAngle();
                op.body.setTransform(pos.x+TRASH_POSITION_X, pos.y+TRASH_POSITION_Y, rotation);
            }

        }

        return false;
    }

    @Override
    public boolean bodyMoving() throws NullPointerException {
        // disabled
        return false;
    }

    private static boolean hasNonTrashableOrganParts(ArrayList<com.suturesapp.workspace.organs.OrganPart> bloc) {
        for(com.suturesapp.workspace.organs.OrganPart op : bloc){
            if(op instanceof EsophagusOrganPart)
                if(op.identifier.equals("1"))
                    return true;
            if(op instanceof com.suturesapp.workspace.organs.rectum.RectumOrganPart)
                if(op.identifier.equals("6"))
                    return true;
            if(op instanceof com.suturesapp.workspace.organs.abdominalwall.AbdominalWallOrganPart)
                return true;
        }
        return false;
    }

    public ArrayList<com.suturesapp.workspace.organs.OrganPart> getOrganPartBloc(com.suturesapp.workspace.organs.OrganPart organPart){

        Set<com.suturesapp.workspace.organs.OrganPart> set = new HashSet<>();
        set.add(organPart);

        int size ;

        do {
            size = set.size();
            Set<com.suturesapp.workspace.organs.OrganPart> tmpSet = new HashSet<>();
            for(com.suturesapp.workspace.organs.OrganPart op : set){
                for(com.suturesapp.workspace.organs.SuturePoint sp: op.getSuturePoints()){
                    com.suturesapp.workspace.organs.OrganPart other = sp.getTheOtherOrganPart();
                    tmpSet.add(other);
                }
            }
            set.addAll(tmpSet);
        }while (size!=set.size());

        return new ArrayList<>(set);
    }
}
