package com.neogineer.smallintestinedemo.organs;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

        //setupExternalJoints(world, camera);
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

    public void saveState(Kryo kryo, Output output){
        smallIntestine.saveState(kryo, output);
        liver.saveState(kryo, output);
        stomach.saveState(kryo, output);
        duodenum.saveState(kryo, output);
        esophagus.saveState(kryo, output);
    }

    public void loadState(Kryo kryo, Input input){
        smallIntestine.loadState(kryo, input);
        liver.loadState(kryo, input);
        stomach.loadState(kryo, input);
        duodenum.loadState(kryo, input);
        esophagus.loadState(kryo, input);
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
}
