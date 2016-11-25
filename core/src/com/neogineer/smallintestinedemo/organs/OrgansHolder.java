package com.neogineer.smallintestinedemo.organs;

import com.neogineer.smallintestinedemo.organs.liver.Liver;
import com.neogineer.smallintestinedemo.organs.stomach.Stomach;

/**
 * Created by neogineer on 02/11/16.
 *
 * just to keep a reference of all the organs, and call some methods upon all (or some) of them.
 */
public class OrgansHolder {

    public SmallIntestine smallIntestine;

    public Liver liver ;

    public Stomach stomach;

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
}
