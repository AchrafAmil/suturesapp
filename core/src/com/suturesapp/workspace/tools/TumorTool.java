package com.suturesapp.workspace.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.suturesapp.workspace.organs.OrganPart;
import com.suturesapp.workspace.organs.Tumor;

/**
 * Created by neogineer on 05/03/17.
 */
public class TumorTool extends Tool {
    public TumorTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera, groundBody);
    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        super.touchDown(screenX, screenY, pointer, button);

        if(hitBody!=null){

            Vector2 localCoord = new Vector2(testPoint.x, testPoint.y);
            localCoord = hitBody.getLocalPoint(localCoord);

            ((OrganPart)hitBody.getUserData()).addTumor(new Tumor(localCoord.x, localCoord.y));



        }

        return false;
    }
}
