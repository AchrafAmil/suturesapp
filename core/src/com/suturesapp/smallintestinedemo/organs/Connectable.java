package com.suturesapp.smallintestinedemo.organs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import java.util.ArrayList;

/**
 * Created by neogineer on 24/10/16.
 */
public interface Connectable {

    boolean addSuturePoint(SuturePoint sp);

    boolean deleteSuturePoint(SuturePoint sp);

    boolean deleteSuturePoint(RevoluteJoint joint);

    ArrayList<SuturePoint> getSuturePoints();

    /**
     * can we suture at that point?
     */
    boolean connectionIntent(Vector2 point);

    /**
     * each organ will look for it's body's anchor inside the def and correct it
     */
    void correctJointDef(RevoluteJointDef def);

    /**
     * changes the suture point coordinates (anchor) and rotation to fit the closest possible values.
     * some organs won't change anything, they accept all possibilities.
     * some other organs, like the small intestine will change the rotation for instance to either 0 or PI/2
     * @return the same updated suture point (for chaining).
     */
    SuturePoint correctAnchorAndRotation(SuturePoint suturePoint);

}
