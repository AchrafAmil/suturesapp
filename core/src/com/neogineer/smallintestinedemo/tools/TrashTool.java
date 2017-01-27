package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.neogineer.smallintestinedemo.organs.OrganPart;
import com.neogineer.smallintestinedemo.organs.SuturePoint;
import com.neogineer.smallintestinedemo.organs.abdominalwall.AbdominalWallOrganPart;
import com.neogineer.smallintestinedemo.organs.esophagus.EsophagusOrganPart;
import com.neogineer.smallintestinedemo.organs.rectum.RectumOrganPart;
import com.neogineer.smallintestinedemo.utils.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by neogineer on 26/01/17.
 */
public class TrashTool extends Tool {


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

            boolean eso = false, rectu = false;
            for(OrganPart op : bloc){
                if(op instanceof EsophagusOrganPart)
                    eso = true;
                if(op instanceof RectumOrganPart)
                    rectu = true;
            }

            if(eso && rectu)
                return false;

            for(OrganPart op : bloc){
                op.body.setTransform(150,150,0);
            }

        }

        return false;
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
