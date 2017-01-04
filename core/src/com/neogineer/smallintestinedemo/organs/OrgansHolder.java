package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.neogineer.smallintestinedemo.organs.Esophagus.Esophagus;
import com.neogineer.smallintestinedemo.organs.Esophagus.EsophagusOrganPart;
import com.neogineer.smallintestinedemo.organs.duedenum.Duodenum;
import com.neogineer.smallintestinedemo.organs.liver.Liver;
import com.neogineer.smallintestinedemo.organs.stomach.Stomach;
import com.neogineer.smallintestinedemo.organs.stomach.StomachOrganPart;
import com.neogineer.smallintestinedemo.tools.ConnectTool;
import com.neogineer.smallintestinedemo.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by neogineer on 02/11/16.
 *
 * just to keep a reference of all the organs, and call some methods upon all (or some) of them.
 */
public class OrgansHolder {

    public static SmallIntestine smallIntestine;

    public static Liver liver ;

    public static Stomach stomach;

    public static Duodenum duodenum;

    public static Esophagus esophagus;

    public static World world;
    public static OrthographicCamera camera;

    public void start(Stage stage){
        if(world==null || camera==null)
            throw new RuntimeException("Holder can't start, World and/or Camera are null");

        this.liver = new Liver(world, camera);
        stage.addActor(this.liver);

        this.stomach = new Stomach(world, camera);
        stage.addActor(this.stomach);

        this.duodenum = new Duodenum(world, camera);
        stage.addActor(this.duodenum);

        this.smallIntestine = new SmallIntestine(world, camera);
        stage.addActor(this.smallIntestine);

        this.esophagus = new Esophagus(world, camera);
        stage.addActor(this.esophagus);

        setupExternalJoints(world, camera);
    }

    private void setupExternalJoints(World world, OrthographicCamera camera){

        JSONArray joints = Utils.loadJoints("external_joints.json");

        for(int i=0; i<joints.length(); i++){
            JSONObject joint = (JSONObject) joints.get(i);
            ConnectTool tool = new ConnectTool(world, camera);
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();

            Organ organA = this.organFromName(joint.getString("organA"));
            Organ organB = this.organFromName(joint.getString("organB"));

            connector.organA = organA.organParts.get(joint.getString("organPartA"));
            connector.organB = organB.organParts.get(joint.getString("organPartB"));

            connector.anchorA = connector.organA.getVertex(joint.getDouble("anchorAx"), joint.getDouble("anchorAy"));
            connector.anchorB = connector.organB.getVertex(joint.getDouble("anchorBx"), joint.getDouble("anchorBy"));

            connector.makeConnection(false);

        }


    }

    // TODO: 31/12/16 multi-thread this, it may disturb UI Thread
    public void saveState(Kryo kryo, Output output){
        smallIntestine.saveState(kryo, output);
        liver.saveState(kryo, output);
        stomach.saveState(kryo, output);
        duodenum.saveState(kryo, output);
        esophagus.saveState(kryo, output);
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
        smallIntestine.loadState(kryo, input);
        liver.loadState(kryo, input);
        stomach.loadState(kryo, input);
        duodenum.loadState(kryo, input);
        esophagus.loadState(kryo, input);
        loadJoints(kryo, input);
    }

    private void loadJoints(Kryo kryo, Input input){
        int size = input.readInt();
        Array<SuturePointDef> spDefs = new Array<>(size);
        for(int i=0; i<size; i++){
            SuturePointDef spDef = kryo.readObject(input, SuturePointDef.class);
            spDefs.add(spDef);
        }

        ConnectTool tool = new ConnectTool(world, camera);
        for(SuturePointDef spDef : spDefs){
            ConnectTool.ConnectToolHelper connector = tool.new ConnectToolHelper();
            connector.proceed(spDef);
            Gdx.app.log("Suturing", "sutured :"+spDef.organA+"("+spDef.idA+") with "+spDef.organB+"("+spDef.idB+")");
        }
    }

    public void highlight(){
        // TODO: 02/11/16 iterate over all organs.
        try {
            //smallIntestine.highlightCutting();
            liver.highlightCutting();
        }catch (NullPointerException npe){

        }
    }

    public void unhighlight(){
        // TODO: 02/11/16 iterate over all organs.
        try {
            //smallIntestine.unhighlightCutting();
            liver.unhighlightCutting();
        }catch (NullPointerException npe){

        }
    }

    public static Organ organFromName(String name){
        switch (name){
            case "Liver":
                return liver;
            case "Stomach":
                return stomach;
            case "Duodenum":
                return duodenum;
            case "Esophagus":
                return esophagus;
            case "SmallIntestine":
                return smallIntestine;
            default:
                return null;
        }
    }

    /**
     * Existing OrganPart instance from spDef data.
     * @param spDef to get fullIdentifier from (and id perhaps)
     * @param a A or B ? (true for A)
     */
    public static OrganPart organPartFromSpDef(SuturePointDef spDef, boolean a){
        if(a){
            if(spDef.organA.contains("SmallIntestine"))
                return organPartFromFullIdentifier(spDef.organA, spDef.idA);
            else
                return organPartFromFullIdentifier(spDef.organA);
        }else {
            if(spDef.organB.contains("SmallIntestine"))
                return organPartFromFullIdentifier(spDef.organB, spDef.idB);
            else
                return organPartFromFullIdentifier(spDef.organB);
        }
    }

    private static OrganPart organPartFromFullIdentifier(String fullIdentifier){
        String[] parts = fullIdentifier.split("OrganPart");
        Organ org = organFromName(parts[0]);
        return org.organParts.get(parts[1]);
    }

    private static OrganPart organPartFromFullIdentifier(String fullIdentifier, int id){
        String[] parts = fullIdentifier.split("OrganPart");
        Organ org = organFromName(parts[0]);
        return org.organParts.get(""+id);
    }
}
