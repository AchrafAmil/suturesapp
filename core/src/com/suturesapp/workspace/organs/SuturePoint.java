package com.suturesapp.workspace.organs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.suturesapp.workspace.organs.rope.RopeOrganPart;
import com.suturesapp.workspace.organs.rope.SmallIntestine;
import com.suturesapp.workspace.tools.ConnectTool;

/**
 * Created by neogineer on 24/10/16.
 */
public class SuturePoint {

    //meters
    Vector2 localCoord ;

    OrganPart relatedOrganPart;

    //radian
    float rotation ;

    RevoluteJoint relatedJoint ;

    boolean visible;

    Sprite backgroundedSprite;

    Sprite transparentSprite;

    State state = State.BACKGROUNDED;

    public SuturePoint(){
        Texture tx = new Texture(Gdx.files.internal("suturePoint.png"));
        this.backgroundedSprite = new Sprite(tx);
    }

    public Sprite getSprite(){
        if(!visible)
            return null;
        if(state==State.BACKGROUNDED)
            return backgroundedSprite;
        else
            return transparentSprite;
    }

    /**
     * A suture point is bewteen two organ parts, and helt by one of them (this.relatedOrganPart)
     * @return the second organ part.
     */
    public OrganPart getTheOtherOrganPart(){
        OrganPart organPart;

        if(this.getRelatedJoint().getBodyA()!=this.relatedOrganPart.body)
            return (OrganPart) this.getRelatedJoint().getBodyA().getUserData();
        else
            return (OrganPart) this.getRelatedJoint().getBodyB().getUserData();
    }

    public void lock(boolean lock){
        //only for ropes
        if(relatedOrganPart instanceof RopeOrganPart){
            if(lock){
                float angle = relatedJoint.getJointAngle();
                getRelatedJoint().setLimits(angle,angle);
            }
            else {
                getRelatedJoint().setLimits(-(float) ConnectTool.SMALLINTESTINE_MAX_ANGLE, (float) ConnectTool.SMALLINTESTINE_MAX_ANGLE);
            }
        }
    }




    public Vector2 getLocalCoord() {
        return localCoord;
    }

    public SuturePoint setLocalCoord(Vector2 localCoord) {
        this.localCoord = localCoord;
        return this;
    }

    public float getRotation() {
        return rotation;
    }

    public SuturePoint setRotation(float rotation) {
        this.rotation = rotation;
        return this;
    }

    public RevoluteJoint getRelatedJoint() {
        return relatedJoint;
    }

    public SuturePoint setRelatedJoint(RevoluteJoint relatedJoint) {
        this.relatedJoint = relatedJoint;
        return this;
    }

    public boolean isVisible() {
        return visible;
    }

    public SuturePoint setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isNoXY(){
        return localCoord.x!=0 && localCoord.y!=0;
    }

    public Sprite getBackgroundedSprite() {
        return backgroundedSprite;
    }

    public SuturePoint setBackgroundedSprite(Sprite backgroundedSprite) {
        this.backgroundedSprite = backgroundedSprite;
        return this;
    }

    /**
     *
     * @param backgrounded false == white bg ; true == transparent bg.
     */
    public SuturePoint setTransparent(boolean backgrounded){
        if(!backgrounded)
            return this;
        Texture tx = new Texture(Gdx.files.internal("SuturePointTransparent.png"));
        this.transparentSprite = new Sprite(tx);
        this.state = State.TRANSPARENT;
        return this;
    }

    public Sprite getTransparentSprite() {
        return transparentSprite;
    }

    public SuturePoint setTransparentSprite(Sprite transparentSprite) {
        this.transparentSprite = transparentSprite;
        return this;
    }

    public void setRelatedOrganPart(OrganPart relatedOrganPart) {
        this.relatedOrganPart = relatedOrganPart;
    }

    public void free(){
        try{
            this.backgroundedSprite.getTexture().dispose();
            this.transparentSprite.getTexture().dispose();
        }catch (NullPointerException npe){
        }
    }

    enum State{BACKGROUNDED, TRANSPARENT}
}
