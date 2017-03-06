package com.neogineer.smallintestinedemo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Timer;
import com.neogineer.smallintestinedemo.tools.Tool;

import java.util.ArrayList;

/**
 * Created by neogineer on 26/01/17.
 */
public class HudStage extends Stage {

    public final SettingTool callback;
    private final ArrayList<ImageButton> buttons = new ArrayList<>();

    public HudStage(final SettingTool callback){
        this.callback = callback;


        setupButtons();


        InputMultiplexer ip = (InputMultiplexer) Gdx.input.getInputProcessor();
        ip.addProcessor(this);
    }

    private void setupButtons() {
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
        this.buttons.add(cutButton);

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
        this.buttons.add(closeButton);

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
        this.buttons.add(connectButton);

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
        this.buttons.add(moveButton);

        ImageButton tumorButton = createButton("tumorButton.png");
        tumorButton.setPosition(buttonPosition(5).x, buttonPosition(5).y);
        addActor(tumorButton);
        tumorButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Tumor);
            }
        });
        this.buttons.add(tumorButton);

        ImageButton screenshotButton = createButton("screenshotButton.png");
        screenshotButton.setPosition(buttonPosition(6).x, buttonPosition(6).y);
        addActor(screenshotButton);
        screenshotButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                setVisible(false);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((SmallIntestineDemoGame) Gdx.app.getApplicationListener()).nativePlatform.saveScreenshot(Gdx.files.external("Sutures/pica.png"));
                    }
                },0.3f);
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        setVisible(true);
                    }
                },0.5f);
            }
        });
        this.buttons.add(screenshotButton);

        ImageButton trashButton = createButton("trashButton.png");
        trashButton.setPosition(buttonPosition(7).x, buttonPosition(7).y);
        addActor(trashButton);
        trashButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                callback.setTool(Tool.Tools.Trash);
            }
        });
        this.buttons.add(trashButton);

    }

    public void setVisible(boolean visible){
        for(ImageButton b : this.buttons)
            b.setVisible(visible);
    }

    /**
     * @return true if at least one of the buttons are visible
     */
    public boolean isVisible(){
        for(ImageButton b : this.buttons)
            if(b.isVisible())
                return true;
        return false;
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

        int x=5,y=5;

        int marge = (int) (width * 0.02f);

        switch (id){
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                x = (int) (marge*(id)+getButtonSize().x*(id-1));
                break;
            case 6:
                x = (int) (marge*4+getButtonSize().x*4);
                y = (int) (height*0.99f - getButtonSize().y);
                break;
            case 7:
                x = marge;
                y = (int) (height*0.99f - getButtonSize().y);
        }

        return tmpVec.set(x,y);
    }

    public static Vector2 getButtonSize(){
        int x = (int) (Gdx.graphics.getWidth() * 0.175f) ;
        return tmpVec.set(x,x);
    }

}