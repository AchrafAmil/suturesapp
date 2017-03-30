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

import java.util.HashMap;

/**
 * Created by neogineer on 26/01/17.
 */
public class HudStage extends Stage {

    public final SettingTool callback;
    private final HashMap<String,ImageButton> buttons = new HashMap<>();

    private Tool.Tools selectedTool = Tool.Tools.Move;

    public HudStage(final SettingTool callback){
        this.callback = callback;


        setupButtons();


        InputMultiplexer ip = (InputMultiplexer) Gdx.input.getInputProcessor();
        ip.addProcessor(this);
    }

    private void setupButtons() {
        final ImageButton cutButton = createButton("cutButton.png","cutButtonDown.png");
        cutButton.setPosition(buttonPosition(1).x, buttonPosition(1).y);
        addActor(cutButton);
        cutButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                HudStage.this.clicked(cutButton, Tool.Tools.Cut);
            }
        });
        this.buttons.put("cutButton",cutButton);

        final ImageButton closeButton = createButton("closeButton.png","closeButtonDown.png");
        closeButton.setPosition(buttonPosition(2).x, buttonPosition(2).y);
        addActor(closeButton);
        closeButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                HudStage.this.clicked(closeButton, Tool.Tools.Close);
            }
        });
        this.buttons.put("closeButton",closeButton);

        final ImageButton connectButton = createButton("connectButton.png","connectButtonDown.png");
        connectButton.setPosition(buttonPosition(3).x, buttonPosition(3).y);
        addActor(connectButton);
        connectButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                HudStage.this.clicked(connectButton, Tool.Tools.Connect);
            }
        });
        this.buttons.put("connectButton",connectButton);

        final ImageButton tumorButton = createButton("tumorButton.png","tumorButtonDown.png");
        tumorButton.setPosition(buttonPosition(5).x, buttonPosition(5).y);
        addActor(tumorButton);
        tumorButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                HudStage.this.clicked(tumorButton, Tool.Tools.Tumor);
            }
        });
        this.buttons.put("tumorButton",tumorButton);

        final ImageButton screenshotButton = createButton("screenshotButton.png","screenshotButtonDown.png");
        screenshotButton.setPosition(buttonPosition(6).x, buttonPosition(6).y);
        addActor(screenshotButton);
        screenshotButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                ((SmallIntestineDemoGame) Gdx.app.getApplicationListener()).nativePlatform.showMessage("-Sutures Annotations- \n    ...coming soon");
                screenshotButton.setChecked(false);
                /*setVisible(false);
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
                },0.5f);*/
            }
        });
        this.buttons.put("screenshotButton",screenshotButton);

        final ImageButton trashButton = createButton("trashButton.png","trashButtonDown.png");
        trashButton.setPosition(buttonPosition(4).x, buttonPosition(4).y);
        addActor(trashButton);
        trashButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                HudStage.this.clicked(trashButton, Tool.Tools.Trash);

            }
        });
        this.buttons.put("trashButton",trashButton);


        final ImageButton pathologyButton = createButton("pathologyButton.png","pathologyButtonDown.png");
        pathologyButton.setPosition(buttonPosition(8).x, buttonPosition(8).y);
        addActor(pathologyButton);
        pathologyButton.addListener(new ClickListener(){
            boolean isOnPathologyBox = false;
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "oh clicked! x"+x+"  y:"+y);
                super.clicked(event, x, y);
                ((SmallIntestineDemoGame) Gdx.app.getApplicationListener()).nativePlatform.showMessage("-External View- \n ...coming soon");
                pathologyButton.setChecked(false);
                /*if(isOnPathologyBox)
                    callback.showWorkspace();
                else
                    callback.showPathology();
                isOnPathologyBox= !isOnPathologyBox;*/
            }
        });
        this.buttons.put("pathologyButton",pathologyButton);

    }

    public void setVisible(boolean visible){
        for(ImageButton b : this.buttons.values())
            b.setVisible(visible);
    }

    public void showPathologyBoxButtons(){
        this.setVisible(false);
        this.buttons.get("pathologyButton").setVisible(true);
        this.buttons.get("screenshotButton").setVisible(true);
    }

    public void showWorkspaceButtons(){
        this.setVisible(true);
    }

    /**
     * @return true if at least one of the buttons are visible
     */
    public boolean isVisible(){
        for(ImageButton b : this.buttons.values())
            if(b.isVisible())
                return true;
        return false;
    }

    private static ImageButton createButton(String pathUp, String pathDown){
        Texture texUp = new Texture(Gdx.files.internal(pathUp));
        TextureRegion texRegUp = new TextureRegion(texUp);
        TextureRegionDrawable rexRegDrawUp = new TextureRegionDrawable(texRegUp);

        Texture texDown = new Texture(Gdx.files.internal(pathDown));
        TextureRegion texRegDown = new TextureRegion(texDown);
        TextureRegionDrawable rexRegDrawDown = new TextureRegionDrawable(texRegDown);
        ImageButton button = new ImageButton(rexRegDrawUp, rexRegDrawDown, rexRegDrawDown);
        button.setSize(getButtonSize().x, getButtonSize().y);
        return button;
    }

    static Vector2 tmpVec = new Vector2();

    /**
     * screen buttons id:
     * 8.7........6
     * .
     * .
     * .
     * .
     * 1.2.3....4.5
     *
     * @param id
     * @return
     */
    public static Vector2 buttonPosition(int id){
        int width = Gdx.graphics.getWidth();
        int height = Gdx.graphics.getHeight();

        int x=5,y=5;

        int marge = (int) (width * 0.02f);

        switch (id){
            case 1:
            case 2:
            case 3:
                x = (int) (marge*(id)+getButtonSize().x*(id-1));
                break;
            case 4:
                x = (int) (marge*(id)+getButtonSize().x*(5));
                break;
            case 5:
                x = (int) (marge*(id)+getButtonSize().x*(6));
                break;

            case 6:
                x = (int) (marge*4+getButtonSize().x*6);
                y = (int) (height*0.99f - getButtonSize().y);
                break;
            case 7:
                x = (int) (marge*2+getButtonSize().x*1);
                y = (int) (height*0.99f - getButtonSize().y);
                break;
            case 8:
                x = marge;
                y = (int) (height*0.99f - getButtonSize().y);
        }

        return tmpVec.set(x,y);
    }

    public static Vector2 getButtonSize(){
        int x = (int) (Gdx.graphics.getWidth() * 0.12f) ;
        return tmpVec.set(x,x);
    }

    private void clicked(ImageButton button, Tool.Tools tool){
        if(selectedTool==tool){
            // unselecting the tool
            callback.setTool(Tool.Tools.Move);
            selectedTool= Tool.Tools.Move;
        }
        else{
            if(selectedTool== Tool.Tools.Move){
                //nothing selected
                callback.setTool(tool);
                selectedTool = tool;
            }else{
                for(ImageButton ib : buttons.values())
                    ib.setChecked(false);
                button.setChecked(true);
                callback.setTool(tool);
                selectedTool = tool;
            }
        }

    }

}
