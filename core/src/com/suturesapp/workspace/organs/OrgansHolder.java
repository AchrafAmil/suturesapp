package com.suturesapp.workspace.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.suturesapp.workspace.GameStage;
import com.suturesapp.workspace.organs.abdominalwall.AbdominalWall;
import com.suturesapp.workspace.organs.appendix.Appendix;
import com.suturesapp.workspace.organs.bileduct.BileDuct;
import com.suturesapp.workspace.organs.duedenum.Duodenum;
import com.suturesapp.workspace.organs.esophagus.Esophagus;
import com.suturesapp.workspace.organs.gallbladder.Gallbladder;
import com.suturesapp.workspace.organs.pancreas.Pancreas;
import com.suturesapp.workspace.organs.rectum.Rectum;
import com.suturesapp.workspace.organs.rope.Colon;
import com.suturesapp.workspace.organs.rope.SmallIntestine;
import com.suturesapp.workspace.organs.spleen.Spleen;
import com.suturesapp.workspace.organs.stomach.Stomach;
import com.suturesapp.workspace.tools.ConnectTool;
import com.suturesapp.workspace.organs.liver.Liver;
import com.suturesapp.workspace.utils.Constants;
import com.suturesapp.workspace.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by neogineer on 02/11/16.
 *
 * just to keep a reference of all the organs, and call some methods upon all (or some) of them.
 */
public class OrgansHolder {

    public static HashMap<String, com.suturesapp.workspace.organs.Organ> allOrgans = new HashMap<>();

    public AbdominalWall abdominalWall;

    public Esophagus esophagus;

    public BileDuct bileDuct;

    public Gallbladder gallbladder;

    public Pancreas pancreas;

    public Duodenum duodenum;

    public Rectum rectum;

    public Stomach stomach;

    public Colon colon;

    public Appendix appendix;

    public SmallIntestine smallIntestine;

    public Liver liver ;

    public Spleen spleen ;

    public com.suturesapp.workspace.organs.abdominalconnector.AbdominalConnector abdominalConnector;

    public static World world;
    public static OrthographicCamera camera;

    public void start(final GameStage stage){
        if(world==null || camera==null)
            throw new RuntimeException("Holder can't start, World and/or Camera are null");

        this.abdominalWall = new AbdominalWall(world, camera);
        stage.addActor(this.abdominalWall);
        allOrgans.put("AbdominalWall", abdominalWall);

        this.abdominalConnector = new com.suturesapp.workspace.organs.abdominalconnector.AbdominalConnector(world, camera);
        stage.addActor(this.abdominalConnector);
        allOrgans.put("AbdominalConnector", abdominalConnector);

        this.esophagus = new Esophagus(world, camera);
        stage.addActor(this.esophagus);
        allOrgans.put("Esophagus", esophagus);

        this.bileDuct = new BileDuct(world, camera);
        stage.addActor(this.bileDuct);
        allOrgans.put("BileDuct", bileDuct);

        this.duodenum = new Duodenum(world, camera);
        stage.addActor(this.duodenum);
        allOrgans.put("Duodenum", duodenum);

        this.gallbladder = new Gallbladder(world, camera);
        stage.addActor(this.gallbladder);
        allOrgans.put("Gallbladder", gallbladder);

        this.pancreas = new Pancreas(world, camera);
        stage.addActor(this.pancreas);
        allOrgans.put("Pancreas", pancreas);

        this.rectum = new Rectum(world, camera);
        stage.addActor(this.rectum);
        allOrgans.put("Rectum", rectum);

        this.stomach = new Stomach(world, camera);
        stage.addActor(this.stomach);
        allOrgans.put("Stomach", stomach);

        this.colon = new Colon(world, camera);
        stage.addActor(this.colon);
        allOrgans.put("Colon", colon);

        this.appendix = new Appendix(world, camera);
        stage.addActor(this.appendix);
        allOrgans.put("Appendix", appendix);

        this.smallIntestine = new SmallIntestine(world, camera);
        stage.addActor(this.smallIntestine);
        allOrgans.put("SmallIntestine", smallIntestine);

        this.liver = new Liver(world, camera);
        stage.addActor(this.liver);
        allOrgans.put("Liver", liver);

        this.spleen = new Spleen(world, camera);
        stage.addActor(this.spleen);
        allOrgans.put("Spleen", spleen);


        //setupExternalJoints(world, camera);

        stage.load();

        //setupAdditionalExternalJoints(world, camera);


        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(600);
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            camera.translate(Constants.CAMERA_INITIAL_TRANSLATION);
                            camera.update();
                            ((com.suturesapp.workspace.SmallIntestineDemoGame) Gdx.app.getApplicationListener()).nativePlatform.loadingFinished();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private void setupExternalJoints(World world, OrthographicCamera camera){

        JSONArray joints = Utils.loadJoints("external_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint;try{joint = (JSONObject) joints.get(i);}catch(Exception e){continue;}
            ConnectTool tool = new ConnectTool(world, camera, null);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            com.suturesapp.workspace.organs.Organ organA = this.organFromName(joint.getString("organA"));
            com.suturesapp.workspace.organs.Organ organB = this.organFromName(joint.getString("organB"));

            connector.organA = organA.organParts.get(joint.getString("organPartA"));
            connector.organB = organB.organParts.get(joint.getString("organPartB"));

            connector.anchorA = connector.organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = connector.organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);

        }


    }

    private void setupAdditionalExternalJoints(World world, OrthographicCamera camera){

        JSONArray joints = Utils.loadJoints("additional_external_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint;try{joint = (JSONObject) joints.get(i);}catch(Exception e){continue;}
            ConnectTool tool = new ConnectTool(world, camera, null);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            com.suturesapp.workspace.organs.Organ organA = this.organFromName(joint.getString("organA"));
            com.suturesapp.workspace.organs.Organ organB = this.organFromName(joint.getString("organB"));

            connector.organA = organA.organParts.get(joint.getString("organPartA"));
            connector.organB = organB.organParts.get(joint.getString("organPartB"));

            connector.anchorA = connector.organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = connector.organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);

        }


    }

    // TODO: 31/12/16 multi-thread this, it may disturb UI Thread
    public void saveState(Kryo kryo, Output output){
        abdominalWall.saveState(kryo, output);
        esophagus.saveState(kryo, output);
        bileDuct.saveState(kryo, output);
        gallbladder.saveState(kryo, output);
        pancreas.saveState(kryo, output);
        duodenum.saveState(kryo, output);
        rectum.saveState(kryo, output);
        stomach.saveState(kryo, output);
        colon.saveState(kryo, output);
        appendix.saveState(kryo, output);
        smallIntestine.saveState(kryo, output);
        liver.saveState(kryo, output);
        spleen.saveState(kryo, output);
        abdominalConnector.saveState(kryo, output);
        saveJoints(kryo, output);
    }

    private void saveJoints(Kryo kryo, Output output){
        Array<Joint> joints = new Array<>(world.getJointCount());
        // TODO: 31/12/16 order joints (especially smallintestine ones)
        world.getJoints(joints);
        output.writeInt(joints.size);
        List<SuturePointDef> spDefs = new ArrayList<>();
        for(Joint joint : joints)
            spDefs.add(Utils.jointToSpDef(joint));
        Collections.sort(spDefs);
        for(SuturePointDef spDef: spDefs)
            kryo.writeObject(output, spDef);
    }


    public void loadState(Kryo kryo, Input input){
        com.suturesapp.workspace.organs.OrganPart.INSTANCES = 0 ;
        abdominalWall.loadState(kryo, input);
        esophagus.loadState(kryo, input);
        bileDuct.loadState(kryo, input);
        gallbladder.loadState(kryo, input);
        pancreas.loadState(kryo, input);
        duodenum.loadState(kryo, input);
        rectum.loadState(kryo, input);
        stomach.loadState(kryo, input);
        colon.loadState(kryo, input);
        appendix.loadState(kryo, input);
        smallIntestine.loadState(kryo, input);
        liver.loadState(kryo, input);
        spleen.loadState(kryo, input);
        abdominalConnector.loadState(kryo, input);
        loadJoints(kryo, input);
        updateOpenableSides();
    }

    /*
    Should be called from a different thread (not the render one)
     */
    public void loadStateThreadSafe(final Kryo kryo, final Input input) throws InterruptedException {

        Thread.sleep(5000);


        com.suturesapp.workspace.organs.OrganPart.INSTANCES = 0 ;
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                abdominalWall.loadState(kryo, input);
                esophagus.loadState(kryo, input);
                duodenum.loadState(kryo, input);
                rectum.loadState(kryo, input);
                stomach.loadState(kryo, input);
            }
        });
        Thread.sleep(5000);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                colon.loadState(kryo, input);
                appendix.loadState(kryo, input);
                smallIntestine.free();
            }
        });
        Thread.sleep(5000);
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                smallIntestine.loadState(kryo, input, 1);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(12000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                smallIntestine.loadState(kryo, input, 2);
                                liver.loadState(kryo, input);
                                loadJoints(kryo, input);
                                updateOpenableSides();
                                input.close();
                            }
                        });
                    }
                }.start();
            }
        });

//        Thread.sleep(100);
//        Gdx.app.postRunnable(new Runnable() {
//            @Override
//            public void run() {
//                liver.loadState(kryo, input);
//                loadJoints(kryo, input);
//                updateOpenableSides();
//                input.close();
//            }
//        });
    }


    private void updateOpenableSides() {
        abdominalWall.loadBufferedOpenableSides();
        esophagus.loadBufferedOpenableSides();
        bileDuct.loadBufferedOpenableSides();
        gallbladder.loadBufferedOpenableSides();
        pancreas.loadBufferedOpenableSides();
        duodenum.loadBufferedOpenableSides();
        rectum.loadBufferedOpenableSides();
        stomach.loadBufferedOpenableSides();
        colon.loadBufferedOpenableSides();
        appendix.loadBufferedOpenableSides();
        smallIntestine.loadBufferedOpenableSides();
        liver.loadBufferedOpenableSides();
        spleen.loadBufferedOpenableSides();
        abdominalConnector.loadBufferedOpenableSides();
    }

    private void loadJoints(Kryo kryo, Input input){
        int size = input.readInt();
        Array<SuturePointDef> spDefs = new Array<>(size);
        for(int i=0; i<size; i++){
            SuturePointDef spDef = kryo.readObject(input, SuturePointDef.class);
            spDefs.add(spDef);
        }

        ConnectTool tool = new ConnectTool(world, camera, null);
        for(SuturePointDef spDef : spDefs){
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();
            connector.proceed(spDef);
            Gdx.app.log("Suturing", "sutured :"+spDef.getOrganA()+"("+spDef.getIdA()+") with "+spDef.getOrganB()+"("+spDef.getIdB()+")");
        }
    }

    public void highlight(){
        try {
            for(com.suturesapp.workspace.organs.Organ organ : allOrgans.values())
                organ.highlightCutting();
        }catch (NullPointerException npe){

        }
    }

    public void unhighlight(){
        try {
            for(com.suturesapp.workspace.organs.Organ organ : allOrgans.values())
                organ.unhighlightCutting();
        }catch (NullPointerException npe){

        }
    }

    public static com.suturesapp.workspace.organs.Organ organFromName(String name){
        return allOrgans.get(name);
//        switch (name){
//            case "AbdominalWall":
//                return abdominalWall;
//            case "Esophagus":
//                return esophagus;
//            case "Duodenum":
//                return duodenum;
//            case "Rectum":
//                return rectum;
//            case "Stomach":
//                return stomach;
//            case "Colon":
//                return colon;
//            case "Appendix":
//                return appendix;
//            case "SmallIntestine":
//                return smallIntestine;
//            case "Liver":
//                return liver;
//            default:
//                return null;
//        }
    }

    /**
     * Existing OrganPart instance from spDef data.
     * @param spDef to get fullIdentifier from (and id perhaps)
     * @param a A or B ? (true for A)
     */
    public static com.suturesapp.workspace.organs.OrganPart organPartFromSpDef(SuturePointDef spDef, boolean a){
        if(a){
            if(spDef.getOrganA().contains("SmallIntestine") || spDef.getOrganA().contains("Colon"))
                return organPartFromFullIdentifier(spDef.getOrganA(), spDef.getIdA());
            else
                return organPartFromFullIdentifier(spDef.getOrganA());
        }else {
            if(spDef.getOrganB().contains("SmallIntestine") || spDef.getOrganB().contains("Colon"))
                return organPartFromFullIdentifier(spDef.getOrganB(), spDef.getIdB());
            else
                return organPartFromFullIdentifier(spDef.getOrganB());
        }
    }

    private static com.suturesapp.workspace.organs.OrganPart organPartFromFullIdentifier(String fullIdentifier){
        String[] parts = fullIdentifier.split("OrganPart");
        com.suturesapp.workspace.organs.Organ org = organFromName(parts[0]);
        return org.organParts.get(parts[1]);
    }

    private static com.suturesapp.workspace.organs.OrganPart organPartFromFullIdentifier(String fullIdentifier, int id){
        String[] parts = fullIdentifier.split("OrganPart");
        com.suturesapp.workspace.organs.Organ org = organFromName(parts[0]);
        return org.organParts.get(""+id);
    }
}
