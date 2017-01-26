package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.neogineer.smallintestinedemo.tools.Tool;

/**
 * Created by neogineer on 26/01/17.
 */
public class HudStage extends Stage {

    public final SettingTool callback;

    public HudStage(final SettingTool callback){
        this.callback = callback;


        ImageButton cutButton = createButton("cutButton.png");
        cutButton.setPosition(10,5);
        addActor(cutButton);
        cutButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Cut);
            }
        });

        ImageButton closeButton = createButton("closeButton.png");
        closeButton.setPosition(100,5);
        addActor(closeButton);
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Close);
            }
        });

        ImageButton connectButton = createButton("connectButton.png");
        connectButton.setPosition(200,5);
        addActor(connectButton);
        connectButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Connect);
            }
        });

        ImageButton moveButton = createButton("moveButton.png");
        moveButton.setPosition(300,5);
        addActor(moveButton);
        moveButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Move);
            }
        });




        InputMultiplexer ip = (InputMultiplexer) Gdx.input.getInputProcessor();
        ip.addProcessor(this);
    }

    private static ImageButton createButton(String path){
        Texture myTexture = new Texture(Gdx.files.internal(path));
        TextureRegion myTextureRegion = new TextureRegion(myTexture);
        TextureRegionDrawable myTexRegionDrawable = new TextureRegionDrawable(myTextureRegion);
        ImageButton button = new ImageButton(myTexRegionDrawable);
        button.setSize(80,80);
        return button;
    }

}
