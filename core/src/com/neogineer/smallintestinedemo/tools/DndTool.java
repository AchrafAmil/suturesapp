package com.neogineer.smallintestinedemo.tools;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by neogineer on 25/10/16.
 *
 * DndTool = Drag'n'Drop tool.
 */

public class DndTool extends Tool {

    public DndTool(World world, OrthographicCamera camera, Body groundBody) {
        super(world, camera, groundBody);
        this.groundBody = groundBody;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        super.touchDown(screenX, screenY, pointer, button);

        // if we hit something we create a new mouse joint
        // and attach it to the hit body.


        return false;
    }


}
