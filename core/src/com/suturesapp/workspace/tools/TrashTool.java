package com.suturesapp.workspace.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;

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
            if(hitBody.getUserData() instanceof com.suturesapp.workspace.organs.abdominalwall.AbdominalWallOrganPart)
                return false;
            ArrayList<com.suturesapp.workspace.organs.OrganPart> bloc = getOrganPartBloc((com.suturesapp.workspace.organs.OrganPart) hitBody.getUserData());
            if(bloc==null)
                return false;

            boolean eso = false, rectu = false;
            for(com.suturesapp.workspace.organs.OrganPart op : bloc){
                if(op instanceof com.suturesapp.workspace.organs.esophagus.EsophagusOrganPart)
                    eso = true;
                if(op instanceof com.suturesapp.workspace.organs.rectum.RectumOrganPart)
                    rectu = true;
            }

            if(eso && rectu)
                return false;

            for(com.suturesapp.workspace.organs.OrganPart op : bloc){
                //op.body.setTransform(TRASH_POSITION_X+=5,TRASH_POSITION_Y+=5,0);
                op.organCallback.removeActor(op);
                op.destroy();
            }

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
