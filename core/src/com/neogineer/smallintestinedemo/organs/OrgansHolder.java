package com.neogineer.smallintestinedemo.organs;

/**
 * Created by neogineer on 02/11/16.
 *
 * just to keep a reference of all the organs, and call some methods upon all (or some) of them.
 */
public class OrgansHolder {

    public SmallIntestine smallIntestine;

    public void highlight(){
        // TODO: 02/11/16 iterate over all organs.
        try {
            smallIntestine.highlightCutting();
        }catch (NullPointerException npe){

        }
    }

    public void unhighlight(){
        // TODO: 02/11/16 iterate over all organs.
        try {
            smallIntestine.unhighlightCutting();
        }catch (NullPointerException npe){

        }
    }
}
