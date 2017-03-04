package com.suturesapp.workspace.organs;


import java.util.List;

/**
 * Created by neogineer on 24/10/16.
 */
public interface Openable {

    /**
     * Please provide world coordinates, the method will translate to local coordinates.
     * @return the openable side that contains the indicated point.
     */
    boolean close(float x, float y);

    /**
     * Please provide world coordinates, the method will translate to local coordinates.
     * @return the openable side that contains the indicated point.
     */
    boolean open(float x, float y);

    /**
     * Please provide world coordinates, the method will translate to local coordinates.
     * @return the openable side that contains the indicated point.
     */
    boolean normal(float x, float y);

    /**
     * Please provide world coordinates, the method will translate to local coordinates.
     * @return the openable side that contains the indicated point.
     */
    OpenableSide getOpenableSide(float x, float y);

    List<OpenableSide> getOpenableSides();

}
