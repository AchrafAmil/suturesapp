package com.suturesapp.workspace;

import com.suturesapp.workspace.tools.Tool;

/**
 * Created by neogineer on 26/01/17.
 */
public interface SettingTool {

    void setTool(Tool.Tools tool);

    void load();

    void showPathology();

    void showWorkspace();
}
