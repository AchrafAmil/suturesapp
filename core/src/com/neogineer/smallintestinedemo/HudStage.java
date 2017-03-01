package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
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
        cutButton.setPosition(buttonPosition(1).x, buttonPosition(1).y);
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
        closeButton.setPosition(buttonPosition(2).x, buttonPosition(2).y);
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
        connectButton.setPosition(buttonPosition(3).x, buttonPosition(3).y);
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
        moveButton.setPosition(buttonPosition(4).x, buttonPosition(4).y);
        addActor(moveButton);
        moveButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Move);
            }
        });

        ImageButton trashButton = createButton("trashButton.png");
        trashButton.setPosition(buttonPosition(5).x, buttonPosition(5).y);
        addActor(trashButton);
        trashButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Trash);
            }
        });

        ImageButton reloadButton = createButton("trashButton.png");
        reloadButton.setPosition(buttonPosition(6).x, buttonPosition(6).y);
        addActor(reloadButton);
        reloadButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.load();
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
        button.setSize(getButtonSize().x, getButtonSize().y);
        return button;
    }

    static Vector2 tmpVec = new Vector2();
    public static Vector2 buttonPosition(int id){
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        int x,y=5;

        int marge = (int) (width * 0.025f);

        if(id<=5)
            x = (int) (marge*(id)+getButtonSize().x*(id-1));
        else{
            x = (int) (marge*5+getButtonSize().x*4);
            y = (int) (height*0.95f - getButtonSize().y);
        }


        return tmpVec.set(x,y);
    }

    public static Vector2 getButtonSize(){
        int x = (int) (Gdx.graphics.getWidth() * 0.175f) ;
        return tmpVec.set(x,x);
    }

}
