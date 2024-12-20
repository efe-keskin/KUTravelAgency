package constants;

import java.awt.*;

public class Constants {
    //file paths
    public static final String LOGIN_IMAGE_PATH = "resources/profile2.png";
    public static final String ADMIN_DB_PATH = "databases/admindb.txt";
    public static final String DB_PATH = "databases/customerdb.txt";
    //frame config
    public static final Dimension FRAME_SIZE = new Dimension(540,760);
    public static final Dimension TEXTFIELD_SIZE = new Dimension((int) (FRAME_SIZE.width * 0.80),50);
    public static final Dimension BUTTON_SIZE = TEXTFIELD_SIZE;
    public static final Dimension RESULT_DIALOG_SIZE = new Dimension((int)(FRAME_SIZE.width*0.33),(int)(FRAME_SIZE.height *0.165));

    //register config
    public static final Dimension REGISTER_LABEL_SIZE = new Dimension(FRAME_SIZE.width,150);

    //login config
    public static final Dimension LOGIN_IMAGE_SIZE = new Dimension(255,262);
    // color configs
    public static final Color PRIMARY_COLOR = new Color(38, 37, 70);
    public static final Color SECONDARY_COLOR = new Color(255, 171, 63);
    public static final Color BUTTON_COLOR = new Color(207,6,0);
}
